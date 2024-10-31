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
import effective_mobile.com.utils.UtilsMethods;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.getShortCityName;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckStatusPayment {

    private final InvoiceRepository invoiceRepository;
    private final DealRepository dealRepository;
    private final GetInvoice getInvoice;
    private final UpdateDealStatus updateDealStatus;
    private final ChangeEventInBitrix changeEventInBitrix;
    private final UtilsMethods utilsMethods;

    /**
     * Синхронизируем статус платежа в бд с робокассой
     */
    @Async("jobExecutor")
    @Scheduled(cron = " 0 0/2 * * * ?")
    public void check() throws BadRequestException {
        log.info("CHECK SUCCESS PAYMENT");
        // выгружаем все счета со статусом PENDING, которые меньше 30 минут находятся в этом статусе
        List<Invoice> invoiceByStatus = invoiceRepository
                .getDealAndEventAndContactByStatusAndCreateTimeMoreThan(Status.PENDING, LocalDateTime.now().minusMinutes(30));
        for (Invoice byStatus : invoiceByStatus) {
            // делаем запрос по каждому инвойсу в робокассу и проверяем изменился ли статус
            Deal deal = byStatus.getDeal();
            Event event = byStatus.getDeal().getEvent();
            PaymentResult paymentResult = getInvoice.checkStatusById(byStatus, getShortCityName(event.getCity()));
            Status status = paymentResult.getStatus();
            String state = paymentResult.getState();
            if (status == Status.SUCCESS) {
                byStatus.setStatus(status);
                byStatus.setState(state);
                deal.setPaid(true);
                invoiceRepository.save(byStatus);
                dealRepository.save(deal);
                updateDealStatus.refreshStatusDeal(deal.getExtDealId(), deal.getType(), event.getCity());
            } else if (status == Status.FAILURE) {
                byStatus.setStatus(status);
                byStatus.setState(state);
                invoiceRepository.save(byStatus);
            }
        }
        log.info("SCHEDULER WAS FINISH");
    }


    /**
     * Все платежи, со статусом PENDING, которые уже 30 минут находятся в этом статусе, будут отменены и места будут откатаны
     */
    @Async("jobExecutor")
    @Scheduled(cron = " 0 0/5 * * * ?")
    public void checkInvoiceStatusAndTime() {
        log.info("SET STATUS FAILURE AND CANSEL BOOKING");
        // выгружаем все счета со статусом PENDING и дата создания которых больше 30 минут
        List<Invoice> invoiceByStatus = invoiceRepository
                .getDealAndEventAndContactByStatusAndCreateTimeLessThan(Status.PENDING, LocalDateTime.now().minusMinutes(30));
        for (Invoice byStatus : invoiceByStatus) {
            Deal deal = byStatus.getDeal();
            Event event = byStatus.getDeal().getEvent();
            byStatus.setStatus(Status.FAILURE);
            invoiceRepository.save(byStatus);
            if (event.getType().contains("ШКОЛЬНЫЕ")) {
                changeEventInBitrix.undoChangingInSchoolEvent(event);
            } else if (event.getType().contains("СБОРНЫЕ")) {
                changeEventInBitrix.undoChangingInMixedEvent(deal.getKidCount(),
                        deal.getAdultCount(), event);
            }

            //делаем сброс в кэше, чтобы след запрос был уже с актуальными местами
            utilsMethods.cleanCashed(event);

        }
        log.info("SCHEDULER WAS FINISH");
    }
}
