package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.DealInfo;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.repository.DealRepository;
import effective_mobile.com.utils.enums.CityInfo;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static effective_mobile.com.utils.UtilsMethods.getShortCityName;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneratedInfoService {

    private final DealRepository dealRepository;
    private final CityProperties cityProperties;

    public DealInfo getInfoByDealId(String dealId) throws BadRequestException {
        if(dealId != null && !dealId.equals("")) {
            Optional<Deal> optional = dealRepository.findByIdDealWithEventContact(Long.valueOf(dealId));
            if(optional.isPresent()) {
                CityProperties.Info info =
                        cityProperties.getCityInfo().get(getShortCityName(optional.get().getEvent().getCity()));
                String linkToSmsAction = CityInfo.getLinkToSmsAction(optional.get().getEvent().getCity());
                DealInfo dealInfo = new DealInfo();
                dealInfo.setTime(optional.get().getEvent().getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                dealInfo.setAddress(info.getAddress());
                dealInfo.setPrice(optional.get().getInvoice().getTotalSum().toString());
                dealInfo.setLink(linkToSmsAction);
                dealInfo.setKidCount(optional.get().getKidCount());
                dealInfo.setAdultCount(optional.get().getAdultCount());
                return dealInfo;
            }else {
                return new DealInfo();
            }
        }else {
          return new DealInfo();
        }
    }

}
