package effective_mobile.com.sheduler;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.entity.*;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.getShortCityName;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendNotify {

    private final EmailService emailService;
    private final ContactRepository contactRepository;
    private final DealRepository dealRepository;
    private final NotificationRepository notificationRepository;
    private final SmsService smsService;
    private final CityProperties cityProperties;

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
                    Event event = optional.get().getEvent();
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
                        String smsText = textSmsCreator(event, contact);
                        SmsInfo smsInfo = smsService.sendSms(deal.getExtDealId(), contact.getPhone(), smsText);
                        notification.setExtSmsId(smsInfo.getExtSmsId());
                        notification.setTextSms(smsText);
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

    private String textSmsCreator(Event event, Contact contact) throws BadRequestException {
        CityProperties.Info info = cityProperties.getCityInfo().get(getShortCityName(event.getCity()));
        return "Ждём Вас на Карамельную Фабрику по адресу: " + info.getAddress()
                + "\nДата: " + event.getTime().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru")))
                + "\nВремя: " + event.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                + "\nБилеты были отправлены на почту " + contact.getEmail();
    }
}
