package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "deal")
@NamedEntityGraph(
        name = "deal_contact",
        attributeNodes = {
                @NamedAttributeNode(value = "contact"),
        }
)
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ext_deal_id")
    private String extDealId;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "add_info")
    private String addInfo;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "kid_count")
    private Integer kidCount;

    @Column(name = "kid_price")
    private BigDecimal kidPrice;

    @Column(name = "kid_age")
    private String kidAge;

    @Column(name = "adult_count")
    private Integer adultCount;

    @Column(name = "adult_price")
    private BigDecimal adultPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;

    @OneToOne(mappedBy = "deal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

}
