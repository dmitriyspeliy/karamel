package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "receipt")
public class Receipt {
    @Id
    @Column(name = "deal_id")
    private Long id;

    @Column(name = "ext_receipt_id")
    private String ext_receipt_id;

    @Column(name = "create_date")
    private LocalDateTime localDateTime;

    @Column(name = "link")
    private String link;

    @Column(name = "value")
    private BigDecimal bigDecimal;

    @Column(name = "currency")
    private String currency;

    @Column(name = "add_info")
    private String add_info;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Deal deal;
}
