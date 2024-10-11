package effective_mobile.com.sheduler;

import effective_mobile.com.model.dto.rs.PaymentResult;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.api.deal.UpdateDealStatus;
import effective_mobile.com.service.api.event.ChangeEventInBitrix;
import effective_mobile.com.service.api.payment.GetInvoice;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private final UpdateDealStatus updateDealStatus;
    private final ChangeEventInBitrix changeEventInBitrix;

    /**
     * Синхронизируем статус платежа в бд с робокассой
     */
    @Scheduled(cron = " 0 0/2 * * * ?")
    public synchronized void check() throws BadRequestException {
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
                updateDealStatus.refreshStatusDeal(deal.getExtDealId(), deal.getType());
            } else if (status == Status.FAILURE) {
                byStatus.setStatus(status);
                byStatus.setState(state);
                invoiceRepository.save(byStatus);
            }
        }
        log.info("Scheduler was finish");
    }


    /**
     * Все платежи, со статусом PENDING, которые уже 15 минут находятся в этом статусе, будут отменены
     */
    @Scheduled(cron = " 0 0/5 * * * ?")
    public synchronized void checkInvoiceStatusAndTime() {
        log.info("Start working scheduler. Set FAILURE status in invoice");
        // выгружаем все счета со статусом PENDING
        List<Invoice> invoiceByStatus = invoiceRepository.getInvoiceByStatus(Status.PENDING.name());
        for (Invoice byStatus : invoiceByStatus) {
            Deal deal = byStatus.getDeal();
            Event event = byStatus.getDeal().getEvent();
            if (byStatus.getCreateAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
                byStatus.setStatus(Status.FAILURE);
                invoiceRepository.save(byStatus);
                if (event.getType().contains("ШКОЛЬНЫЕ")) {
                    changeEventInBitrix.undoChangingInSchoolEvent(event);
                } else if (event.getType().contains("СБОРНЫЕ")) {
                    changeEventInBitrix.undoChangingInMixedEvent(deal.getKidCount(),
                            deal.getAdultCount(), event);
                }
            }
        }
        log.info("Scheduler was finish");
    }
}
