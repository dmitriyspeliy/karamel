package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "event")
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ext_event_id")
    private Long extEventId;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "time")
    private String time;
    @Column(name = "adult_price")
    private Integer adultPrice;
    @Column(name = "kid_price")
    private Integer kidPrice;
    @Column(name = "child_age")
    private Integer childAge;
    @Column(name = "capacity")
    private Integer capacity;
    @Column(name = "adult_capacity")
    private Integer adultCapacity;
    @Column(name = "kid_capacity")
    private Integer kidCapacity;
    @Column(name = "slots_left")
    private Integer slotsLeft;
    @Column(name = "adult_slots_left")
    private Integer adultSlotsLeft;
    @Column(name = "kid_slots_left")
    private Integer kidSlotsLeft;
    @Column(name = "gathering_type")
    private String gatheringType;
    @Column(name = "adult_required")
    private Boolean adultRequired;
    @Column(name = "city")
    private String city;

}