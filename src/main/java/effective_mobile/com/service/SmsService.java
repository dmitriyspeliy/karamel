package effective_mobile.com.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import effective_mobile.com.model.dto.rs.SmsAnswer;
import effective_mobile.com.model.entity.SmsInfo;
import effective_mobile.com.repository.SmsInfoRepository;
import effective_mobile.com.service.api.sms.SendSmsApi;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SendSmsApi sendSmsApi;
    private final SmsInfoRepository smsInfoRepository;

    public void sendSms(String extDealId, String phone, String text) {
        try {
            log.info("Запрос на отправку смс по телефону " + phone + " по сделке " + extDealId);
            SmsAnswer smsAnswer = sendSmsApi.sendSms(phone, text);
            SmsInfo smsInfo = new SmsInfo();
            SmsAnswer.Sms.Info info = smsAnswer.getSms().getInfo();
            String status = info.getStatus();
            if (status.equals("OK")) {
                smsInfo.setExtSmsId(info.getSmsId());
                smsInfo.setCost(info.getCost());
            } else {
                smsInfo.setExtSmsId(UUID.randomUUID().toString());
                smsInfo.setCost(BigDecimal.ZERO);
            }
            smsInfo.setStatus(status);
            smsInfo.setStatusCode(String.valueOf(info.getStatus_code()));
            smsInfo.setStatusText(info.getStatusText());
            smsInfo.setPhoneReceiver(phone);
            smsInfo.setSmsText(text);
            smsInfo.setSendTime(LocalDateTime.now());
            smsInfo.setBalance(smsAnswer.getBalance());
            smsInfo.setDealExtId(extDealId);
            smsInfoRepository.save(smsInfo);
            log.info("Сохраняем в бд информацию о смс по айди " + smsInfo.getExtSmsId());
        } catch (BadRequestException e) {
            log.error(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void hookProcessing(List<String> stringList) {
        for (String elem : stringList) {
            List<String> listVar = Arrays.stream(elem.split("\n")).toList();
            if (listVar.get(0).equals("sms_status")) {
                String extId = listVar.get(1);
                String code = listVar.get(2);
                LocalDateTime localDateTime = Instant.ofEpochSecond(Integer.parseInt(listVar.get(3))).atZone(ZoneId.systemDefault()).toLocalDateTime();
                Optional<SmsInfo> optionalSmsInfo = smsInfoRepository.findByExtSmsId(extId);
                if (optionalSmsInfo.isPresent()) {
                    optionalSmsInfo.get().setStatusCode(code);
                    optionalSmsInfo.get().setSendTime(localDateTime);
                    smsInfoRepository.save(optionalSmsInfo.get());
                    log.info("Sms обновлена с айди " + extId);
                } else {
                    log.warn("Could not find sms by id " + extId);
                }
            } else {
                break;
            }
        }
    }
}
