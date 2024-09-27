package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "invoice_info")
public class InvoiceInfo {
    @Id
    @Column(name = "deal_id")
    private Long invoiceId;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "hash")
    private String hash;
    @Column(name = "success_status_id")
    private Long successStatusId;
    @Column(name = "failure_status_id")
    private Long failureStatusId;
    @Column(name = "invoice_link")
    private URL invoiceLink;
    @Column(name = "create_at")
    private LocalDateTime createdAt;
    @Column(name = "bitrix_url")
    private String bitrixUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Invoice invoice;

    public enum Status {
        PENDING, SUCCESS, FAILURE
    }
}