package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.model.dto.EventResponse;
import effective_mobile.com.model.dto.rs.GetEventResponse;
import effective_mobile.com.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    @Value("${spring.current-city}")
    private String currentCity;
    private final EventService eventService;
    private final CityProperties cityProperties;

    @GetMapping("/group")
    public GetEventResponse getUpcomingGroupEvents() {
        var cityName = cityProperties.getCityInfo().get(currentCity).getCityName();
        var events = eventService.getUpcomingEvents(cityName, "Школьный");
        var response = new EventResponse();
        var eventsWrapper = new EventResponse.EventsWrapper();
        eventsWrapper.setEvents(events);

        var eventFields = new GetEventResponse.EventsField(toEvent(events));

        response.setEvents(eventsWrapper);
        return new GetEventResponse(eventFields);
    }

    @GetMapping("/mixed")
    public EventResponse getUpcomingMixedEvents() {
        var cityName = cityProperties.getCityInfo().get(currentCity).getCityName();
        var events = eventService.getUpcomingEvents(cityName, "Сборная");
        var response = new EventResponse();
        var eventsWrapper = new EventResponse.EventsWrapper();
        eventsWrapper.setEvents(events);

        response.setEvents(eventsWrapper);
        return response;
    }

    private List<GetEventResponse.Event> toEvent(List<Event> events) {
        var list = new ArrayList<GetEventResponse.Event>();
        for (var event : events) {
            var newEvent = new GetEventResponse.Event(
                    event.getId(),
                    event.getName(),
                    event.getType(),
                    event.getTime(),
                    event.getAdultPrice(),
                    event.getKidPrice(),
                    event.getChildAge(),
                    event.getCapacity(),
                    event.getAdultCapacity(),
                    event.getKidCapacity(),
                    event.getSlotsLeft(),
                    event.getAdultSlotsLeft(),
                    event.getKidSlotsLeft(),
                    event.getGatheringType(),
                    event.getAdultRequired(),
                    event.getCity()
            );
            list.add(newEvent);
        }
        return list;
    }

}
