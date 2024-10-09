package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rs.PaymentResult;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final DealRepository dealRepository;
    private final EmailService emailService;
    private final CityProperties cityProperties;
    private final UpdateDealComment updateDealComment;


    @Value("${spring.current-city}")
    private String currentCity;

    public void paymentProcessing(PaymentResult paymentResult) throws BadRequestException {
        Optional<Invoice> optionalInvoice = invoiceRepository.findByExtInvoiceId(paymentResult.getExtId());
        if (optionalInvoice.isPresent()) {
            if (paymentResult.getStatus() == Status.SUCCESS) {
                Deal deal = optionalInvoice.get().getDeal();
                deal.setPaid(true);
                dealRepository.save(deal);
                optionalInvoice.get().setStatus(Status.SUCCESS);
                invoiceRepository.save(optionalInvoice.get());
                emailService.sendEmail(
                        deal.getContact().getEmail(),
                        "Письмо с Карамельной Фабрики Деда Мороза и ваш билет",
                        createMessage(deal));
                // TODO поставить статус оплаты в битриске
                updateDealComment.refreshCommentDeal(
                        deal.getExtDealId(),
                        "Билеты отправлены на почту " + deal.getContact().getEmail());
            } else {
                optionalInvoice.get().setStatus(Status.FAILURE);
                invoiceRepository.save(optionalInvoice.get());
                // TODO: вернуть забронированные места
            }
        } else {
            throw new BadRequestException("Can't find invoice by mockId" + paymentResult.getExtId());
        }
    }

    private String createMessage(Deal deal) throws BadRequestException {

        Resource resource = new ClassPathResource("ticket-email-template.html");
        String emailContent;

        try {
            emailContent = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

        try {

            CityProperties.Info info = cityProperties.getCityInfo().get(currentCity);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

            return emailContent
                    .replace("{email}", deal.getContact().getEmail())
                    .replace("{phone1}", String.join(", ",
                            info.getManagerContactNumbers()))
                    .replace("{vkLink}", info.getVkLink())
                    .replace("{type}", deal.getType())
                    .replace("{date}", deal.getCreateDate().format(formatter))
                    .replace("{time}", deal.getCreateDate().toLocalTime().toString())
                    .replace("{price}", deal.getInvoice().getTotalSum().toString())
                    .replace("{address}", info.getAddress())
                    .replace("{adultCount}", deal.getAdultCount().toString())
                    .replace("{kidCount}", deal.getKidCount().toString());
        } catch (Exception e) {
            throw new BadRequestException("Failed prepare email template", e.getMessage());
        }
    }

}
