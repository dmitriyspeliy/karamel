package effective_mobile.com.controller;

import effective_mobile.com.service.GeneratedInfoService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user-info/")
public class EventInfoController {

    private final GeneratedInfoService generatedInfo;

    @GetMapping("{dealId}")
    public String sendInfoAboutEvent(@PathVariable("dealId") String dealId, Model model) throws BadRequestException {
        model.addAttribute("info", generatedInfo.getInfoByDealId(dealId));
        return "info-user";
    }

}
