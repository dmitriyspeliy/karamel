package effective_mobile.com.service;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.model.dto.Event;
import effective_mobile.com.model.dto.enums.City;
import effective_mobile.com.model.dto.enums.SlotType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    @Value("${bitrix.baseUrl}")
    private String baseUrl;
    @Value("${bitrix.endpoints.getEvents}")
    private String endpoint;

    private static final String IBLOCK_TYPE_ID = "lists";
    private static final String IBLOCK_ID = "29";
    private static final String FILTER_PROPERTY_119 = "93";
    private static final String ELEMENT_ORDER_PROPERTY_113 = "DESC";

    public List<Event> getUpcomingEvents(String city, String type) {
        var response = sentRequest(city, type);
        var elements = response.path("result");
        var slots = new ArrayList<Event>();

        for (var element : elements) {
            if (isAvailable(element, city)) {
                var slot = Event.builder()
                        .id(element.path("ID").asLong())
                        .name(element.path("NAME").asText())
                        .time(getValueFromProperty(element, "PROPERTY_113"))
                        .adultPrice(Integer.parseInt(getValueFromProperty(element, "PROPERTY_135")))
                        .kidPrice(Integer.parseInt(getValueFromProperty(element, "PROPERTY_129")))
                        .childAge(element.path("PROPERTY_133").asInt())
                        .capacity(element.path("PROPERTY_111").asInt())
                        .adultCapacity(element.path("PROPERTY_121").asInt())
                        .kidCapacity(element.path("PROPERTY_107").asInt())
                        .slotsLeft(element.path("PROPERTY_119").asInt())
                        .adultSlotsLeft(element.path("PROPERTY_119").asInt())
                        .kidSlotsLeft(element.path("PROPERTY_119").asInt())
                        .gatheringType(SlotType.typeFromValue(element.path("PROPERTY_125").asInt()))
                        //в слотах не передается этот параметр
                        .adultRequired(true)
                        .build();
                slots.add(slot);
            }
        }
        return slots;
    }

    private boolean isAvailable(JsonNode node, String city) {
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        var dateStr = getValueFromProperty(node, "PROPERTY_113");
        var now = LocalDateTime.now();
        if (dateStr.isBlank()) {
            return false;
        }
        var date = LocalDateTime.parse(dateStr, formatter);

        return date.isAfter(now);
    }

    private JsonNode sentRequest(String city, String type) {
        var restTemplate = new RestTemplate();
        var codeOfCity = City.getCodeOfCity(city);
        var codeOfType = SlotType.getCodeOfType(type);

        var hookWithAdditionalParams = baseUrl + endpoint
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&FILTER[PROPERTY_119]=" + FILTER_PROPERTY_119
                + "&ELEMENT_ORDER[PROPERTY_113]=" + ELEMENT_ORDER_PROPERTY_113
                + "&FILTER[PROPERTY_123]=" + codeOfCity
                + "&FILTER[PROPERTY_125]=" + codeOfType;

        return restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);
    }

    private String getValueFromProperty(JsonNode node, String property) {
        var entrySet = node.path(property);
        var key = entrySet.fieldNames().next();
        return entrySet.path(key).asText();
    }
}
