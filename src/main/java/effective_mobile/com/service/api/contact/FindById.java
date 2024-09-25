package effective_mobile.com.service.api.contact;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rs.FindByIdResponse;
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
@Slf4j
@RequiredArgsConstructor
public class FindById {
    private String extContactId;
    private FindByIdResponse findByIdResponse;
    private HttpResponse<JsonNode> contactResponse;
    private boolean result;

    public boolean existInBitrix(String extContactId) throws BadRequestException {
        log.info("Запрос на поиск контакта по айди " + extContactId);
        checkVar(List.of(extContactId));
        this.extContactId = extContactId;
        result = false;
        makeRequest();
        answer();
        return result;
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.contact.get.json")
                    .queryString("ID", extContactId)
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на поиск контакта. Текст боди : " + e.getMessage());
        }
    }

    private void getResult() {
        Gson gson = new Gson();
        findByIdResponse = gson.fromJson(contactResponse.getBody().toString(), FindByIdResponse.class);
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            getResult();
            if (findByIdResponse.getResult() != null && findByIdResponse.getResult().getID() != null) {
                log.info("Контакт с айди " + extContactId + " есть в битрикс");
                result = true;
            } else {
                log.info("Нет контакта с айди " + extContactId + " в битрикс");
            }
        } else {
            throw new BadRequestException("Не удалось получить контакт по айди " + extContactId);
        }
    }

}
