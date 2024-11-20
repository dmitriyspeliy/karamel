package effective_mobile.com.sheduler;

import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.model.entity.Notification;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.repository.NotificationRepository;
import effective_mobile.com.service.EmailService;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.enums.Status;
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
public class SendEmail {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;
    private final UpdateDealComment updateDealComment;
    private final ContactRepository contactRepository;
    private final DealRepository dealRepository;
    private final NotificationRepository notificationRepository;

    @Async("jobExecutor")
    @Scheduled(cron = " 0 0/3 * * * ?")
    public void sendTicket() throws BadRequestException {
        log.info("SEND EMAIL");
        // выгружаем все счета со статусом SUCCESS и счетчиком отправки 0
        List<Invoice> invoiceByStatus = invoiceRepository.getDealAndEventAndContactByStatus(Status.SUCCESS);
        for (Invoice byStatus : invoiceByStatus) {
            Contact contact = byStatus.getDeal().getContact();
            Deal deal = byStatus.getDeal();
            // отправляем билеты
            String msg = emailService.createMessage(deal);
            emailService.sendEmail(contact.getEmail(),
                    "Письмо с Карамельной Фабрики Деда Мороза и ваш билет",
                    msg);
            byStatus.setCountOfSendTicket(byStatus.getCountOfSendTicket() == null ? 1 : byStatus.getCountOfSendTicket() + 1);
            invoiceRepository.save(byStatus);
            updateDealComment.refreshCommentDeal(deal.getExtDealId(), "Билеты были отправлены на почту " + contact.getEmail() + "\n\n" + msg);
            log.info("Билеты были отправлены на почту " + contact.getEmail());
        }
        log.info("SCHEDULER WAS FINISH");
    }

    @Async("jobExecutor")
    @Scheduled(cron = "0 0 3 * * *")
    public void sendNotifyToEvent() {
        log.info("START SCHEDULER NOTIFY");
        // выгружаем все контакты, чьи ивенты буду через три дня
        List<Long> afterThreeDays = contactRepository.findAllContactsDealWhenStartEventAfterThreeDays();
        if(afterThreeDays == null || afterThreeDays.isEmpty()) {
            log.info("Контактов на отправку нет");
        }else {
            for (Long id : afterThreeDays) {
                Optional<Deal> optional = dealRepository.findByIdDealWithEventContact(id);
                if(optional.isPresent()) {
                    // отправляем билеты
                    Contact contact = optional.get().getContact();
                    String email = contact.getEmail();
                    Notification notification = new Notification();
                    notification.setEmail(email);
                    notification.setSendTimeEmail(LocalDateTime.now());
                    notification.setPhone(contact.getPhone());
                    notification.setStatus("OK");
                    try {
                        String msg = emailService.createMessageFroNotify(optional.get());
                        notification.setTextEmail(msg);
                        emailService.sendEmail(email,
                                "Письмо с Карамельной Фабрики Деда Мороза и ваш билет",
                                msg);
                    }catch (BadRequestException e) {
                        notification.setTextError(e.getTextException());
                        notification.setStatus("ERROR");
                    }
                    notificationRepository.save(notification);
                }
            }
        }
        log.info("SCHEDULER WAS FINISH");
    }
}
