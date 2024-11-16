package effective_mobile.com.controller;

import effective_mobile.com.service.SmsService;
import effective_mobile.com.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HookSmsProcessing {

    private final SmsService sms;

    @PostMapping(consumes = {"multipart/form-data"})
    public String getHook(@RequestParam Map<String, String> parameters) {
        sms.hookProcessing(new ArrayList<>(parameters.values()));
        return "100";
    }

    @GetMapping("/test")
    public String get() throws BadRequestException {
        sms.sendSms("123123", "79526817361", "test");
        return "GET /v1/test";
    }
}
