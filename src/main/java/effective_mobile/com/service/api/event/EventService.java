package effective_mobile.com.service.api.event;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.utils.enums.City;
import effective_mobile.com.utils.enums.SlotType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    @Value("${bitrix.baseUrl}")
    private String baseUrl;
    @Value("${bitrix.endpoints.getEvents}")
    private String endpoint;
    private final EventRepository eventRepository;

    private static final String IBLOCK_TYPE_ID = "lists";
    private static final String IBLOCK_ID = "29";
    private static final String FILTER_PROPERTY_119 = "93";
    private static final String ELEMENT_ORDER_PROPERTY_113 = "ASC";

    public List<Event> getUpcomingEvents(String city, String type) {
        log.info("Fetching upcoming events for city: {} and type: {}", city, type);

        var response = sentRequest(city, type);
        var elements = response.path("result");
        var slots = new ArrayList<Event>();

        for (var element : elements) {
            BigDecimal adultPrice =
                    BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_135")));
            BigDecimal kidPrice =
                    BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_129")));
            String childAge = getValueFromProperty(element, "PROPERTY_133");
            Long capacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_131"));
            Long adultCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
            Long kidCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_109"));
            Long slotsLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_131"));
            Long adultSlotsLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
            Long kidSlotLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
            Long extId = element.path("ID").asLong();
            String name = element.path("NAME").asText();
            String time = formatData(getValueFromProperty(element, "PROPERTY_113"));

            var slot = Event.builder()
                    .id(element.path("ID").asLong())
                    .type(type)
                    .name(element.path("NAME").asText())
                    .time(Instant.parse(time))
                    .adultPrice(adultPrice)
                    .kidPrice(kidPrice)
                    .childAge(childAge.split("")[0])
                    .capacity(capacity)
                    .adultCapacity(adultCapacity)
                    .kidCapacity(kidCapacity)
                    .slotsLeft(slotsLeft)
                    .adultSlotsLeft(adultSlotsLeft)
                    .kidSlotsLeft(kidSlotLeft)
                    .gatheringType(type)
                    //в слотах не передается этот параметр
                    .adultRequired(true)
                    .city(city)
                    .build();
            slots.add(slot);

            Optional<effective_mobile.com.model.entity.Event> optionalEvent = eventRepository.findByExtEventId(extId);
            if (optionalEvent.isEmpty()) {
                effective_mobile.com.model.entity.Event event = new effective_mobile.com.model.entity.Event();
                event.setExtEventId(extId);
                event.setName(name);
                event.setType(type);
                event.setTime(LocalDateTime.ofInstant(Instant.parse(time), ZoneId.of("GMT+0")));
                event.setAdultPrice(adultPrice);
                event.setKidPrice(kidPrice);
                event.setChildAge(Long.parseLong(childAge.split("")[0]));
                event.setCapacity(capacity);
                event.setAdultCapacity(adultCapacity);
                event.setKidCapacity(kidCapacity);
                event.setSlotsLeft(slotsLeft);
                event.setAdultSlotsLeft(adultSlotsLeft);
                event.setGatheringType(type);
                event.setAdultRequired(true);
                event.setCity(city);
                event.setKidSlotsLeft(kidSlotLeft);
                eventRepository.save(event);
                log.info("Save event in db");
            }
        }

        log.info("Found {} upcoming events for city: {}", slots.size(), city);

        return slots;
    }


    private JsonNode sentRequest(String city, String type) {
        log.info("Sending request to Bitrix API for city: {} and type: {}", city, type);

        var restTemplate = new RestTemplate();
        var codeOfCity = City.getCodeOfCity(city);
        var codeOfType = SlotType.getCodeOfType(type);
        var now = LocalDateTime.now();

        var hookWithAdditionalParams = baseUrl + endpoint
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&FILTER[PROPERTY_119]=" + FILTER_PROPERTY_119
                + "&ELEMENT_ORDER[PROPERTY_113]=" + ELEMENT_ORDER_PROPERTY_113
                + "&FILTER[PROPERTY_123]=" + codeOfCity
                + "&FILTER[PROPERTY_125]=" + codeOfType
                + "&FILTER[PROPERTY_113]>" + now;

        return restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);
    }

    private String getValueFromProperty(JsonNode node, String property) {
        try {
            var entrySet = node.path(property);
            var key = entrySet.fieldNames().next();
            return entrySet.path(key).asText();
        } catch (NoSuchElementException e) {
            log.warn("Property {} not found in node, returning default value '0'", property);
            return "0";
        }
    }

    private String formatData(String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(time, inputFormatter);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);
        String formattedDateTime = offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        formattedDateTime = formattedDateTime.replace("+00:00", "Z");

        return formattedDateTime;
    }
}
