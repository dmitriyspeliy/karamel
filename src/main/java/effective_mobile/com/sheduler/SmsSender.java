package effective_mobile.com.sheduler;

import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.SmsService;
import effective_mobile.com.utils.enums.CityInfo;
import effective_mobile.com.utils.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class SmsSender {

    private final InvoiceRepository invoiceRepository;
    private final SmsService smsService;

    @Async("jobExecutor")
    @Scheduled(cron = " 0 0/3 * * * ?")
    public void sendSmsForSuccessPayment() {
        log.info("SEND SMS");
        // выгружаем все счета со статусом SUCCESS и счетчиком отправки sms 0, которые меньше 30 минуты в этом статусе
        List<Invoice> invoiceByStatus = invoiceRepository.getDealAndEventAndContactByStatusSms(Status.SUCCESS, LocalDateTime.now().minusMinutes(30));
        for (Invoice byStatus : invoiceByStatus) {
            Contact contact = byStatus.getDeal().getContact();
            Deal deal = byStatus.getDeal();
            String linkToSmsAction = CityInfo.getLinkToSmsAction(byStatus.getDeal().getEvent().getCity());
            String sms = "Предоплата за экскурсию получена. Дальнейшие действия тут " + linkToSmsAction;
            // отправляем sms
            smsService.sendSms(deal.getExtDealId(), contact.getPhone(), sms);
            byStatus.setCountOfSendSms(1);
            invoiceRepository.save(byStatus);
        }
        log.info("SCHEDULER WAS FINISH");
    }

}
