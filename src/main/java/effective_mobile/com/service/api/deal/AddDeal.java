package effective_mobile.com.service.api.deal;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rs.BitrixCommonResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.enums.NameOfCity;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddDeal {

    @Value("${spring.current-city}")
    private String currentCity;
    private BitrixCommonResponse bitrixCommonResponse;
    private Event event;
    private HttpResponse<JsonNode> contactResponse;
    private Deal deal;
    private BigDecimal sum;
    private Contact contact;
    private String siteHostName;
    private Integer adultCount;
    private Integer kidCount;

    public Deal addDeal(BigDecimal sum, Event event, Contact contact, Integer adultCount, Integer kidCount) throws BadRequestException {
        log.info("Запрос на добавление cделки \n" + event.toString());
        this.kidCount = kidCount;
        this.adultCount = adultCount;
        this.sum = sum;
        this.contact = contact;
        this.event = event;
        checkVar(List.of(contact.getPhone(), contact.getFullName()));
        makeRequest();
        answer();
        return deal;
    }

    private void makeRequest() throws BadRequestException {
        try {
            siteHostName = NameOfCity.valueOf(currentCity.toUpperCase()).getHostName();
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.add.json")
                    .queryString("fields[CONTACT_ID]", contact.getExtContactId())
                    .queryString("fields[OPPORTUNITY]", sum.toString())
                    .queryString("fields[TITLE]", "Сделка с сайта " + siteHostName)
                    .queryString("fields[STAGE_ID]", "NEW")
                    .queryString("fields[UF_CRM_66A35EF7571B0]", "119") //117 yes 119 no
                    .queryString("fields[UF_CRM_1723104093]", event.getExtEventId())
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на добавление сделки. Текст боди : " + e.getMessage());
        }
    }

    private void getResult() {
        Gson gson = new Gson();
        bitrixCommonResponse = gson.fromJson(contactResponse.getBody().toString(), BitrixCommonResponse.class);
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            getResult();
            deal = new Deal();
            deal.setExtDealId(String.valueOf(bitrixCommonResponse.getResult()));
            createDeal();
            log.info("Сделка успешно добавлена в битрикс систему");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на добавление сделки. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }

    private void createDeal() {
        deal.setKidAge(event.getChildAge());
        deal.setKidPrice(event.getKidPrice());
        deal.setKidCount(kidCount);
        deal.setAdultPrice(event.getAdultPrice());
        deal.setAdultCount(adultCount);
        deal.setCreateDate(LocalDateTime.now());
        deal.setContact(contact);
        deal.setTitle(siteHostName);
        deal.setPaid(false);
    }
}
