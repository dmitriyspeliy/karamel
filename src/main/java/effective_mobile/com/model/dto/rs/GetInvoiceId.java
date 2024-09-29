package effective_mobile.com.model.dto.rs;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetInvoiceId implements Serializable {
    private String hashId;
    private String errorCode;
    private String errorMessage;
}
