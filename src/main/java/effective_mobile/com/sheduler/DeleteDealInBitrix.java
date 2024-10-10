package effective_mobile.com.sheduler;

import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.api.deal.DeleteDeal;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.util.List;

/**
 * Поднимаем этот шедулер только на челябинске, чтобы не было работы на всех сессиях
 */
//@Component
@ConditionalOnExpression(
        "'${spring.current-city}'.equals('chel')"
)
@Slf4j
@RequiredArgsConstructor
public class DeleteDealInBitrix {

    private final InvoiceRepository invoiceRepository;
    private final DeleteDeal deleteDeal;

    /**
     * Удаляем сделку с битриска, если статус оплаты FAILURE
     */
    //@Scheduled(fixedDelay = 400000L)
    public void check() throws BadRequestException {
        log.info("Start working scheduler. Delete deal");
        // выгружаем все счета со статусом FAILURE
        List<Invoice> invoiceByStatus = invoiceRepository.getInvoiceByStatus(Status.FAILURE.name());
        for (Invoice byStatus : invoiceByStatus) {
            log.info("Delete deal by extID" + byStatus.getDeal().getExtDealId());
            deleteDeal.deleteByExtId(byStatus.getDeal().getExtDealId());
        }
        log.info("Scheduler was finish");
    }


}
