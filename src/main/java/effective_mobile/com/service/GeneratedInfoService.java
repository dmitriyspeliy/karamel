package effective_mobile.com.service;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.DealInfo;
import effective_mobile.com.model.entity.Deal;
import effective_mobile.com.model.entity.Event;
import effective_mobile.com.model.entity.Invoice;
import effective_mobile.com.utils.enums.CityInfo;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static effective_mobile.com.utils.UtilsMethods.getShortCityName;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneratedInfoService {

    private final CashedUserInfo cashedUserInfo;
    private final CityProperties cityProperties;

    public DealInfo getInfoByDealId(String dealId) throws BadRequestException {
        if (checkNum(dealId)) {
            Deal deal = cashedUserInfo.cashedUserInfo(Long.valueOf(dealId));
            if (deal != null) {
                Event event = deal.getEvent();
                Invoice invoice = deal.getInvoice();
                CityProperties.Info info =
                        cityProperties.getCityInfo().get(getShortCityName(event.getCity()));
                String linkToSmsAction = CityInfo.getLinkToSmsAction(event.getCity());
                DealInfo dealInfo = new DealInfo();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));
                dealInfo.setDate(event.getTime().format(formatter));
                dealInfo.setTime(event.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                dealInfo.setAddress(info.getAddress());
                dealInfo.setPrice(invoice.getTotalSum().toString());
                dealInfo.setLink(linkToSmsAction);
                dealInfo.setKidCount(deal.getKidCount());
                dealInfo.setAdultCount(deal.getAdultCount());
                return dealInfo;
            } else {
                return new DealInfo();
            }
        } else {
            return new DealInfo();
        }
    }

    public static boolean checkNum(String strNum) {
        if (strNum == null || strNum.equals("")) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
            if (d <= 0 || d > 10_000) {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
