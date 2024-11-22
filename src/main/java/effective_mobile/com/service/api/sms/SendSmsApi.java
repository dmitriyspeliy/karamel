package effective_mobile.com.service.api.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import effective_mobile.com.model.dto.rs.SmsAnswer;
import effective_mobile.com.utils.CommonVar;
import effective_mobile.com.utils.exception.BadRequestException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendSmsApi {
    private SmsAnswer smsAnswer;
    private HttpResponse<String> response;
    private String phone;
    private String text;

    public SmsAnswer sendSms(String phone, String text) throws BadRequestException, JsonProcessingException {
        this.phone = phone;
        this.text = text;

        makeRequest();
        makeBody();
        return smsAnswer;
    }

    private void makeRequest() throws BadRequestException {
        try {
            String SMS_FORMAT = "json=1";
            String TTL = "ttl=1440";
            //String FOR_TEST = "test=1";
            response = Unirest.get(CommonVar.SMS_RU_URL +
                            "?api_id=" + CommonVar.SMS_RU_API_ID +
                            "&to=" + phone +
                            "&msg=" + text +
                            "&from=" + "exkcaramel" +
                            "&" + SMS_FORMAT +
                            "&" + TTL)
                    .asString();
        } catch (Exception e) {
            throw new BadRequestException("Не удалось отправить запрос к смс ру. Сообщение " + e.getMessage());
        }
    }

    private void makeBody() throws BadRequestException, JsonProcessingException {
        if (response.getStatus() == 200) {
            smsAnswer = new ObjectMapper().readValue(response.getBody(), SmsAnswer.class);
            String key = new ArrayList<>(new ArrayList<>(new ObjectMapper().readValue(response.getBody(), SmsAnswer.class).getSms().getDetails().keySet())).get(0);
            LinkedHashMap<String, String> values = (LinkedHashMap<String, String>) smsAnswer.getSms().getDetails().get(key);
            SmsAnswer.Sms.Info info = new SmsAnswer.Sms.Info();
            info.setCost(BigDecimal.valueOf(Double.parseDouble(values.get("cost") == null ? "0.00" : values.get("cost"))));
            info.setStatus(values.get("status"));
            info.setStatus_code(Integer.valueOf(String.valueOf(values.get("status_code"))));
            info.setStatusText(values.get("status_text"));
            info.setSmsId(values.get("sms_id"));
            smsAnswer.getSms().setInfo(info);

        } else {
            throw new BadRequestException("Не удалось отправить запрос к смс ру. Сообщение " + response.getBody() +
                    " Статус код " + response.getStatus());
        }

    }
}
