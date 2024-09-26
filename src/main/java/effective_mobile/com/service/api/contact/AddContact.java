package effective_mobile.com.service.api.contact;

import com.google.gson.Gson;
import effective_mobile.com.model.dto.rs.BitrixCommonResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.repository.ContactRepository;
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
@Slf4j
@RequiredArgsConstructor
public class AddContact {

    private String fullname;
    private String phone;
    private String city;
    private String comment;
    private Contact contact;
    private Optional<Contact> optionalContact;
    private BitrixCommonResponse bitrixCommonResponse;
    private HttpResponse<JsonNode> contactResponse;
    private final ContactRepository contactRepository;
    private final FindById findById;

    public Contact addContact(String fullName, String city, String phone, String comment) throws BadRequestException {
        log.info("Запрос на добавление контакта по имени ( " + fullName +
                " ), городу ( " + city + " ), телефону ( " + phone + " ), с комментарием ( " + comment + " ).");
        checkVar(List.of(fullName, city, phone, comment));
        this.fullname = fullName;
        this.comment = comment;
        this.city = city;
        this.phone = phone;
        if (checkExistContact()) {
            return contact;
        }
        makeRequest();
        answer();
        return contact;
    }

    private void makeRequest() throws BadRequestException {
        try {
            contactResponse
                    = Unirest.post(CommonVar.BITRIX_WEBHOOK + "crm.contact.add.json")
                    .queryString("fields[PHONE][0][VALUE]", phone)
                    .queryString("fields[PHONE][0][VALUE_TYPE]", "WORK")
                    .queryString("fields[ADDRESS_CITY]", city)
                    .queryString("fields[COMMENTS]", comment)
                    .queryString("fields[NAME]", fullname)
                    .queryString("fields[TYPE_ID]", "CLIENT")
                    .asJson();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Текст боди : " + e.getMessage());
        }
    }

    private void getResult() {
        Gson gson = new Gson();
        bitrixCommonResponse = gson.fromJson(contactResponse.getBody().toString(), BitrixCommonResponse.class);
    }

    public void answer() throws BadRequestException {
        if (contactResponse.getStatus() == 200) {
            getResult();
            contact = new Contact();
            contact.setExtContactId(Long.parseLong(String.valueOf(bitrixCommonResponse.getResult())));
            saveToDb();
            log.info("Контакт успешно добавлен в битрикс систему и сохранен в бд");
        } else {
            throw new BadRequestException("Не удалось отправить запрос на добавление контакта. Статус код " + contactResponse.getStatus()
                    + ". Боди " + contactResponse.getBody());
        }
    }

    private void saveToDb() {
        if (optionalContact.isPresent()) {
            optionalContact.get().setCity(city);
            optionalContact.get().setPhone(phone);
            optionalContact.get().setComment(comment);
            optionalContact.get().setFullName(fullname);
            contact = contactRepository.save(optionalContact.get());
        } else {
            contact.setCity(city);
            contact.setPhone(phone);
            contact.setComment(comment);
            contact.setFullName(fullname);
            contact = contactRepository.save(contact);
        }

    }

    private boolean checkExistContact() throws BadRequestException {
        boolean res = false;
        optionalContact = contactRepository.findByPhone(phone);
        if (optionalContact.isPresent()) {
            Long extId = optionalContact.get().getExtContactId();
            boolean exist = findById.existInBitrix(String.valueOf(extId));
            if (exist) {
                contact = optionalContact.get();
                res = true;
            }
        }
        return res;
    }

}
