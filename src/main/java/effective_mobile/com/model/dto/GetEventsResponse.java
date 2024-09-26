package effective_mobile.com.model.dto;

import java.util.List;

public record GetEventsResponse(EventsField events) {

    public record EventsField(List<Event> events) {

    }

}