package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.model.dto.rs.GetBookingCartResponse;
import effective_mobile.com.model.dto.rs.GetPaymentLinkResponse;
import effective_mobile.com.service.BookingService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

import static effective_mobile.com.mapper.Mapper.toDto;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor

public class BookingController {

    private final BookingService bookingService;

    //@GetMapping("/cart/{id}")
    public ResponseEntity<GetBookingCartResponse> getCart(@PathVariable UUID id) {
        return ResponseEntity.ok(toDto(bookingService.getBookingCart(id)));
    }

    @PostMapping("/group")
    public ResponseEntity<GetPaymentLinkResponse> createGroupBooking(@RequestBody RequestToBookingEvent requestBody)
            throws BadRequestException {
        return ResponseEntity.ok(bookingService.bookEvent(requestBody));
    }
}