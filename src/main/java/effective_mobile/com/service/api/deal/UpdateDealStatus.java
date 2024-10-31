package effective_mobile.com.service.api.deal;

import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.enums.CityInfo;
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
public class UpdateDealStatus {

    private HttpResponse<JsonNode> contactResponse;
    private String extId;
    private String type;
    private String city;

    public void refreshStatusDeal(String extId, String type, String city) throws BadRequestException {
        log.info("Запрос на обновление статуса платежа сделки по айди " + extId);
        checkVar(List.of(extId));
        this.extId = extId;
        this.type = type;
        this.city = city;
        makeRequest();
        answer();
    }

    private void makeRequest() throws BadRequestException {
        String typeOfSection = CityInfo.getCitySection(city);
        log.info("Город: " + city);
        if (type.equals("СБОРНЫЕ ГРУППЫ")) {
            try {
                contactResponse
                        = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.update.json")
                        .queryString("id", extId)
                        .queryString("params[REGISTER_SONET_EVENT]", "Y")
                        .queryString("fields[STAGE_ID]", typeOfSection)
                        .queryString("fields[UF_CRM_66A35EF7675A3]", "121") // оплата мест 121 yes 123 no
                        .asJson();
            } catch (Exception e) {
                throw new BadRequestException("Не удалось отправить запрос на обновление статуса платежа. Текст боди : " + e.getMessage());
            }
        } else {
            try {
                contactResponse
                        = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.update.json")
                        .queryString("id", extId)
                        .queryString("params[REGISTER_SONET_EVENT]", "Y")
                        .queryString("fields[STAGE_ID]", typeOfSection)
                        .queryString("fields[UF_CRM_66A35EF7571B0]", "117") // оплата бронь 117 yes 119 no
                        .asJson();
            } catch (Exception e) {
                throw new BadRequestException("Не удалось отправить запрос на обновление статуса платежа. Текст боди : " + e.getMessage());
            }
        }
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            log.info("Сделка успешно обновлена в битрикс");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на обновление сделки. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }
}
