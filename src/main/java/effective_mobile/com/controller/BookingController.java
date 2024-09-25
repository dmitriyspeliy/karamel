package effective_mobile.com.controller;

import effective_mobile.com.model.dto.GetPaymentLinkResponse;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import effective_mobile.com.service.BookingService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Instant;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor

public class BookingController {

    private final BookingService bookingService;
    @PostMapping("/group")
    public ResponseEntity<GetPaymentLinkResponse> createGroupBooking(@RequestBody RequestToBookingEvent requestBody)
            throws MalformedURLException, BadRequestException, URISyntaxException {
        return ResponseEntity.ok(bookingService.bookEvent(requestBody));
    }
}