package effective_mobile.com.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealInfo {
    private String time;
    private String price;
    private String address;
    private Integer adultCount;
    private Integer kidCount;
    private String link;
}