package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BitrixResponseDeleteDeal implements Serializable {
    @JsonProperty("result")
    private Boolean result;
    @JsonProperty("time")
    private Time time;

    @Getter
    @Setter
    public static class Time {
        public Double start;
        public Double finish;
        public Double duration;
        public Double processing;
        public Date date_start;
        public Date date_finish;
        public Integer operating_reset_at;
        public Double operating;
    }
}