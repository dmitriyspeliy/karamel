package effective_mobile.com.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "ext_sms_id")
    private String extSmsId;
    @Column(name = "text_sms")
    private String textSms;
    @Column(name = "text_email")
    private String textEmail;
    @Column(name = "text_error")
    private String textError;
    @Column(name = "status")
    private String status;
    @Column(name = "send_time_sms")
    private LocalDateTime sendTimeSms;
    @Column(name = "send_time_email")
    private LocalDateTime sendTimeEmail;
}