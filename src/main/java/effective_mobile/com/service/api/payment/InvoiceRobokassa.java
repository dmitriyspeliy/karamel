package effective_mobile.com.service.api.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.configuration.properties.RobokassaProperties;
import effective_mobile.com.model.dto.rs.GetInvoiceId;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static effective_mobile.com.utils.UtilsMethods.calculateSignature;
import static effective_mobile.com.utils.UtilsMethods.createUrlEncodedReceipt;

@Component
@Slf4j
@RequiredArgsConstructor
public class InvoiceRobokassa {

    private final CityProperties cityProperties;
    private final RobokassaProperties robokassaProperties;
    private final InvoiceRepository invoiceRepository;

    @Value("${spring.current-city}")
    private String currentCity;

    private CityProperties.Info.PaymentInfo paymentInfo;
    private Deal deal;
    private BigDecimal sum;
    private String receipt;
    private String body;
    private HttpResponse<JsonNode> response;
    private String hashId;
    private Invoice invoice;

    public Invoice generateInvoiceLink(BigDecimal sum, Deal deal) throws BadRequestException, MalformedURLException {
        this.paymentInfo = cityProperties.getCityInfo().get(currentCity).getPaymentInfo();
        this.deal = deal;
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);

        makeReceipt();
        makeBody();
        makeRequest();
        answerProcessing();
        createInvoice();
        return invoice;
    }

    private void makeReceipt() throws BadRequestException {
        try {
            receipt = createUrlEncodedReceipt(
                    sum,
                    "Оплата предварительной брони %s %s".formatted(
                            deal.getTitle(),
                            deal.getExtDealId()),
                    paymentInfo.getSno(),
                    paymentInfo.getTax(),
                    BigDecimal.ONE);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void makeBody() {
        body = "MerchantLogin=" + paymentInfo.getLogin()
                + "&OutSum=" + sum.toString()
                + "&InvId=" + deal.getExtDealId()
                + "&Receipt=" + URLEncoder.encode(receipt, StandardCharsets.UTF_8)
                + "&ExpirationDate=" + DateTimeFormatter.ISO_ZONED_DATE_TIME.format(
                ZonedDateTime.ofInstant(Instant.now().plus(Duration.ofHours(24)), ZoneOffset.UTC))
                + "&SignatureValue=" + calculateSignature(paymentInfo.getLogin(), sum, deal.getExtDealId(), receipt, paymentInfo.getRobokassaPass1())
                + "&Culture=ru";
    }

    private void makeRequest() throws BadRequestException {
        log.info("Запрос на получение айди счета");
        try {
            response = Unirest.post(robokassaProperties.getIdUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED.toString())
                    .body(body)
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void answerProcessing() throws BadRequestException {
        if (response.getStatus() == 200) {
            GetInvoiceId getInvoiceId = new Gson().fromJson(response.getBody().toString(), GetInvoiceId.class);
            //https://docs.robokassa.ru/pay-interface/#errors
            if (getInvoiceId.getErrorCode().equals("0") && getInvoiceId.getInvoiceID() != null
                    && !getInvoiceId.getInvoiceID().equals("")) {
                log.info("Айди получен " + hashId);
                hashId = getInvoiceId.getInvoiceID();
            } else {
                throw new BadRequestException("Не удалось получить айди счета. Код ошибки " + getInvoiceId.getErrorCode() + ". Сообщение: "
                        + getInvoiceId.getErrorMessage());
            }
        } else {
            throw new BadRequestException("Не удалось отправить запрос получение айди счета. Статус код " + response.getStatus()
                    + ". Боди " + response.getBody());
        }
    }

    private void createInvoice() throws MalformedURLException {
        invoice = new Invoice();
        invoice.setTotalSum(sum);
        invoice.setExtInvoiceId(hashId);
        invoice.setDeal(deal);
        invoice.setBody(body);
        invoice.setDealId(deal.getId());
        invoice.setCreateAt(LocalDateTime.now());
        invoice.setInvoiceLink(new URL(robokassaProperties.getInvoiceUrl() + hashId));
        invoice.setStatus(Status.PENDING);
        invoice.setCountOfSendTicket(0);
        invoice = invoiceRepository.save(invoice);
        log.info("Счет сохранен в бд");
    }


}
