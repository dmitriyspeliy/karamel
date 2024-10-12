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

    /**
     * Возвращает информацию про школьные группы
     */
    @GetMapping("/group")
    public ResponseEntity<GetEventsResponse> getUpcomingGroupEvents() {
        return eventService.getUpcomingEvents(cityProperties.getCityInfo().get(currentCity).getCityName(), "Школьные");
    }

    /**
     * Возвращает информацию про сборные группы
     */
    @GetMapping("/mixed")
    public ResponseEntity<GetEventsResponse> getUpcomingMixedEvents() {
        return eventService.getUpcomingEvents(cityProperties.getCityInfo().get(currentCity).getCityName(), "Сборные");
    }

}
