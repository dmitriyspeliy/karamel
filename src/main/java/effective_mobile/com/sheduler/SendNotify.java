package effective_mobile.com.sheduler;

import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Notification;
import effective_mobile.com.model.entity.SmsInfo;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.NotificationRepository;
import effective_mobile.com.service.EmailService;
import effective_mobile.com.service.SmsService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendNotify {

    private final EmailService emailService;
    private final ContactRepository contactRepository;
    private final DealRepository dealRepository;
    private final NotificationRepository notificationRepository;
    private final SmsService smsService;

    @Async("jobExecutor")
    @Scheduled(cron = "0 0 3 * * *")
    public void sendNotifyToEvent() {
        log.info("START SCHEDULER NOTIFY");
        // выгружаем все контакты, чьи ивенты буду через три дня
        List<Long> afterThreeDays = contactRepository.findAllContactsDealWhenStartEventAfterThreeDays();
        if (afterThreeDays == null || afterThreeDays.isEmpty()) {
            log.info("Контактов на отправку нет");
        } else {
            for (Long id : afterThreeDays) {
                Optional<Deal> optional = dealRepository.findByIdDealWithEventContact(id);
                if (optional.isPresent()) {
                    // отправляем билеты
                    Contact contact = optional.get().getContact();
                    Deal deal = optional.get();
                    String email = contact.getEmail();
                    Notification notification = new Notification();
                    notification.setEmail(email);
                    notification.setSendTimeEmail(LocalDateTime.now());
                    notification.setPhone(contact.getPhone());
                    notification.setStatus("OK");
                    try {
                        String msg = emailService.createMessageFroNotify(deal);
                        notification.setTextEmail(msg);
                        // send email
                        emailService.sendEmail(email,
                                "Письмо с Карамельной Фабрики Деда Мороза и ваш билет",
                                msg);
                        // send sms
                        String text = "Добрый день! Информация про мероприятие "
                                + "https://info-user.exkursiacaramel.ru/" + deal.getId();
                        SmsInfo smsInfo = smsService.sendSms(deal.getExtDealId(), contact.getPhone(),
                                text);
                        notification.setExtSmsId(smsInfo.getExtSmsId());
                        notification.setTextSms(text);
                        notification.setSendTimeSms(smsInfo.getSendTime());
                        notification.setPhone(contact.getPhone());
                    } catch (BadRequestException e) {
                        notification.setTextError(e.getTextException());
                        notification.setStatus("ERROR");
                    }
                    notificationRepository.save(notification);
                }
            }
        }
        log.info("SCHEDULER NOTIFY WAS FINISH");
    }

}
