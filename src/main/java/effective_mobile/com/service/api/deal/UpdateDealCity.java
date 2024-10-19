package effective_mobile.com.service.api.deal;

import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDealCity {

    private HttpResponse<JsonNode> contactResponse;
    private String city;
    private String extId;

    public void refreshDealCity(String extId, String city) throws BadRequestException {
        log.info("Запрос на обновление города сделки по айди " + extId);
        checkVar(List.of(extId, city));
        this.extId = extId;
        this.city = city;
        makeRequest();
        answer();
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.update.json")
                    .queryString("id", extId)
                    .queryString("fields[UF_CRM_66A35EF5A2449]", city)
                    .queryString("params[REGISTER_SONET_EVENT]", "Y")
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на обновление города сделки. Текст боди : " + e.getMessage());
        }
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            log.info("Сделка успешно обновлена в битрикс");
            return;
        }
        throw new BadRequestException("Не удалось отправить запрос на обновление города сделки. Статус код " + contactResponse.getStatus()
                + ". Боди " + contactResponse.getBody());
    }
}
