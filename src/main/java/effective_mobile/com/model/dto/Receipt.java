package effective_mobile.com.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class Receipt {

    private List<Items> items;
    private String sno;

    @Getter
    @Setter
    public static class Items {
        private String name;
        private BigDecimal quantity;
        private BigDecimal sum;
        private String tax;
    }
}