package effective_mobile.com.service.api.deal;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.ContactAddResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.service.EventService;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.NameOfCity;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddDeal {

//    private DealAddResponse dealAddResponse;
//    private Contact contact;
//    private RequestToBookingEvent requestToBookingEvent;
//    private HttpResponse<String> httpResponse;
//    private final HttpClient httpClient;
//    private final ContactRepository contactRepository;
//    @Value("${spring.current-city}")
//    private String currentCity;
//    private final EventService eventService;
//
//    public Deal addDeal(RequestToBookingEvent requestToBookingEvent, Contact contact) throws BadRequestException {
//        log.info("Запрос на добавление cделки \n" + requestToBookingEvent.toString());
//        this.requestToBookingEvent = requestToBookingEvent;
//        this.contact = contact;
//        checkVar(List.of(requestToBookingEvent.getNumber(), requestToBookingEvent.getContactName()));
//        makeRequest();
//        answer();
//        return contact;
//    }
//
//    private void makeRequest() throws BadRequestException {
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(CommonVar.BITRIX_WEBHOOK
//                        + "crm.deal.add.json?" +
//                        "&fields[CONTACT_ID]=" + contact.getExtContactId() +
//                        "&fields[OPPORTUNITY]=" + contact.getExtContactId() +
//                        "&fields[TITLE]=Сделка с сайта " + NameOfCity.valueOf(currentCity).getHostName() +
//                        "&fields[STAGE_ID]=NEW" +
//                        "&fields[UF_CRM_66A35EF77437E]=lnkpay" +
//                        "&fields[UF_CRM_66A35EF7571B0]=BOOKORNOT" +
//                        "&fields[UF_CRM_1723104093]=NAME" +
//                        "&fields[COMMENTS]=\"" + requestToBookingEvent.toString() + "\""))
//                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                .timeout(Duration.of(10, ChronoUnit.SECONDS))
//                .build();
//        try {
//            httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Текст боди : " + e.getMessage());
//        }
//
//    }
//
//    private void getResult() {
//        Gson gson = new Gson();
//        contactAddResponse = gson.fromJson(httpResponse.body(), ContactAddResponse.class);
//    }
//
//    public void answer() throws BadRequestException {
//        if (httpResponse.statusCode() == 200) {
//            getResult();
//            contact = new Contact();
//            contact.setExtContactId(Long.parseLong(String.valueOf(contactAddResponse.getResult())));
//            saveToDb();
//            log.info("Контакт успешно добавлен в битрикс систему и сохранен в бд");
//        } else {
//            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Статус код " + httpResponse.statusCode()
//                    + ". Боди " + httpResponse.body());
//        }
//    }
//
//    private void saveToDb() {
//        contact.setCity(city);
//        contact.setComment(comment);
//        contact.setFullName(fullname);
//        contact = contactRepository.save(contact);
//    }

}
