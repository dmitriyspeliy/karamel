package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SmsAnswer implements Serializable {
    private String status;
    private Integer status_code;
    private BigDecimal balance;
    private Sms sms;

    @Getter
    @Setter
    public static class Sms {
        private Map<String, Object> details = new LinkedHashMap<>();
        private Info info;

        @JsonAnySetter
        void setDetail(String key, Object value) {
            details.put(key, value);
        }

        @Getter
        @Setter
        public static class Info {
            private String status;
            private Integer status_code;
            private String smsId;
            private String statusText;
            private BigDecimal cost;
        }

    }

}
