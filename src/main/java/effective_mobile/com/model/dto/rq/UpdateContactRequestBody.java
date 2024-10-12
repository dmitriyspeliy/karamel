package effective_mobile.com.model.dto.rq;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContactRequestBody implements Serializable {
    private Long leadId;
    private String email;
}
