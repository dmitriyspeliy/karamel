package effective_mobile.com.service;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.model.dto.rs.GetEventsResponse;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.defineType;
import static effective_mobile.com.utils.UtilsMethods.getValueFromProperty;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private BigDecimal adultPrice;
    private BigDecimal kidPrice;
    private String childAge;
    private Long capacity;
    private Long adultCapacity;
    private Long kidCapacity;
    private String extId;
    private LocalDateTime time;
    private String name;
    private String type;
    private String city;
    private Long id;
    private final CashedEvent cashedEvent;

    private final CacheManager cacheManager;

    public synchronized ResponseEntity<GetEventsResponse> getUpcomingEvents(String city, String type) {
        this.type = type;
        this.city = city;
        var slots = new ArrayList<Event>();
        if (Objects.requireNonNull(cacheManager.getCache("json-nodes")).get(city + type) != null) {
            ArrayList<JsonNode> jsonNodeArrayList = cashedEvent.cashedEvent(city, type);
            for (JsonNode jsonNode : jsonNodeArrayList) {
                var elements = jsonNode.path("result");
                for (var element : elements) {

                    extractValue(element);

                    slots.add(makeEvent());
                }
            }
        } else {
            ArrayList<JsonNode> jsonNodeArrayList = cashedEvent.cashedEvent(city, type);
            for (JsonNode jsonNode : jsonNodeArrayList) {
                var elements = jsonNode.path("result");
                for (var element : elements) {

                    extractValue(element);

                    slots.add(makeEvent());

                    saveToDb();
                }
            }
        }
        return ResponseEntity.ok(new GetEventsResponse(new GetEventsResponse.EventsField(slots)));
    }

    private void extractValue(JsonNode element) {
        adultPrice =
                BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_135")));
        kidPrice =
                BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_129")));
        childAge = getValueFromProperty(element, "PROPERTY_133");
        type = defineType(element);
        if (type.equals("ШКОЛЬНЫЕ ГРУППЫ")) {
            capacity = 1L;
            adultCapacity = 1L;
            kidCapacity = 1L;
        } else {
            capacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_131"));
            adultCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_111"));
            kidCapacity = Long.parseLong(getValueFromProperty(element, "PROPERTY_109"));
        }
        extId = element.path("ID").asText();
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
                .adultPrice(adultPrice.longValue())
                .kidPrice(kidPrice.longValue())
                .childAge(childAge)
                .capacity(capacity)
                .slotsLeft(capacity)
                .kidSlotsLeft(kidCapacity)
                .adultSlotsLeft(adultCapacity)
                .adultCapacity(adultCapacity)
                .kidCapacity(kidCapacity)
                .gatheringType(type) //в слотах не передается этот параметр
                .adultRequired(true)
                .build();
    }

    private void saveToDb() {
        Optional<effective_mobile.com.model.entity.Event> optionalEvent = eventRepository.findByExtEventId(extId);
        effective_mobile.com.model.entity.Event event = new effective_mobile.com.model.entity.Event();
        if (optionalEvent.isPresent()) {
            createEvent(event);
            event.setId(optionalEvent.get().getId());
            event.setExtEventId(optionalEvent.get().getExtEventId());
            event.setCity(optionalEvent.get().getCity());
            event.setName(optionalEvent.get().getName());
        } else {
            event.setExtEventId(extId);
            event.setName(name);
            event.setCity(city);
            createEvent(event);
        }
        eventRepository.save(event);
    }

    private void createEvent(effective_mobile.com.model.entity.Event event) {
        event.setAdultPrice(adultPrice);
        event.setKidPrice(kidPrice);
        event.setChildAge(childAge);
        event.setCapacity(capacity);
        event.setType(type);
        event.setTime(time);
        event.setAdultCapacity(adultCapacity);
        event.setKidCapacity(kidCapacity);
        event.setGatheringType(type);
        event.setAdultRequired(true);
    }

    public effective_mobile.com.model.entity.Event findEventByNameAndCity(String name, String city) throws BadRequestException {
        Optional<effective_mobile.com.model.entity.Event> eventOptional = eventRepository.findByNameAndCity(name, city);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new BadRequestException("No event by name " + name + " and city " + city);
        }
    }


}
