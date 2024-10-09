package effective_mobile.com.model.dto.rs;

import effective_mobile.com.utils.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentResult implements Serializable {
    private String extId;
    private Status status;
}
