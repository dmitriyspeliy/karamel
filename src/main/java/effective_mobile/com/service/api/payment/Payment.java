package effective_mobile.com.service.api.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import effective_mobile.com.configuration.properties.PaymentProperties;
import effective_mobile.com.model.dto.BookingCreationResult;
import effective_mobile.com.model.dto.Receipt;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.model.entity.InvoiceInfo;
import effective_mobile.com.repository.InvoiceInfoRepository;
import effective_mobile.com.repository.InvoiceRepository;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.MultiValueMapAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class Payment {

    private final ObjectMapper objectMapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceInfoRepository invoiceInfoRepository;
    private final PaymentProperties paymentProperties;


    public BookingCreationResult pay(Contact contact, Deal deal, RequestToBookingEvent requestToBookingEvent) {

        InvoiceInfo invoiceInfo = createInvoice(
                deal.getExtDealId(),
                BigDecimal.ONE,
                "Оплата предварительной брони %s %s".formatted(
                        requestToBookingEvent.getEventName(),
                        requestToBookingEvent.getDate()),
                Duration.ofHours(24L),
                1L,
                1L,
                1L,
                1L,
                true,
                contact.getExtContactId());

        return new BookingCreationResult(
                deal.getExtDealId(),
                invoiceInfo.getHash(),
                invoiceInfo.getInvoiceLink());
    }

    private InvoiceInfo createInvoice(
            String leadId,
            BigDecimal payment,
            String description,
            Duration expiresIn,
            Long successStatusId,
            Long failureStatusId,
            Long kidSlotsCount,
            Long adultSlotsCount,
            boolean isGroupEvent,
            String contactId) {

        String urlEncodedReceipt = null;
        String signature;

        final InvoiceInfo result = invoiceInfoRepository.save(new InvoiceInfo());
        final Invoice invoice = invoiceRepository.save(new Invoice());

        try {
            urlEncodedReceipt = createUrlEncodedReceipt(payment, description);
            signature = calculateSignature(payment, urlEncodedReceipt, invoice.getExtInvoiceId());
        } catch (JsonProcessingException e) {
            log.error("Couldn't generate receipt string", e);
            signature = calculateSignature(payment, invoice.getExtInvoiceId());
        }
        final MultiValueMapAdapter<String, String> multiValueMap = new MultiValueMapAdapter<>(new HashMap<>(Map.of(
                "MerchantLogin", List.of(paymentProperties.getMerchantName()),
                "OutSum", List.of(payment.setScale(2, RoundingMode.HALF_UP).toPlainString()),
                "InvId", List.of(String.valueOf(invoice.getExtInvoiceId())),
                "ExpirationDate", List.of(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
                        ZonedDateTime.ofInstant(Instant.now().plus(expiresIn), ZoneOffset.UTC))
                ),
                "SignatureValue", List.of(signature),
                "Culture", List.of("ru"))));

        if (urlEncodedReceipt != null) {
            multiValueMap.put("Receipt", List.of(urlEncodedReceipt));
        }

        HttpResponse<JsonNode> httpRequest =
                Unirest.post(paymentProperties.getAcquiringServiceHost() + paymentProperties.getAcquiringServicePaymentBaseUrl())
                        .contentType("application/x-www-form-urlencoded")
                        .body(multiValueMap)
                        .asJson();

        result.setInvoice(invoice);
        result.getInvoice().setExtInvoiceId(httpRequest.getBody().getObject().get("invoiceID").toString());
        result.getInvoice().setSignature1(signature);
        result.getInvoice().setSignature2(calculateSignature2(invoice.getExtInvoiceId()));
        result.setFailureStatusId(failureStatusId);
        result.setSuccessStatusId(successStatusId);
        result.setStatus(InvoiceInfo.Status.PENDING);
        result.setBitrixUrl("");
        try {
            result.setInvoiceLink(new URL("https://auth.robokassa.ru/Merchant/Index/" + result.getInvoice().getExtInvoiceId()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private String createUrlEncodedReceipt(BigDecimal payment, String name) throws JsonProcessingException {
        return URLEncoder.encode(objectMapper.writeValueAsString(create(new BookingReceiptInfo(
                payment,
                name))), StandardCharsets.UTF_8);
    }

    public Receipt create(BookingReceiptInfo bookingReceiptInfo) {
        Receipt receipt = new Receipt();
        receipt.setValue(bookingReceiptInfo.sum);
        receipt.setCreateDate(LocalDateTime.now());
        receipt.setSno(paymentProperties.getSno());
        receipt.setAdd_info(bookingReceiptInfo.name());
        receipt.setTax(paymentProperties.getTax());
        receipt.setQuantity(BigDecimal.ONE);
        return receipt;
    }

    public record BookingReceiptInfo(BigDecimal sum, String name) {

    }

    private String calculateSignature(BigDecimal payment, String urlEncodedReceipt, String invoiceId) {
        return DigestUtils.md5DigestAsHex(String.join(
                ":",
                paymentProperties.getMerchantName(),
                payment.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                String.valueOf(invoiceId),
                urlEncodedReceipt,
                paymentProperties.getPassword()).getBytes());

    }

    private String calculateSignature(BigDecimal payment, String invoiceId) {
        return DigestUtils.md5DigestAsHex(String.join(
                ":",
                paymentProperties.getMerchantName(),
                payment.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                String.valueOf(invoiceId),
                paymentProperties.getPassword()).getBytes());
    }

    private String calculateSignature2(String invoiceId) {
        return DigestUtils.md5DigestAsHex(String.join(
                ":",
                paymentProperties.getMerchantName(),
                String.valueOf(invoiceId),
                paymentProperties.getPassword2()).getBytes());
    }
}
