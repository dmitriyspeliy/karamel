package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rs.GetEventsResponse;
import effective_mobile.com.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    @Value("${spring.current-city}")
    private String currentCity;
    private final EventService eventService;
    private final CityProperties cityProperties;

    @GetMapping("/group")
    public ResponseEntity<GetEventsResponse> getUpcomingGroupEvents() {
        log.info("Request received for upcoming group events");
        var cityName = cityProperties.getCityInfo().get(currentCity).getCityName();
        return eventService.getUpcomingEvents(cityName, "Школьные");
    }

    @GetMapping("/mixed")
    public ResponseEntity<GetEventsResponse> getUpcomingMixedEvents() {
        log.info("Request received for upcoming mixed events");
        var cityName = cityProperties.getCityInfo().get(currentCity).getCityName();
        return eventService.getUpcomingEvents(cityName, "Сборные");
    }

}
