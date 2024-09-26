package effective_mobile.com.service;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.service.api.event.FetchAllSlot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.getValueFromProperty;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final FetchAllSlot fetchAllSlot;

    private BigDecimal adultPrice;
    private BigDecimal kidPrice;
    private String childAge;
    private Long capacity;
    private Long adultCapacity;
    private Long kidCapacity;
    private Long slotsLeft;
    private Long adultSlotsLeft;
    private Long kidSlotLeft;
    private Long extId;
    private LocalDateTime time;
    private String name;
    private String type;
    private String city;
    private Long id;

    public List<Event> getUpcomingEvents(String city, String type) {
        this.type = type;
        this.city = city;
        log.info("Fetching upcoming events for city: {} and type: {}", city, type);

//        var response = fetchAllSlot.fetchAllSlotByCityAndType(city, type);
//        var elements = response.path("result");
//        var slots = new ArrayList<Event>();
//
//        for (var element : elements) {
//
//            extractValue(element);
//
//            slots.add(makeEvent());
//
//            saveToDb();
//        }

        //      log.info("Found {} upcoming events for city: {}", slots.size(), city);
//
        Event event = new Event(
                1L,
                "test",
                "test",
                Instant.now(),
                BigDecimal.ONE,
                BigDecimal.ONE,
                "4-6 лет",
                1L,
                1L,
                1L,
                1L,
                1L,
                1L,
                "type",
                true
        );

        return List.of(event);
    }

    private void extractValue(JsonNode element) {
        adultPrice =
                BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_135")));
        kidPrice =
                BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_129")));
        childAge = getValueFromProperty(element, "PROPERTY_133");
        capacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_131"));
        adultCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
        kidCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_109"));
        slotsLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_131"));
        adultSlotsLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
        kidSlotLeft = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
        extId = element.path("ID").asLong();
        name = element.path("NAME").asText();
        time =
                LocalDateTime.parse(getValueFromProperty(element, "PROPERTY_113"),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        id = element.path("ID").asLong();
    }

    private Event makeEvent() {
        return Event.builder()
                .id(id)
                .type(type)
                .name(name)
                .time(time.toInstant(ZoneOffset.of("+00:00")))
                .adultPrice(adultPrice)
                .kidPrice(kidPrice)
                .childAge(childAge)
                .capacity(capacity)
                .adultCapacity(adultCapacity)
                .kidCapacity(kidCapacity)
                .slotsLeft(slotsLeft)
                .adultSlotsLeft(adultSlotsLeft)
                .kidSlotsLeft(kidSlotLeft)
                .gatheringType(type)
                //в слотах не передается этот параметр
                .adultRequired(true)
                .build();
    }

    private void saveToDb() {
        Optional<effective_mobile.com.model.entity.Event> optionalEvent = eventRepository.findByExtEventId(extId);
        if (optionalEvent.isEmpty()) {
            effective_mobile.com.model.entity.Event event = new effective_mobile.com.model.entity.Event();
            event.setExtEventId(extId);
            event.setName(name);
            event.setType(type);
            event.setTime(time);
            event.setAdultPrice(adultPrice);
            event.setKidPrice(kidPrice);
            event.setChildAge(childAge);
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
}
