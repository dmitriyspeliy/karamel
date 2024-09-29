package effective_mobile.com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import effective_mobile.com.model.dto.Receipt;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class UtilsMethods {

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
            log.warn("Property {} not found in node, returning default value '0'", property);
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
}
