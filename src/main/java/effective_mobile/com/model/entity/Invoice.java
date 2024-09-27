package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Column(name = "signature1")
    private String signature1;
    @Column(name = "signature2")
    private String signature2;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Deal deal;
    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private InvoiceInfo invoiceInfo;
}
