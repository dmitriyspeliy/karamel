package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.Cart;
import effective_mobile.com.model.dto.BookingCreationResult;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.GetPaymentLinkResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.service.api.contact.AddContact;
import effective_mobile.com.service.api.deal.AddDeal;
import effective_mobile.com.service.api.payment.Payment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final CityProperties cityProperties;

    @Value("${spring.current-city}")
    private String currentCity;

    public GetPaymentLinkResponse bookEvent(RequestToBookingEvent requestBody) throws BadRequestException {
        checkVar(List.of(requestBody.getNumber()));
        Contact contact = addContact.addContact(requestBody);
        Deal deal = addDeal.addDeal(requestBody, contact);
        BookingCreationResult bookingCreationResult = payment.pay(contact, deal, requestBody);
        return new GetPaymentLinkResponse(bookingCreationResult.id(),
                bookingCreationResult.invoiceLink(),
                Instant.now());
    }

    public Cart getBookingCart(UUID invoiceId) {
        CityProperties.Info info = cityProperties.getCityInfo().get(currentCity);

//
//        InvoiceInfo invoiceInfo = getInvoiceUseCase.getInvoiceById(invoiceId);
//        GetLeadUseCase.Lead lead = getLeadUseCase.getLeadWithContactsById(invoiceInfo.getLeadId())
//                .orElseThrow(() -> new LeadNotFoundException("Lead not found %s".formatted(invoiceId)));
//
//
//        return Cart.builder()
//                .leadId(lead.getId())
//                .contactId(lead.getContactId())
//                .name("Карамельная фабрика Деда Мороза")
//                .managerContactNumber(info.getManagerContactNumbers().get(0))
//                .paymentLink(invoiceInfo.getInvoiceLink())
//                .kidCount(lead.getKidCount())
//                .kidPrice(lead.getKidPrice())
//                .adultCount(lead.getAdultCount())
//                .adultPrice(lead.getAdultPrice())
//                .totalPrice(lead.getTotalPrice())
//                .kidAge(lead.getKidAge())
//                .address(lead.getEventAddress())
//                .time(lead.getTime())
//                .createdAt(invoiceInfo.getCreatedAt())
//                .build();
        return null;
    }


}
