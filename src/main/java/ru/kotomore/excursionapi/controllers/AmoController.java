package ru.kotomore.excursionapi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotomore.excursionapi.services.AmoCRMServiceUseCase;

@RestController
@RequiredArgsConstructor
public class AmoController {
    private final AmoCRMServiceUseCase amoCRMServiceUseCase;

    @GetMapping
    public ResponseEntity<?> getLeads() {
        return ResponseEntity.ok(amoCRMServiceUseCase.getLeadsByPhone("+74951270967"));
    }

}
