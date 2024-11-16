package effective_mobile.com.controller;

import effective_mobile.com.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class HookSmsProcessing {

    private final SmsService sms;

    @PostMapping(consumes = {"multipart/form-data"})
    public String getHook(@RequestParam Map<String, String> parameters) {
        sms.hookProcessing(new ArrayList<>(parameters.values()));
        return "100";
    }

}
