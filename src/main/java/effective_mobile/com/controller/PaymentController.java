package effective_mobile.com.controller;

import effective_mobile.com.model.dto.rs.PaymentResult;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.repository.InvoiceRepository;
import effective_mobile.com.service.PaymentService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final InvoiceRepository invoiceRepository;
    private final PaymentService paymentService;

    /**
     * Ищет инвойс по айди и возвращает статус
     */
    @GetMapping(value = "{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public String paymentStatus(@PathVariable UUID id) throws BadRequestException {
        Optional<Invoice> invoiceOptional = invoiceRepository.findByExtInvoiceId(id.toString());
        if (invoiceOptional.isPresent()) {
            return invoiceOptional.get().getStatus().toString();
        } else {
            throw new BadRequestException("No invoice by hash " + id);
        }
    }

    @PostMapping("/result")
    public void paymentResult(@RequestBody Object paymentResult) throws BadRequestException {
        System.out.println(paymentResult.toString());
        //paymentService.paymentProcessing(new PaymentResult());
    }
}
