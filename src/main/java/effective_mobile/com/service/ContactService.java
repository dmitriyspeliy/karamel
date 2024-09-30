package effective_mobile.com.service;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final UpdateDealComment updateContact;
    private final ContactRepository contactRepository;


    public void updateEmail(Long id, UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        Optional<Contact> optionalContact = contactRepository.findByExtContactId(String.valueOf(id));
        if (optionalContact.isPresent()) {
            List<Deal> deal = optionalContact.get().getDeal();
            for (Deal deal1 : deal) {
                if (deal1.getEvent().getId().equals(contactRequestBody.getLeadId())) {
                    String extId = deal1.getExtDealId();
                    updateContact.refreshCommentDeal(
                            extId,
                            "Билеты отправлены на почту " + contactRequestBody.getEmail()
                    );
                    break;
                }
            }
        } else {
            throw new BadRequestException("No contact by ext id " + id);
        }
    }
}
