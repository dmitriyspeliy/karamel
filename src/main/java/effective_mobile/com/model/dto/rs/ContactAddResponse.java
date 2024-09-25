package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ContactAddResponse {
    private int result;
    @JsonProperty("time")
    private Time time;

    @Getter
    @Setter
    public static class Time {
        private double start;
        private double finish;
        private double duration;
        private double processing;
        private Date date_start;
        private Date date_finish;
        private int operating_reset_at;
        private double operating;
    }

}

