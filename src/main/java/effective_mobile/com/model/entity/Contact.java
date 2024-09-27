package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "contact")
public class Contact {
    @Id
    @Column(name = "deal_id")
    private Long dealId;

    @Column(name = "ext_contact_id")
    private String extContactId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "city")
    private String city;

    @Column(name = "phone")
    private String phone;

    @Column(name = "add_info")
    private String addInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "deal_id")
    private Deal deal;
}
