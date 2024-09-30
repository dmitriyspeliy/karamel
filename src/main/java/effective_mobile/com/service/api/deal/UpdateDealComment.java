package effective_mobile.com.service.api.deal;

import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDealComment {

    private final DealRepository dealRepository;
    private HttpResponse<JsonNode> contactResponse;
    private String comment;
    private String extId;

    public void refreshCommentDeal(String extId, String comment) throws BadRequestException {
        log.info("Запрос на обновление коммента сделки по айди " + extId);
        checkVar(List.of(extId, comment));
        this.extId = extId;
        this.comment = comment;
        makeRequest();
        answer();
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.update.json")
                    .queryString("id", extId)
                    .queryString("fields[COMMENTS]", comment)
                    .queryString("params[REGISTER_SONET_EVENT]", "Y")
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на обновление коммента сделки. Текст боди : " + e.getMessage());
        }
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            Optional<Deal> dealOptional = dealRepository.findByExtDealId(extId);
            if (dealOptional.isPresent()) {
                String addInfo = dealOptional.get().getAddInfo();
                if (addInfo != null) {
                    addInfo = addInfo + ";" + comment;
                } else {
                    addInfo = comment + ";";
                }
                dealOptional.get().setAddInfo(addInfo);
                dealRepository.save(dealOptional.get());
                log.info("Сделка успешно обновлена в бд и в битрикс");
            }
            return;
        }
        throw new BadRequestException("Не удалось отправить запрос на обновление коммента сделки. Статус код " + contactResponse.getStatus()
                + ". Боди " + contactResponse.getBody());
    }
}
