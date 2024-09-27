package effective_mobile.com.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Receipt {
    private Long dealId;
    private String ext_receipt_id;
    private String sno;
    private LocalDateTime createDate;
    private String link;
    private BigDecimal value;
    private String tax;
    private BigDecimal quantity;
    private String currency;
    private String add_info;
}