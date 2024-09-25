package effective_mobile.com.controller;

import effective_mobile.com.model.dto.GetPaymentLinkResponse;
import effective_mobile.com.model.dto.rq.RequestToBookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

//
//    @PostMapping("/group")
//    public ResponseEntity<GetPaymentLinkResponse> createGroupBooking(@RequestBody RequestToBookingEvent requestBody) {
//
//
//        return ResponseEntity.ok(new GetPaymentLinkResponse(result.invoiceId(), result.invoiceLink(), Instant.now()));
//    }


}