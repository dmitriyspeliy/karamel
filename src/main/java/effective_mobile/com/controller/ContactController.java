package effective_mobile.com.controller;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.service.ContactService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * Апдейт почты по айди контакта и айди евента
     */
    @PatchMapping("/{id}")
    public void updateEmail(@PathVariable Long id, @RequestBody UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        contactService.updateEmail(id, contactRequestBody);
    }

}