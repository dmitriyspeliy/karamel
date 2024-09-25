package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "deal")
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ext_deal_id")
    private Long extDealId;

    @Column(name = "title")
    private String title;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "add_info")
    private String addInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    Contact contact;

    @OneToOne(mappedBy = "deal", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Receipt receipt;
}
