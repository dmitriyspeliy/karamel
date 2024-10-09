package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "event")
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ext_event_id")
    private String extEventId;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "adult_price")
    private BigDecimal adultPrice;
    @Column(name = "kid_price")
    private BigDecimal kidPrice;
    @Column(name = "child_age")
    private String childAge;
    @Column(name = "capacity")
    private Long capacity;
    @Column(name = "adult_capacity")
    private Long adultCapacity;
    @Column(name = "kid_capacity")
    private Long kidCapacity;
    @Column(name = "gathering_type")
    private String gatheringType;
    @Column(name = "adult_required")
    private Boolean adultRequired;
    @Column(name = "city")
    private String city;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "event")
    List<Deal> dealList;
}