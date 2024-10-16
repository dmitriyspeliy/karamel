package effective_mobile.com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import effective_mobile.com.model.dto.Receipt;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.utils.enums.NameOfCity;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UtilsMethods {

    private final CacheManager cacheManager;


    public static void checkVar(List<String> vars) throws BadRequestException {
        for (String var : vars) {
            if (var == null || var.equals("")) {
                throw new BadRequestException("Var mustn't null or empty");
            }
        }
    }

    public static String calculateSignature(String loginMerchant,
                                            BigDecimal payment,
                                            String extInvoiceId,
                                            String urlEncodedReceipt,
                                            String pass1) {
        return DigestUtils.md5DigestAsHex(String.join(
                ":",
                loginMerchant,
                payment.toString(),
                extInvoiceId,
                urlEncodedReceipt,
                pass1).getBytes());
    }

    public static String calculateSignature(String loginMerchant,
                                            String extInvoiceId,
                                            String pass2) {
        return DigestUtils.md5DigestAsHex(String.join(
                ":",
                loginMerchant,
                extInvoiceId,
                pass2).getBytes());
    }

    public static String createUrlEncodedReceipt(BigDecimal sum, String desc, String sno, String tax, BigDecimal quantity) throws JsonProcessingException {
        Receipt.Items items = new Receipt.Items();
        items.setName(desc);
        items.setTax(tax);
        items.setSum(sum);
        items.setQuantity(quantity);
        Receipt receipt = new Receipt();
        receipt.setItems(List.of(items));
        receipt.setSno(sno);
        return URLEncoder.encode(new ObjectMapper().writeValueAsString(receipt), StandardCharsets.UTF_8);
    }

    public static String getValueFromProperty(JsonNode node, String property) {
        try {
            var entrySet = node.path(property);
            var key = entrySet.fieldNames().next();
            return entrySet.path(key).asText();
        } catch (NoSuchElementException e) {
            return "0";
        }
    }

    public static String defineType(JsonNode element) {
        String type = getValueFromProperty(element, "PROPERTY_125");
        if (type == null || type.equals("")) {
            return "ШКОЛЬНЫЕ ГРУППЫ";
        } else if (type.equals("117")) {
            return "ШКОЛЬНЫЕ ГРУППЫ";
        } else if (type.equals("119")) {
            return "СБОРНЫЕ ГРУППЫ";
        } else {
            return "ШКОЛЬНЫЕ ГРУППЫ";
        }
    }

    public static String defineType(String type) {
        if (type.contains("СБОРНЫЕ ГРУППЫ")) {
            return "Сборные";
        }else {
            return "Школьные";
        }
    }

    public static String getAge(String age) {
        return switch (age) {
            case "9,9" -> "8-11";
            case "7,7" -> "6-8";
            case "5,5" -> "4-6";
            default -> age;
        };
    }

    public static String getAgeBitrixField(String age) {
        return switch (age) {
            case "9,9" -> "107";
            case "7,7" -> "105";
            default -> "103";
        };
    }

    public static String typeOfDealInBitrixFields(String type) {
        if (type.contains("СБОРНЫЕ")) {
            return "95";
        } else {
            return "93";
        }
    }

    public static String changeTimeFormat(String time) {
        if (time == null || time.length() < 10) {
            return time;
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public static String getShortCityName(String name) throws BadRequestException {
        NameOfCity[] values = NameOfCity.values();
        for (NameOfCity value : values) {
            if (value.getName().contains(name)) {
                return value.name().toLowerCase();
            }
        }
        throw new BadRequestException("No city by name " + name);
    }


    public void cleanCashed(Event event) {
        String type = defineType(event.getType());
        //делаем сброс в кэше, чтобы след запрос был уже с актуальными местами
        if (cacheManager.getCache("json-nodes") != null) {
            try {
                boolean res = Objects.requireNonNull(cacheManager.getCache("json-nodes")).evictIfPresent(event.getCity() + type);
                if (!res) {
                    log.warn("Cashed wasn't refreshed for city " + event.getCity() + " and type " + type);
                } else {
                    log.info("Cashed was refreshed for city " + event.getCity() + " and type " + type);
                }
            } catch (NullPointerException e) {
                log.error(e.getMessage());
            }
        }
    }

}
