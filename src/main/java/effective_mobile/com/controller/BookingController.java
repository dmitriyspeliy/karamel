package effective_mobile.com.controller;

import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.GetBookingCartResponse;
import effective_mobile.com.model.dto.rs.GetPaymentLinkResponse;
import effective_mobile.com.service.BookingService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static effective_mobile.com.mapper.Mapper.toDto;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor

public class BookingController {

    private final BookingService bookingService;

    /**
     * Карточка заказа после бронирования
     *
     * @param id счета
     * @return дто заказа
     */
    @GetMapping("/cart/{id}")
    public ResponseEntity<GetBookingCartResponse> getCart(@PathVariable UUID id) throws BadRequestException {
        return ResponseEntity.ok(toDto(bookingService.getBookingCart(id)));
    }

    /**
     * Для бронирования сборных групп
     */
    @PostMapping("/gathered")
    public ResponseEntity<GetPaymentLinkResponse> createGroupBooking(@RequestBody RequestToBookingEvent requestBody)
            throws BadRequestException {
        return ResponseEntity.ok(bookingService.bookEvent(requestBody));
    }

    /**
     * Для бронирования школьных групп
     */
    @PostMapping("/group")
    public ResponseEntity<GetPaymentLinkResponse> createGroupBookingGroup(@RequestBody RequestToBookingEvent requestBody)
            throws BadRequestException {
        return ResponseEntity.ok(bookingService.bookEvent(requestBody));
    }
}