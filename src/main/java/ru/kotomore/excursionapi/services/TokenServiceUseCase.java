package ru.kotomore.excursionapi.services;

import ru.kotomore.excursionapi.models.TokenResponse;

public interface TokenServiceUseCase {
    TokenResponse getTokenResponse();

    TokenResponse refreshToken(TokenResponse tokenResponse);
}
