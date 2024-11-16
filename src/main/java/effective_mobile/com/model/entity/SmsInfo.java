package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sms_info")
public class SmsInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ext_sms_id")
    private String extSmsId;
    @Column(name = "phone_receiver")
    private String phoneReceiver;
    @Column(name = "sms_text")
    private String smsText;
    @Column(name = "status")
    private String status;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "status_text")
    private String statusText;
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    @Column(name = "status_time")
    private LocalDateTime statusTime;
    @Column(name = "cost")
    private BigDecimal cost;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "deal_ext_id")
    private String dealExtId;
}