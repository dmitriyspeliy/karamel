package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.EventResponse;
import effective_mobile.com.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    @Value("${spring.current-city}")
    private String currentCity;
    private final EventService eventService;
    private final CityProperties cityProperties;

    @GetMapping("/group")
    public EventResponse getUpcomingGroupEvents() {
        var cityName = cityProperties.getCityInfo().get(currentCity).getCityName();
        var events = eventService.getUpcomingEvents(cityName, "Школьные");
        var response = new EventResponse();
        var eventsWrapper = new EventResponse.EventsWrapper();
        eventsWrapper.setEvents(events);

        response.setEvents(eventsWrapper);
        return response;
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

}
