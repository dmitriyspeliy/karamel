package effective_mobile.com.service.api.event;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static effective_mobile.com.utils.CommonVar.*;
import static effective_mobile.com.utils.UtilsMethods.defineType;
import static effective_mobile.com.utils.UtilsMethods.getValueFromProperty;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeEventInBitrix {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;
    private Map<String, String> payLoad;

    public void bookMixedEvent(int kidTickets, int adultTickets, Event event) throws BadRequestException {
        payLoad = new HashMap<>();
        updateEventFromWebHook(event);

        event.setKidCapacity(event.getKidCapacity() - kidTickets);
        event.setAdultCapacity(event.getAdultCapacity() - adultTickets);
        event.setCapacity(event.getCapacity() - adultTickets - kidTickets);

        int sum = kidTickets + adultTickets;
        if (event.getKidCapacity() < 0
                || event.getAdultCapacity() < 0
                || event.getCapacity() < 0) {
            throw new BadRequestException("Нельзя забронировать так как всего мест меньше, чем нужно." +
                    "\nМест для детей " + event.getKidCapacity() + ", а нужно " + kidTickets
                    + "\nМест для взрослых " + event.getAdultCapacity() + ", а нужно " + adultTickets
                    + "\nМест общее кол-во " + event.getAdultCapacity() + ", а нужно " + sum
            );
        }

        payLoad.put("PROPERTY_109", event.getKidCapacity().toString());
        payLoad.put("PROPERTY_111", event.getAdultCapacity().toString());
        payLoad.put("PROPERTY_131", event.getCapacity().toString());

        if (event.getCapacity() == 0 || event.getAdultCapacity() == 0 || event.getKidCapacity() == 0) {
            payLoad.put("PROPERTY_119", "95");
        }

        updateInBitrix(event, payLoad);

        eventRepository.save(event);
    }

    public void bookSchoolEvent(Event event) throws BadRequestException {
        payLoad = new HashMap<>();
        updateEventFromWebHook(event);

        String property119 = payLoad.get("PROPERTY_119");

        if (property119.equals("95")) {
            throw new BadRequestException("Уже забронировали");
        }

        payLoad.put("PROPERTY_119", "95");
        updateInBitrix(event, payLoad);

        eventRepository.save(event);
    }

    public void undoChangingInMixedEvent(int kidTickets, int adultTickets, Event event) {
        payLoad = new HashMap<>();
        updateEventFromWebHook(event);

        event.setKidCapacity(event.getKidCapacity() + kidTickets);
        event.setAdultCapacity(event.getAdultCapacity() + adultTickets);
        event.setCapacity(event.getKidCapacity() + event.getAdultCapacity());

        payLoad.put("PROPERTY_109", event.getKidCapacity().toString());
        payLoad.put("PROPERTY_111", event.getAdultCapacity().toString());
        payLoad.put("PROPERTY_131", event.getCapacity().toString());
        payLoad.put("PROPERTY_119", "93");

        updateInBitrix(event, payLoad);

        eventRepository.save(event);
    }

    public void undoChangingInSchoolEvent(Event event) {
        payLoad = new HashMap<>();
        updateEventFromWebHook(event);

        payLoad.put("PROPERTY_119", "93");

        updateInBitrix(event, payLoad);

        eventRepository.save(event);
    }

    private void updateEventFromWebHook(Event event) {

        var eventBitrixId = event.getExtEventId();

        var hookWithAdditionalParams = BITRIX_WEBHOOK + "lists.element.get.json?"
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&ELEMENT_ID=" + eventBitrixId;

        var node = restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);

        assert node != null;

        var levelNode = node.path("result").get(0);

        updateValueFromNode(levelNode, event);
    }

    private void updateValueFromNode(JsonNode element, Event event) {
        event.setAdultPrice(BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_135"))));
        event.setKidPrice(BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_129"))));
        event.setChildAge(getValueFromProperty(element, "PROPERTY_133"));
        String type = defineType(element);
        event.setType(type);
        if (type.equals("ШКОЛЬНЫЕ ГРУППЫ")) {
            event.setCapacity(1L);
            event.setAdultCapacity(1L);
            event.setKidCapacity(1L);
        } else {
            event.setCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_131")));
            event.setAdultCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_111")));
            event.setKidCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_109")));
        }
        event.setExtEventId(element.path("ID").asText());
        event.setName(element.path("NAME").asText());
        event.setTime(LocalDateTime.parse(getValueFromProperty(element, "PROPERTY_113"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));


        payLoad.put("NAME", element.path("NAME").asText());
        payLoad.put("PROPERTY_107", getValueFromProperty(element, "PROPERTY_107"));
        payLoad.put("PROPERTY_109", getValueFromProperty(element, "PROPERTY_109"));
        payLoad.put("PROPERTY_111", getValueFromProperty(element, "PROPERTY_111"));
        payLoad.put("PROPERTY_113", getValueFromProperty(element, "PROPERTY_113"));
        payLoad.put("PROPERTY_115", getValueFromProperty(element, "PROPERTY_115"));
        payLoad.put("PROPERTY_117", element.path("PROPERTY_117").toString());
        payLoad.put("PROPERTY_119", getValueFromProperty(element, "PROPERTY_119"));
        payLoad.put("PROPERTY_121", getValueFromProperty(element, "PROPERTY_121"));
        payLoad.put("PROPERTY_123", getValueFromProperty(element, "PROPERTY_123"));
        payLoad.put("PROPERTY_125", getValueFromProperty(element, "PROPERTY_125"));
        payLoad.put("PROPERTY_127", getValueFromProperty(element, "PROPERTY_127"));
        payLoad.put("PROPERTY_129", getValueFromProperty(element, "PROPERTY_129"));
        payLoad.put("PROPERTY_131", getValueFromProperty(element, "PROPERTY_131"));
        payLoad.put("PROPERTY_133", getValueFromProperty(element, "PROPERTY_133"));
        payLoad.put("PROPERTY_135", getValueFromProperty(element, "PROPERTY_135"));
    }

    private void updateInBitrix(Event event, Map<String, String> payLoad) {

        var eventBitrixId = event.getExtEventId();
        var url = BITRIX_WEBHOOK + "lists.element.update";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "IBLOCK_TYPE_ID", IBLOCK_TYPE_ID,
                "IBLOCK_ID", IBLOCK_ID,
                "ELEMENT_ID", eventBitrixId,
                "FIELDS", payLoad
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        restTemplate.postForObject(url, requestEntity, String.class);
    }

}
