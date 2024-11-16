package effective_mobile.com.controller;

import effective_mobile.com.configuration.properties.CityProperties;
import effective_mobile.com.model.dto.rs.CityInfoResponse;
import effective_mobile.com.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CityInfoController {

    private final CityProperties cityProperties;

    /**
     * Возвращает информацию про конкретный город
     */
    @GetMapping("/city")
    public CityInfoResponse getCityInfo(@RequestParam(name = "city") String city) {
        log.info("Get city info by city " + city);
        CityProperties.Info info = cityProperties.getCityInfo().get(city);
        return new CityInfoResponse(
                info.getCityName(),
                info.getStartEndpoint(),
                info.getFinishEndpoint(),
                info.getAddress(),
                info.getManagerContactNumbers().get(0),
                info.getOfferLink());
    }

}
