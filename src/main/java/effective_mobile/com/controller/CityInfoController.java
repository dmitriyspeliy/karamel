package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rs.CityInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CityInfoController {

    private final CityProperties cityProperties;

    /**
     * Возвращает информацию про конкретный город
     */
    @GetMapping("/city")
    public CityInfoResponse getCityInfo(@RequestParam(name = "city") String city) {
        CityProperties.Info info = cityProperties.getCityInfo().get(city);
        return new CityInfoResponse(
                info.getCityName(),
                info.getStartEndpoint(),
                info.getFinishEndpoint(),
                info.getAddress(),
                info.getManagerContactNumbers().get(0),
                info.getOfferLink(),
                info.getMaxCapacity(),
                info.getMinCapacity(),
                info.getRefundPeriod());
    }

}
