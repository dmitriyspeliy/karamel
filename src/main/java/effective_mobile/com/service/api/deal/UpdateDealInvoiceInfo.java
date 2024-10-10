package effective_mobile.com.service.api.deal;

import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDealInvoiceInfo {

    private HttpResponse<JsonNode> contactResponse;
    private String extId;
    private String payLink;
    private LocalDate createPayLink;

    public void refreshStatusDeal(String extId, String payLink, LocalDate createPayLink) throws BadRequestException {
        log.info("Запрос на обновление информации по платежу сделки по айди " + extId);
        checkVar(List.of(extId));
        this.extId = extId;
        this.createPayLink = createPayLink;
        this.payLink = payLink;
        makeRequest();
        answer();
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.update.json")
                    .queryString("id", extId)
                    .queryString("params[REGISTER_SONET_EVENT]", "Y")
                    .queryString("fields[UF_CRM_66A35EF77437E]", payLink) // pay link
                    .queryString("fields[UF_CRM_66A35EF7800D8]", createPayLink
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) // pay link date send
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на обновление информации по платежу. Текст боди : " + e.getMessage());
        }
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            log.info("Сделка успешно обновлена в битрикс");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на обновление информации по платежу. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }
}
