package effective_mobile.com.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventResponse {
    @JsonProperty("events")
    private List<Event> events;
}
