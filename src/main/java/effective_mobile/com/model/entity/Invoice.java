package effective_mobile.com.model.entity;

import effective_mobile.com.utils.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "invoice")
public class Invoice {
    @Id
    @Column(name = "deal_id")
    private Long dealId;
    @Column(name = "ext_invoice_id")
    private String extInvoiceId;
    @Column(name = "body")
    private String body;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "invoice_link")
    private URL invoiceLink;
    @Column(name = "total_sum")
    private BigDecimal totalSum;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Deal deal;

}
