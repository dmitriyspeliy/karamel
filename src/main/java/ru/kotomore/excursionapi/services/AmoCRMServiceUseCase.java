package ru.kotomore.excursionapi.services;

import ru.kotomore.excursionapi.dto.Lead;

import java.util.List;

public interface AmoCRMServiceUseCase {
    List<Lead> getLeadsByPhone(String phone);

}
