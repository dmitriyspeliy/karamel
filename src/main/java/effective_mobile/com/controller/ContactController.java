package effective_mobile.com.controller;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final UpdateDealComment updateContact;

    @PatchMapping("/{id}")
    public void updateEmail(@PathVariable Long id, @RequestBody UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        updateContact.refreshCommentDeal(
                String.valueOf(id),
                "Билеты отправлены на почту " + contactRequestBody.getEmail()
        );
    }

}