package effective_mobile.com.service.api.payment;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.configuration.properties.RobokassaProperties;
import effective_mobile.com.model.dto.rs.PaymentResult;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.utils.enums.Status;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static effective_mobile.com.utils.UtilsMethods.calculateSignature;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetInvoice {

    private final RobokassaProperties robokassaProperties;
    private final CityProperties cityProperties;

    @Value("${spring.current-city}")
    private String currentCity;
    private String extId;
    private String login;
    private String pass2;
    private HttpResponse<byte[]> response;
    String resultCode;
    String stateCode;
    String info;


    public PaymentResult checkStatusById(Invoice invoice) throws BadRequestException {
        CityProperties.Info.PaymentInfo paymentInfo = cityProperties.getCityInfo().get(currentCity).getPaymentInfo();
        this.extId = invoice.getDeal().getExtDealId();
        this.login = paymentInfo.getLogin();
        this.pass2 = paymentInfo.getRobokassaPass2();
        try {
            makeRequest();
            answerProcessing();
            return defineState();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void makeRequest() throws BadRequestException {
        log.info("Запрос на получение состояние счета по айди " + extId);
        try {
            response = Unirest.get(robokassaProperties.getInvoiceStatus() +
                            "MerchantLogin=" + login +
                            "&InvoiceID=" + extId +
                            "&Signature=" + calculateSignature(login, extId, pass2))
                    .asBytes();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private void answerProcessing() throws BadRequestException, IOException {
        log.info("Response is " + response.getStatus());
        stateCode = null;
        resultCode = null;
        info = null;
        if (response.getStatus() == 200) {
            XmlMapper xmlMapper = new XmlMapper();
            com.fasterxml.jackson.databind.JsonNode node = xmlMapper.readTree(response.getBody());
            // доки https://docs.robokassa.ru/xml-interfaces/#account
            resultCode = node.get("Result").get("Code").asText();
            if (resultCode.equals("0")) {
                stateCode = node.get("State").get("Code").asText();
                info = node.get("Info").toString();
            }
        } else {
            throw new BadRequestException("Статус код операции" + response.getStatus());
        }
    }

    private PaymentResult defineState() {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.setState(info);
        if (stateCode != null && !stateCode.equals("") && stateCode.equals("100")) {
            paymentResult.setStatus(Status.SUCCESS);
            log.info("Счет по номеру " + extId + " в состоянии " + Status.SUCCESS);
        } else if (stateCode != null && !stateCode.equals("") && (stateCode.equals("50") || stateCode.equals("20") || stateCode.equals("5"))) {
            paymentResult.setStatus(Status.PENDING);
            log.info("Счет по номеру " + extId + " в состоянии " + Status.PENDING);
        } else if (stateCode != null && !stateCode.equals("") && (stateCode.equals("10") || stateCode.equals("60") || stateCode.equals("80"))) {
            paymentResult.setStatus(Status.FAILURE);
            log.info("Счет по номеру " + extId + " в состоянии " + Status.FAILURE);
        }
        return paymentResult;
    }
}
