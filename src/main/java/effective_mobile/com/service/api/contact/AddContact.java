package effective_mobile.com.service.api.contact;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rs.ContactAddResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class AddContact {

    private String fullname;
    private String phone;
    private String city;
    private String comment;
    private Contact contact;
    private ContactAddResponse contactAddResponse;
    private HttpResponse<String> httpResponse;
    private final HttpClient httpClient;
    private final ContactRepository contactRepository;

    public Contact addContact(String fullName, String city, String phone, String comment) throws BadRequestException {
        log.info("Запрос на добавление контакта по имени ( " + fullName +
                " ), городу ( " + city + " ), телефону ( " + phone + " ), с комментарием ( " + comment + " ).");
        checkVar(List.of(fullName, city, phone, comment));
        this.fullname = fullName;
        this.comment = comment;
        this.city = city;
        this.phone = phone;
        makeRequest();
        answer();
        return contact;
    }

    private void makeRequest() throws BadRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CommonVar.BITRIX_WEBHOOK
                        + "crm.contact.add.json?" +
                        "fields[NAME]=" + fullname +
                        "&fields[PHONE][0][VALUE]=" + phone +
                        "&fields[PHONE][0][VALUE_TYPE]=WORK" +
                        "&fields[ADDRESS_CITY]=" + city +
                        "&fields[COMMENTS]=\"" + comment + "\""))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        try {
            httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Текст боди : " + e.getMessage());
        }

    }

    private void getResult() {
        Gson gson = new Gson();
        contactAddResponse = gson.fromJson(httpResponse.body(), ContactAddResponse.class);
    }

    public void answer() throws BadRequestException {
        if (httpResponse.statusCode() == 200) {
            getResult();
            contact = new Contact();
            contact.setExtContactId(Long.parseLong(String.valueOf(contactAddResponse.getResult())));
            saveToDb();
            log.info("Контакт успешно добавлен в битрикс систему и сохранен в бд");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Статус код " + httpResponse.statusCode()
                    + ". Боди " + httpResponse.body());
        }
    }

    private void saveToDb() {
        contact.setCity(city);
        contact.setComment(comment);
        contact.setFullName(fullname);
        contact = contactRepository.save(contact);
    }

}
