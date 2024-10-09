package effective_mobile.com.service.api.deal;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rs.BitrixCommonResponse;
import effective_mobile.com.model.dto.rs.BitrixResponseDeleteDeal;
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
public class DeleteDeal {
    private BitrixResponseDeleteDeal bitrixCommonResponse;
    private HttpResponse<JsonNode> contactResponse;
    private String extId;

    public void deleteByExtId(String extId) throws BadRequestException {
        log.info("Запрос на удаление cделки по айди \n" + extId);
        this.extId = extId;
        checkVar(List.of(extId));
        makeRequest();
        answer();
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.delete.json")
                    .queryString("id", extId)
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на удаление сделки. Текст боди : " + e.getMessage());
        }
    }

    private void getResult() {
        Gson gson = new Gson();
        bitrixCommonResponse = gson.fromJson(contactResponse.getBody().toString(), BitrixResponseDeleteDeal.class);
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            getResult();
            log.info("Сделка успешно удалена в битрикс системе");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на удаление сделки. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }
}
