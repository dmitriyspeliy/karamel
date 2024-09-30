package effective_mobile.com.service;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final UpdateDealComment updateContact;
    private final DealRepository dealRepository;


    public void updateEmail(Long id, UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        Optional<Deal> dealOptional = dealRepository.findDealByContactIdAdnEventId(String.valueOf(id), contactRequestBody.getLeadId());
        if (dealOptional.isPresent()) {
            String extId = dealOptional.get().getExtDealId();
            updateContact.refreshCommentDeal(
                    extId,
                    "Билеты отправлены на почту " + contactRequestBody.getEmail()
            );
        } else {
            throw new BadRequestException("No contact by ext id " + id);
        }
    }
}
