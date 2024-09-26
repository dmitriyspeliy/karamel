package effective_mobile.com.model.dto.rs;

import effective_mobile.com.model.dto.Event;

import java.util.List;

public record GetEventsResponse(EventsField events) {

    public record EventsField(List<Event> events) {

    }

}