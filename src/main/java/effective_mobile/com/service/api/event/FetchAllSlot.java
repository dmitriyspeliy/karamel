package effective_mobile.com.service.api.event;

import com.fasterxml.jackson.databind.JsonNode;
import effective_mobile.com.utils.enums.City;
import effective_mobile.com.utils.enums.SlotType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static effective_mobile.com.utils.CommonVar.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchAllSlot {

    private final RestTemplate restTemplate;

    public JsonNode fetchAllSlotByCityAndType(String city, String type) {
        log.info("Sending request to Bitrix API for city: {} and type: {}", city, type);

        var codeOfCity = City.getCodeOfCity(city);
        var codeOfType = SlotType.getCodeOfType(type);
        var now = LocalDateTime.now();

        var hookWithAdditionalParams = BITRIX_WEBHOOK + "lists.element.get.json?"
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&FILTER[PROPERTY_119]=" + FILTER_PROPERTY_119
                + "&ELEMENT_ORDER[PROPERTY_113]=" + ELEMENT_ORDER_PROPERTY_113
                + "&FILTER[PROPERTY_123]=" + codeOfCity
                + "&FILTER[PROPERTY_125]=" + codeOfType
                + "&FILTER[PROPERTY_113]>" + now;

        return restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);
    }

    public JsonNode fetchAllSlotByCityAndType(String city, String type, String start) {
        log.info("Sending request to Bitrix API for city: {} and type: {}", city, type);

        var codeOfCity = City.getCodeOfCity(city);
        var codeOfType = SlotType.getCodeOfType(type);
        var now = LocalDateTime.now();

        var hookWithAdditionalParams = BITRIX_WEBHOOK + "lists.element.get.json?"
                + "IBLOCK_TYPE_ID=" + IBLOCK_TYPE_ID
                + "&IBLOCK_ID=" + IBLOCK_ID
                + "&start=" + start
                + "&FILTER[PROPERTY_119]=" + FILTER_PROPERTY_119
                + "&ELEMENT_ORDER[PROPERTY_113]=" + ELEMENT_ORDER_PROPERTY_113
                + "&FILTER[PROPERTY_123]=" + codeOfCity
                + "&FILTER[PROPERTY_125]=" + codeOfType
                + "&FILTER[PROPERTY_113]>" + now;

        return restTemplate.getForObject(hookWithAdditionalParams, JsonNode.class);
    }

}
