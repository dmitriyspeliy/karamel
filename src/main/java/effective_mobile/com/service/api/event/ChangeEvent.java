package effective_mobile.com.service.api.event;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.service.EventService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static effective_mobile.com.utils.CommonVar.*;
import static effective_mobile.com.utils.UtilsMethods.getValueFromProperty;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeEvent {
    private final EventRepository eventRepository;
    private final EventService service;
    // id сделок в битриксе
    private ArrayList<String> deals = new ArrayList<>();
    // сохраняем все поля слота, т.к. битрикс удаляет значения из полей, которых нет в запросе.
    private Map<String, String> payLoad = new HashMap<>();

    // предполагается что deal уже есть в битрикс
    public void bookMixedEvent(int kidTickets, int adultTickets, Deal deal) {
        log.info("Starting to book mixed event: {}", deal.getEvent().getName());
        var event = deal.getEvent();

        deals.add(deal.getExtDealId());

        updateEventFromWebHook(event);

        event.setKidSlotsLeft(event.getKidSlotsLeft() - kidTickets);
        event.setKidCapacity(event.getKidCapacity() - kidTickets);
        event.setAdultSlotsLeft(event.getAdultSlotsLeft() - adultTickets);
        event.setAdultCapacity(event.getAdultCapacity() - adultTickets);
        event.setCapacity(event.getCapacity() - adultTickets - kidTickets);

        payLoad.put("PROPERTY_109", event.getKidCapacity().toString());
        payLoad.put("PROPERTY_111", event.getAdultCapacity().toString());
        payLoad.put("PROPERTY_131", event.getCapacity().toString());
        payLoad.put("PROPERTY_117", deals.toString());
        payLoad.put("PROPERTY_113", event.getTime().toString());
        if (event.getAdultCapacity() == 0 || event.getKidCapacity() == 0) {
            payLoad.put("PROPERTY_119", "95");
        }

        updateInBitrix(event, payLoad);

        log.info("Saving event: {}", event);
        eventRepository.save(event);
    }

    // убрал логику с местами, т.к. по идеи 1бронь - 1класс
    public void bookSchoolEvent(int kidTickets, int adultTickets, Deal deal) {
        log.info("Starting to book school event: {}", deal.getEvent().getName());
        var event = deal.getEvent();
        deals.add(deal.getExtDealId());

        updateEventFromWebHook(event);
        payLoad.put("PROPERTY_119", "95");
        payLoad.put("PROPERTY_117", deals.toString());
        updateInBitrix(event, payLoad);

        log.info("Saving event: {}", event);
        eventRepository.save(event);
    }

    private void updateEventFromWebHook(Event event) {
        var restTemplate = new RestTemplate();
        var eventBitrixId = event.getExtEventId();
        var hookWithAdditionalParams = BITRIX_WEBHOOK + "lists.element.get.json?"
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&ELEMENT_ID=" + eventBitrixId;

        var node = restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);
        var levelNode = node.path("result").get(0);
        updateValueFromNode(levelNode, event);
        log.info("Updated event from webhook: {}", event);
    }

    // обновляем сущность и сохраняем значения в пейлоад, чтобы битрикс их при обновление не затер
    private void updateValueFromNode(JsonNode element, Event event) {
        event.setAdultPrice(BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_135"))));
        event.setKidPrice(BigDecimal.valueOf(Long.parseLong(getValueFromProperty(element, "PROPERTY_129"))));
        event.setChildAge(getValueFromProperty(element, "PROPERTY_133"));
        event.setCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_131")));
        event.setAdultCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_111")));
        event.setKidCapacity(Long.parseLong(getValueFromProperty(element, "PROPERTY_109")));
        event.setSlotsLeft(Long.parseLong(getValueFromProperty(element, "PROPERTY_131")));
        event.setAdultSlotsLeft(Long.parseLong(getValueFromProperty(element, "PROPERTY_111")));
        event.setKidSlotsLeft(Long.parseLong(getValueFromProperty(element, "PROPERTY_111")));
        event.setExtEventId(element.path("ID").asText());
        event.setName(element.path("NAME").asText());
        event.setType(service.defineType(element));
        event.setTime(LocalDateTime.parse(getValueFromProperty(element, "PROPERTY_113"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        JsonNode property117 = element.path("PROPERTY_117");
        property117.fields().forEachRemaining(entry -> {
            deals.add(entry.getValue().asText());
        });

        payLoad.put("NAME", element.path("NAME").asText());
        payLoad.put("PROPERTY_107", getValueFromProperty(element, "PROPERTY_107"));
        payLoad.put("PROPERTY_109", getValueFromProperty(element, "PROPERTY_109"));
        payLoad.put("PROPERTY_111", getValueFromProperty(element, "PROPERTY_111"));
        payLoad.put("PROPERTY_113", getValueFromProperty(element, "PROPERTY_113"));
        payLoad.put("PROPERTY_115", getValueFromProperty(element, "PROPERTY_115"));
        payLoad.put("PROPERTY_117", deals.toString());
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
        var restTemplate = new RestTemplate();
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
        log.info("Updating event in Bitrix with payload: {}", payLoad);
        var response = restTemplate.postForObject(url, requestEntity, String.class);
        log.info("Received response from Bitrix: {}", response);
    }

}
