package effective_mobile.com.sheduler;

import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.EmailService;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendEmail {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;
    private final UpdateDealComment updateDealComment;

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
            updateDealComment.refreshCommentDeal(deal.getExtDealId(), "Билеты были отправлены на почту " + contact.getEmail() + "\n\n" + msg);
            invoiceRepository.updateCountOfEmailByDealId(deal.getId());
            log.info("Билеты были отправлены на почту " + contact.getEmail());
        }
        log.info("SCHEDULER WAS FINISH");
    }
}
