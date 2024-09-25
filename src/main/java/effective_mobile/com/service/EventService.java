package effective_mobile.com.service;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.utils.enums.City;
import effective_mobile.com.utils.enums.SlotType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventService {
    @Value("${bitrix.baseUrl}")
    private String baseUrl;
    @Value("${bitrix.endpoints.getEvents}")
    private String endpoint;

    private static final String IBLOCK_TYPE_ID = "lists";
    private static final String IBLOCK_ID = "29";
    private static final String FILTER_PROPERTY_119 = "93";
    private static final String ELEMENT_ORDER_PROPERTY_113 = "ASC";

    public List<Event> getUpcomingEvents(String city, String type) {
        var response = sentRequest(city, type);
        var elements = response.path("result");
        var slots = new ArrayList<Event>();

        for (var element : elements) {
            var slot = Event.builder()
                    .id(element.path("ID").asLong())
                    .type("test")
                    .name(element.path("NAME").asText())
                    .time(formatData(getValueFromProperty(element, "PROPERTY_113")))
                    .adultPrice(Integer.parseInt(getValueFromProperty(element, "PROPERTY_135")))
                    .kidPrice(Integer.parseInt(getValueFromProperty(element, "PROPERTY_129")))
                    .childAge(Integer.parseInt(getValueFromProperty(element, "PROPERTY_133")))
                    .capacity(Integer.parseInt(getValueFromProperty(element, "PROPERTY_131")))
                    .adultCapacity(Integer.parseInt(getValueFromProperty(element, "PROPERTY_111")))
                    .kidCapacity(Integer.parseInt(getValueFromProperty(element, "PROPERTY_109")))
                    .slotsLeft(Integer.parseInt(getValueFromProperty(element, "PROPERTY_131")))
                    .adultSlotsLeft(Integer.parseInt(getValueFromProperty(element, "PROPERTY_111")))
                    .kidSlotsLeft(Integer.parseInt(getValueFromProperty(element, "PROPERTY_129")))
                    .gatheringType(SlotType.typeFromValue(Integer.parseInt(getValueFromProperty(element, "PROPERTY_125"))))
                    //в слотах не передается этот параметр
                    .adultRequired(true)
                    .city(city)
                    .build();
            slots.add(slot);
        }
        return slots;
    }


    private JsonNode sentRequest(String city, String type) {
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
