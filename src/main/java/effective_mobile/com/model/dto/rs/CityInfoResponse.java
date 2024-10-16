package effective_mobile.com.model.dto.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class CityInfoResponse implements Serializable {
    private String city;
    private Instant startEndpoint;
    private Instant finishEndpoint;
    private String address;
    private String managerContactNumber;
    private String offerLink;
}