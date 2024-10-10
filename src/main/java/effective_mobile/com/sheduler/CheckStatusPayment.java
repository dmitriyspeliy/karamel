package effective_mobile.com.sheduler;

import effective_mobile.com.model.dto.rs.PaymentResult;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.EmailService;
import effective_mobile.com.service.api.payment.GetInvoice;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Поднимаем этот шедулер только на челябинске, чтобы не было работы на всех сессиях
 */
@Component
@ConditionalOnExpression(
        "'${spring.current-city}'.equals('chel')"
)
@Slf4j
@RequiredArgsConstructor
public class CheckStatusPayment {

    private final InvoiceRepository invoiceRepository;
    private final DealRepository dealRepository;
    private final GetInvoice getInvoice;
    private final EmailService emailService;

    @Scheduled(fixedRate = 300000L)
    public void check() throws BadRequestException {
        log.info("Start working scheduler. Check invoice's with status PENDING");
        // выгружаем все счета со статусом PENDING
        List<Invoice> invoiceByStatus = invoiceRepository.getInvoiceByStatus(Status.PENDING.name());
        for (Invoice byStatus : invoiceByStatus) {
            // делаем запрос по каждому инвойсу в робокассу и проверяем изменился ли статус
            PaymentResult paymentResult = getInvoice.checkStatusById(byStatus);
            Status status = paymentResult.getStatus();
            String state = paymentResult.getState();
            Deal deal = byStatus.getDeal();
            if (status == Status.SUCCESS) {
                byStatus.setStatus(status);
                byStatus.setState(state);
                deal.setPaid(true);
                invoiceRepository.save(byStatus);
                dealRepository.save(deal);
            } else if (status == Status.FAILURE) {
                byStatus.setStatus(status);
                byStatus.setState(state);
                invoiceRepository.save(byStatus);
            }
        }
        log.info("Scheduler was finish");
    }

    @Scheduled(fixedRate = 300000L)
    public void sendTicket() throws BadRequestException {
        log.info("Start working scheduler. Send tickets");
        // выгружаем все счета со статусом SUCCESS и счетчиком отправки 0
        List<Invoice> invoiceByStatus = invoiceRepository.getInvoiceByStatusAndCountOfSendTicket(Status.SUCCESS.name());
        for (Invoice byStatus : invoiceByStatus) {
            Contact contact = byStatus.getDeal().getContact();
            Deal deal = byStatus.getDeal();
            // отправляем билеты
            log.info("Оплата по счету " + byStatus.getExtInvoiceId() + " прошла успешно. Отправляем билеты");
            emailService.sendEmail(contact.getEmail(),
                    "Письмо с Карамельной Фабрики Деда Мороза и ваш билет",
                    emailService.createMessage(deal));
            log.info("Билеты были отправлены на почту " + contact.getEmail());
            byStatus.setCountOfSendTicket(byStatus.getCountOfSendTicket() + 1);
            invoiceRepository.save(byStatus);
        }
        log.info("Scheduler was finish");
    }
}
