package effective_mobile.com.service;

import effective_mobile.com.model.dto.GetPaymentLinkResponse;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.service.api.contact.AddContact;
import effective_mobile.com.service.api.deal.AddDeal;
import effective_mobile.com.service.api.payment.Payment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static effective_mobile.com.utils.UtilsMethods.checkVar;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final AddDeal addDeal;
    private final AddContact addContact;
    private final Payment payment;
    private final ContactRepository contactRepository;


    public GetPaymentLinkResponse bookEvent(RequestToBookingEvent requestBody) throws BadRequestException, URISyntaxException, MalformedURLException {
        checkVar(List.of(requestBody.getNumber()));
        String pay = payment.pay();
        Contact contact = addContact.addContact(requestBody.getContactName(), requestBody.getCity(),
                requestBody.getNumber(), requestBody.getSource());
        Deal deal = addDeal.addDeal(requestBody, contact, payment.pay());
        return new GetPaymentLinkResponse(UUID.randomUUID(), new URL("https://www.baeldung.com/"), Instant.now());
    }


}
