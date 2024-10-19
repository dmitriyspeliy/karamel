package effective_mobile.com.service.api.deal;

import com.google.gson.Gson;
import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rs.BitrixCommonResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.enums.CityInfo;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddDeal {

    private final CityProperties cityProperties;
    private BitrixCommonResponse bitrixCommonResponse;
    private Event event;
    private HttpResponse<JsonNode> contactResponse;
    private Deal deal;
    private BigDecimal sum;
    private Contact contact;
    private String siteHostName;
    private Integer adultCount;
    private Integer kidCount;
    private String currentCity;

    public Deal addDeal(BigDecimal sum, Event event, Contact contact, Integer adultCount, Integer kidCount, String city) throws BadRequestException {
        log.info("Запрос на добавление cделки для города " + city);
        this.kidCount = kidCount;
        this.adultCount = adultCount;
        this.sum = sum;
        this.contact = contact;
        this.event = event;
        this.currentCity = city;
        checkVar(List.of(contact.getPhone(), contact.getFullName()));
        makeRequest();
        answer();
        return deal;
    }

    private void makeRequest() throws BadRequestException {
        CityProperties.Info cityInfo = cityProperties.getCityInfo().get(currentCity);
        log.info(cityInfo.getBitrixFieldNum());
        try {
            siteHostName = CityInfo.valueOf(currentCity.toUpperCase()).getHostName();
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.add.json")
                    .queryString("fields[CONTACT_ID]", contact.getExtContactId())
                    .queryString("fields[OPPORTUNITY]", sum.toString())
                    .queryString("fields[TITLE]", "Сделка с сайта " + siteHostName)
                    .queryString("fields[STAGE_ID]", "NEW") // FINAL_INVOICE
                    .queryString("fields[UF_CRM_66A35EF5A2449]", cityInfo.getBitrixFieldNum()) // CITY
                    .queryString("fields[UF_CRM_66A35EF6A220C]", changeTimeFormat(event.getName())) // DATE Event
                    .queryString("fields[UF_CRM_66A35EF7571B0]", "119") // оплата бронь 117 yes 119 no
                    .queryString("fields[UF_CRM_66A35EF7675A3]", "123") // оплата мест 121 yes 123 no
                    .queryString("fields[UF_CRM_1723104093]", event.getExtEventId())
                    .queryString("fields[UF_CRM_66A35EF6D7732]", kidCount) // count kid
                    .queryString("fields[UF_CRM_66D98814EFA14]", event.getKidPrice()) // price kid
                    .queryString("fields[UF_CRM_66A35EF6E715E]", adultCount) // count adult
                    .queryString("fields[UF_CRM_66D98814E3C15]", event.getAdultPrice()) // price adult
                    .queryString("fields[UF_CRM_66A35EF72DC9E]", getAgeBitrixField(event.getChildAge())) // age 103 105 107
                    .queryString("fields[UF_CRM_66A35EF6C77BB]", typeOfDealInBitrixFields(event.getType())) // type of group школьные 93 сборные 95
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
        deal.setType(event.getType());
        deal.setAdultPrice(event.getAdultPrice());
        deal.setAdultCount(adultCount);
        deal.setCreateDate(LocalDateTime.now());
        deal.setContact(contact);
        deal.setTitle(siteHostName);
        deal.setPaid(false);
    }
}
