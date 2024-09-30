package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.Cart;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.GetPaymentLinkResponse;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.repository.EventRepository;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.api.contact.AddContact;
import effective_mobile.com.service.api.deal.AddDeal;
import effective_mobile.com.service.api.event.ChangeEventInBitrix;
import effective_mobile.com.service.api.payment.InvoiceRobokassa;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final AddDeal addDeal;
    private final AddContact addContact;
    private final InvoiceRobokassa invoiceRobokassa;
    private final CityProperties cityProperties;
    private final EventService eventService;
    private final DealRepository dealRepository;
    private final EventRepository eventRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContactRepository contactRepository;
    private final ChangeEventInBitrix changeEvent;


    @Value("${spring.current-city}")
    private String currentCity;

    public synchronized GetPaymentLinkResponse bookEvent(RequestToBookingEvent requestBody) throws BadRequestException {
        Event event = eventService.findEventByName(requestBody.getEventName());

        if (event.getType().equalsIgnoreCase("Школьные")) {
            changeEvent.bookSchoolEvent(event);
        } else if (event.getType().equalsIgnoreCase("Сборные")) {
            changeEvent.bookMixedEvent(requestBody.getChildrenCount(), requestBody.getPaidAdultCount(), event);
        } else {
            // TODO тут нужно будет добавить кастомное исключение, хотя возможно оно и не нужно
            throw new RuntimeException();
        }

        // считаем сумму
        BigDecimal adultPrice = event.getAdultPrice();
        BigDecimal kidPrice = event.getKidPrice();
        BigDecimal sum = adultPrice.multiply(BigDecimal.valueOf(requestBody.getPaidAdultCount()))
                .add(
                        kidPrice.multiply(BigDecimal.valueOf(requestBody.getChildrenCount())));

        // регистрация сущностей в битриксе
        Contact contact = addContact.addContact(requestBody);
        Deal deal = addDeal.addDeal(sum, event, contact, requestBody.getPaidAdultCount(), requestBody.getChildrenCount());

        // получение инвойса
        Invoice invoice = null;
        try {
            invoice = invoiceRobokassa.generateInvoiceLink(sum, deal);
        } catch (Exception e) {
            if (event.getType().equalsIgnoreCase("Школьные")) {
                changeEvent.undoChangingInSchoolEvent(event);
            } else {
                changeEvent.undoChangingInMixedEvent(requestBody.getChildrenCount(),
                        requestBody.getPaidAdultCount(), event);
            }

            // нужно удалять сделку ?
        }

        // TODO тут должно быть добавление сделки к слоту

        // сохранили все сущности в бд
        deal.setContact(contact);
        deal.setEvent(event);
        Deal save = dealRepository.save(deal);

        List<Deal> dealList = event.getDealList();
        if (dealList != null) {
            dealList.add(save);
        } else {
            dealList = new ArrayList<>();
            dealList.add(save);
        }
        event.setDealList(dealList);
        eventRepository.save(event);


        List<Deal> deals = contact.getDeal();
        if (deals != null) {
            deals.add(save);
        } else {
            deals = new ArrayList<>();
            deals.add(save);
        }
        contact.setDeal(deals);
        contactRepository.save(contact);


        return new GetPaymentLinkResponse(invoice.getExtInvoiceId(), invoice.getInvoiceLink(),
                invoice.getCreateAt().toInstant(ZoneOffset.of("+00:00")));
    }


    public Cart getBookingCart(UUID invoiceId) throws BadRequestException {
        CityProperties.Info info = cityProperties.getCityInfo().get(currentCity);

        Optional<Invoice> optionalInvoice = invoiceRepository.findByExtInvoiceId(invoiceId.toString());
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            Deal deal = invoice.getDeal();
            Contact contact = invoice.getDeal().getContact();
            Event event = invoice.getDeal().getEvent();
            return Cart.builder()
                    .leadId(event.getId())
                    .contactId(Long.valueOf(contact.getExtContactId()))
                    .name("Карамельная фабрика Деда Мороза")
                    .managerContactNumber(info.getManagerContactNumbers().get(0))
                    .paymentLink(invoice.getInvoiceLink())
                    .kidCount(deal.getKidCount())
                    .kidPrice(deal.getKidPrice())
                    .adultCount(deal.getAdultCount())
                    .adultPrice(deal.getAdultPrice())
                    .totalPrice(invoice.getTotalSum())
                    .kidAge(deal.getKidAge())
                    .address(info.getAddress())
                    .time(event.getTime().toInstant(ZoneOffset.of("+00:00")))
                    .createdAt(invoice.getCreateAt().toInstant(ZoneOffset.of("+00:00")))
                    .build();
        } else {
            throw new BadRequestException("No invoice by hash " + invoiceId);
        }
    }


}
