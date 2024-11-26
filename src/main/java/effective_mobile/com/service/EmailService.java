package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.utils.exception.BadRequestException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static effective_mobile.com.utils.UtilsMethods.getShortCityName;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final CityProperties cityProperties;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String username;


    public void sendEmail(String email, String messageHeader, String message) throws BadRequestException {

        try {
            log.info("Отправка на почту " + email);
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(email);
            helper.setFrom(username);
            helper.setSubject(messageHeader);
            helper.setText(message, true);

            javaMailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            throw new BadRequestException(email);
        }
    }

    public String createMessage(Deal deal) throws BadRequestException {
        Event event = deal.getEvent();
        Resource resource = new ClassPathResource("ticket-email-template.html");
        String emailContent;

        try {
            emailContent = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

        try {

            CityProperties.Info info = cityProperties.getCityInfo().get(getShortCityName(event.getCity()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

            return emailContent
                    .replace("{email}", username)
                    .replace("{phone1}", String.join(", ",
                            info.getManagerContactNumbers()))
                    .replace("{vkLink}", info.getVkLink())
                    .replace("{type}", deal.getType())
                    .replace("{date}", event.getTime().format(formatter))
                    .replace("{time}", event.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .replace("{price}", deal.getInvoice().getTotalSum().toString())
                    .replace("{address}", info.getAddress())
                    .replace("{adultCount}", deal.getAdultCount().toString())
                    .replace("{kidCount}", deal.getKidCount().toString());
        } catch (Exception e) {
            throw new BadRequestException("Failed prepare email template", e.getMessage());
        }
    }

    public String createMessageFroNotify(Deal deal) throws BadRequestException {
        Event event = deal.getEvent();
        Resource resource = new ClassPathResource("ticket-email-notify.html");
        String emailContent;

        try {
            emailContent = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

        try {

            CityProperties.Info info = cityProperties.getCityInfo().get(getShortCityName(event.getCity()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

            return emailContent
                    .replace("{type}", "СБОРНЫЕ ГРУППЫ")
                    .replace("{date}", event.getTime().format(formatter))
                    .replace("{time}", event.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .replace("{price}", deal.getInvoice().getTotalSum().toString())
                    .replace("{address}", info.getAddress())
                    .replace("{adultCount}", deal.getAdultCount().toString())
                    .replace("{kidCount}", deal.getKidCount().toString());
        } catch (Exception e) {
            throw new BadRequestException("Failed prepare email template", e.getMessage());
        }
    }


}