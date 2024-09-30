package effective_mobile.com.model.dto.rq;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContactRequestBody {
    private Long leadId;
    private String email;
}
