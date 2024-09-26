package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BitrixCommonResponse {
    @JsonProperty("result")
    private Integer result;
    @JsonProperty("time")
    private Time time;

    @Getter
    @Setter
    public static class Time {
        private Double start;
        private Double finish;
        private Double duration;
        private Double processing;
        private Date date_start;
        private Date date_finish;
        private Integer operating_reset_at;
        private Double operating;
    }

}

