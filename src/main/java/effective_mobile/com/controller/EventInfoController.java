package effective_mobile.com.controller;

import effective_mobile.com.service.EmailService;
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
    private final EmailService emailService;

    @GetMapping("{dealId}")
    public String sendInfoAboutEvent(@PathVariable("dealId") String dealId, Model model) throws BadRequestException {
        emailService.sendEmail("dmitriypospelov93@gmail.com", "TEST", "TEST");
        model.addAttribute("info", generatedInfo.getInfoByDealId(dealId));
        return "info-user";
    }

}
