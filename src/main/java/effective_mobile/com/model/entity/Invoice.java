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
@NamedEntityGraph(
        name = "invoice_deal_event",
        attributeNodes = {
                @NamedAttributeNode(value = "deal", subgraph = "deal-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "deal-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("event"),
                                @NamedAttributeNode("contact")
                        }
                )
        }
)
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
    @Column(name = "state")
    private String state;
    @Column(name = "count_of_send_ticket")
    private Integer countOfSendTicket;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Deal deal;

}
