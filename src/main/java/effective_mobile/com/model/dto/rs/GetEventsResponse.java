package effective_mobile.com.model.dto.rs;

import effective_mobile.com.model.dto.Event;

import java.io.Serializable;
import java.util.List;

public record GetEventsResponse(EventsField events) implements Serializable {

    public record EventsField(List<Event> events) implements Serializable {

    }

}