package effective_mobile.com.utils.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("code")
    private String code;
}