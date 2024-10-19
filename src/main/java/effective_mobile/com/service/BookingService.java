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
import effective_mobile.com.service.api.deal.UpdateDealCity;
import effective_mobile.com.service.api.event.ChangeEventInBitrix;
import effective_mobile.com.service.api.payment.InvoiceRobokassa;
import effective_mobile.com.utils.UtilsMethods;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static effective_mobile.com.utils.UtilsMethods.getAge;

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
    private final UtilsMethods utilsMethods;
    private final UpdateDealCity updateDealCity;

    private Event event;
    private RequestToBookingEvent requestToBookingEvent;
    private BigDecimal sum;
    private Contact contact;
    private Deal deal;
    private Invoice invoice;

    public synchronized GetPaymentLinkResponse bookEvent(RequestToBookingEvent requestBody, String currentCity) throws BadRequestException {

        this.requestToBookingEvent = requestBody;

        event = eventService.findEventByNameAndCity(requestBody.getEventName(),
                cityProperties.getCityInfo().get(currentCity).getCityName());

        // бронируем места
        bookSeats();
        //делаем сброс в кэше, чтобы след запрос был уже с актуальными местами
        utilsMethods.cleanCashed(event);
        // считаем сумму
        makeSum();

        try {
            // регистрация сущностей в битриксе
            contact = addContact.addContact(requestBody);
            deal = addDeal.addDeal(sum, event, contact, requestBody.getPaidAdultCount(), requestBody.getChildrenCount(), currentCity);
            updateDealCity.refreshDealCity(deal.getExtDealId(), cityProperties.getCityInfo().get(currentCity).getBitrixFieldNum());
            // получение инвойса
            makeInvoice(currentCity);

        } catch (Exception e) {
            // если на этапе получение инвойса или создания сущностей что-то не получилось,
            // то делаем компенсирующую операцию для возвращая бронируемых мест
            log.error("Произошла ошибка на этапе получения инвойса");
            if (event.getType().contains("ШКОЛЬНЫЕ")) {
                changeEvent.undoChangingInSchoolEvent(event);
            } else if (event.getType().contains("СБОРНЫЕ")) {
                changeEvent.undoChangingInMixedEvent(requestToBookingEvent.getChildrenCount(),
                        requestToBookingEvent.getPaidAdultCount(), event);
            }
            utilsMethods.cleanCashed(event);
            try {
                invoice = new Invoice();
                invoice.setExtInvoiceId("NO");
                invoice.setInvoiceLink(new URL(""));
                invoice.setCreateAt(LocalDateTime.now());
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }


        // сохранили все сущности в бд
        saveEntitiesInDb();


        // отдаем ссылку
        return new GetPaymentLinkResponse(invoice.getExtInvoiceId(), invoice.getInvoiceLink(),
                invoice.getCreateAt().toInstant(ZoneOffset.of("+00:00")));
    }


    public Cart getBookingCart(UUID invoiceId, String currentCity) throws BadRequestException {
        CityProperties.Info info = cityProperties.getCityInfo().get(currentCity);

        Optional<Invoice> optionalInvoice = invoiceRepository.getDealAndEvent(invoiceId.toString());
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            Deal deal = invoice.getDeal();
            Contact contact = invoice.getDeal().getContact();
            Event event = invoice.getDeal().getEvent();
            if (event.getType().contains("ШКОЛЬНЫЕ")) {
                return Cart.builder()
                        .leadId(event.getId())
                        .contactId(Long.valueOf(contact.getExtContactId()))
                        .name("Карамельная фабрика Деда Мороза")
                        .managerContactNumber(info.getManagerContactNumbers().get(0))
                        .paymentLink(invoice.getInvoiceLink())
                        .kidCount(deal.getKidCount())
                        .kidPrice(invoice.getTotalSum())
                        .adultCount(deal.getAdultCount())
                        .adultPrice(BigDecimal.ZERO)
                        .totalPrice(invoice.getTotalSum())
                        .kidAge(getAge(deal.getKidAge()))
                        .address(info.getAddress())
                        .time(event.getTime().toInstant(ZoneOffset.of("+00:00")))
                        .createdAt(invoice.getCreateAt().toInstant(ZoneOffset.of("+00:00")))
                        .build();
            } else {
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
                        .kidAge(getAge(deal.getKidAge()))
                        .address(info.getAddress())
                        .time(event.getTime().toInstant(ZoneOffset.of("+00:00")))
                        .createdAt(invoice.getCreateAt().toInstant(ZoneOffset.of("+00:00")))
                        .build();
            }
        } else {
            throw new BadRequestException("No invoice by hash " + invoiceId);
        }
    }

    private void bookSeats() throws BadRequestException {
        if (event.getType().contains("ШКОЛЬНЫЕ")) {
            changeEvent.bookSchoolEvent(event);
        } else if (event.getType().contains("СБОРНЫЕ")) {
            changeEvent.bookMixedEvent(requestToBookingEvent.getChildrenCount(), requestToBookingEvent.getPaidAdultCount(), event);
        } else {
            throw new BadRequestException("Нет такого типа " + event.getType());
        }
    }

    private void makeSum() {
        if (event.getType().contains("ШКОЛЬНЫЕ")) {
            sum = BigDecimal.valueOf(5000L);
        } else if (event.getType().contains("СБОРНЫЕ")) {
            BigDecimal adultPrice = event.getAdultPrice();
            BigDecimal kidPrice = event.getKidPrice();
            sum = adultPrice.multiply(BigDecimal.valueOf(requestToBookingEvent.getPaidAdultCount()))
                    .add(
                            kidPrice.multiply(BigDecimal.valueOf(requestToBookingEvent.getChildrenCount())));
        }
    }

    private void makeInvoice(String currentCity) throws BadRequestException, MalformedURLException {
        invoice = null;
        invoice = invoiceRobokassa.generateInvoiceLink(sum, deal, currentCity);
    }

    private void saveEntitiesInDb() {
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
        event = eventRepository.save(event);


        List<Deal> deals = contact.getDeal();
        if (deals != null) {
            deals.add(save);
        } else {
            deals = new ArrayList<>();
            deals.add(save);
        }
        contact.setDeal(deals);
        contactRepository.save(contact);
    }


}
