package effective_mobile.com.service;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.model.entity.Contact;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.ContactRepository;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final DealRepository dealRepository;
    private final ContactRepository contactRepository;


    public void updateEmail(Long id, UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        String extDealId = dealRepository.findDealByContactIdAdnEventId(String.valueOf(id), contactRequestBody.getLeadId());
        if (extDealId != null && !extDealId.equals("")) {
            updateContactEmailInDb(extDealId, contactRequestBody);
        } else {
            throw new BadRequestException("No contact by ext id " + id);
        }
    }

    private void updateContactEmailInDb(String extDealId, UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        Optional<Deal> dealOptional = dealRepository.findByExtDealId(extDealId);
        if (dealOptional.isPresent()) {
            Contact contact = dealOptional.get().getContact();
            contact.setEmail(contactRequestBody.getEmail());
            contactRepository.save(contact);
            log.info("Сохранили почту " + contactRequestBody.getEmail() + " в бд для контакта " + contact.getExtContactId());
        } else {
            throw new BadRequestException("Нет сделки по идентификатору " + extDealId);
        }
    }
}
