package effective_mobile.com.service;

import effective_mobile.com.model.dto.rq.UpdateContactRequestBody;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.service.api.deal.UpdateDealComment;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final UpdateDealComment updateContact;
    private final DealRepository dealRepository;


    public void updateEmail(Long id, UpdateContactRequestBody contactRequestBody) throws BadRequestException {
        String extDealId = dealRepository.findDealByContactIdAdnEventId(String.valueOf(id), contactRequestBody.getLeadId());
        if (extDealId != null && !extDealId.equals("")) {
            updateContact.refreshCommentDeal(
                    extDealId,
                    "Билеты отправлены на почту " + contactRequestBody.getEmail()
            );
        } else {
            throw new BadRequestException("No contact by ext id " + id);
        }
    }
}
