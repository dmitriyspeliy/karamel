package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import effective_mobile.com.model.dto.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventResponse {
    @JsonProperty("events")
    private EventsWrapper events;

    @Getter
    @Setter
    public static class EventsWrapper {
        @JsonProperty("events")
        private List<Event> events;
    }
}
