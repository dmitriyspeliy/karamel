package effective_mobile.com.service.api.deal;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.DealAddResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.model.entity.Receipt;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.repository.ReceiptRepository;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.UtilsMethods;
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
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddDeal {

    @Value("${spring.current-city}")
    private String currentCity;
    private DealAddResponse dealAddResponse;
    private RequestToBookingEvent requestToBookingEvent;
    private HttpResponse<JsonNode> contactResponse;
    private Deal deal;
    private Contact contact;
    private String link;
    private final DealRepository dealRepository;
    private final EventRepository eventRepository;
    private final ReceiptRepository receiptRepository;

    public Deal addDeal(RequestToBookingEvent requestToBookingEvent, Contact contact, String link) throws BadRequestException {
        log.info("Запрос на добавление cделки \n" + requestToBookingEvent.toString());
        this.requestToBookingEvent = requestToBookingEvent;
        this.contact = contact;
        this.link = link;
        checkVar(List.of(requestToBookingEvent.getNumber(), requestToBookingEvent.getContactName()));
        makeRequest();
        answer();
        return deal;
    }

    private void makeRequest() throws BadRequestException {
        try {
            Optional<Event> eventOptional = eventRepository.findByName(requestToBookingEvent.getName().trim());
            Integer sum = 0;
            String extId = "0";
            if (eventOptional.isPresent()) {
                Integer adultPrice = eventOptional.get().getAdultPrice();
                Integer kidPrice = eventOptional.get().getKidPrice();
                sum = (adultPrice * requestToBookingEvent.getPaidAdultCount()) + (kidPrice * requestToBookingEvent.getChildrenCount());
                extId = String.valueOf(eventOptional.get().getExtEventId());
            }
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.deal.add.json")
                    .queryString("fields[CONTACT_ID]", contact.getExtContactId())
                    .queryString("fields[OPPORTUNITY]", String.valueOf(sum))
                    .queryString("fields[TITLE]", "Сделка с сайта " + NameOfCity.valueOf(currentCity.toUpperCase()).getHostName())
                    .queryString("fields[STAGE_ID]", "NEW")
                    .queryString("fields[UF_CRM_66A35EF77437E]", link)
                    .queryString("fields[UF_CRM_66A35EF7571B0]", "119") //117 yes 119 no
                    .queryString("fields[UF_CRM_1723104093]", extId)
                    .queryString("fields[COMMENTS]", requestToBookingEvent)
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на добавление сделки. Текст боди : " + e.getMessage());
        }
    }

    private void getResult() {
        Gson gson = new Gson();
        dealAddResponse = gson.fromJson(contactResponse.getBody().toString(), DealAddResponse.class);
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            getResult();
            deal = new Deal();
            deal.setExtDealId(Long.parseLong(String.valueOf(dealAddResponse.getResult())));
            saveToDb();
            log.info("Сделка успешно добавлена в битрикс систему и сохранена в бд");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на добавление сделки. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }

    private void saveToDb() {


        deal.setAddInfo(requestToBookingEvent.getSource());
        deal.setCreateDate(UtilsMethods.parseLocalDataTimeFromInstant(requestToBookingEvent.getDate()));
        deal.setContact(contact);
        deal.setTitle("Сделка с сайта " + NameOfCity.valueOf(currentCity.toUpperCase()).getHostName());
        deal.setPaid(false);
        Deal save = dealRepository.save(deal);

        // TODO delete receipt
        Receipt receipt = new Receipt();
        receipt.setAdd_info("Чек по сделке " + requestToBookingEvent.getCity());
        receipt.setCurrency("RUB");
        receipt.setLink(link);
        receipt.setDeal(save);
        receipt.setLocalDateTime(LocalDateTime.now());
        receipt.setBigDecimal(BigDecimal.ONE);
        receipt.setExt_receipt_id("UUUUU-iIIIIIAsdad");
        receiptRepository.save(receipt);
    }

}
