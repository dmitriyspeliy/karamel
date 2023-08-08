package ru.kotomore.excursionapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kotomore.excursionapi.models.TokenResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService implements TokenServiceUseCase {
    @Value("${amocrm.client_id}")
    private String CLIENT_ID;
    @Value("${amocrm.client_secret}")
    private String CLIENT_SECRET;
    @Value("${amocrm.code}")
    private String ACCESS_CODE;
    @Value("${amocrm.redirect_uri}")
    private String REDIRECT_URL;
    @Value("${amocrm.access_token_link}")
    private String AMO_API_URL;
    @Value("${amocrm.token_file_path}")
    private String TOKEN_FILE_PATH;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    @Cacheable(cacheNames = "token", key = "'accessToken'")
    public TokenResponse getTokenResponse() {
        if (Files.exists(Paths.get(TOKEN_FILE_PATH))) {
            return readTokenFromFile();
        } else {
            TokenResponse tokenResponse = getAccessTokenFromCode();
            saveTokenToFile(tokenResponse);
            return tokenResponse;
        }
    }

    @Override
    @CachePut(cacheNames = "token", key = "'accessToken'")
    public TokenResponse refreshToken(TokenResponse tokenResponse) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", CLIENT_ID);
        requestBody.put("client_secret", CLIENT_SECRET);
        requestBody.put("grant_type", "refresh_token");
        requestBody.put("refresh_token", tokenResponse.getRefreshToken());
        requestBody.put("redirect_uri", REDIRECT_URL);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(AMO_API_URL + "/oauth2/access_token", requestBody, TokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    private TokenResponse getAccessTokenFromCode() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", CLIENT_ID);
        requestBody.put("client_secret", CLIENT_SECRET);
        requestBody.put("grant_type", "authorization_code");
        requestBody.put("code", ACCESS_CODE);
        requestBody.put("redirect_uri", REDIRECT_URL);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(AMO_API_URL + "/oauth2/access_token",
                requestBody, TokenResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

    private void saveTokenToFile(TokenResponse tokenResponse) {
        try {
            objectMapper.writeValue(new File(TOKEN_FILE_PATH), tokenResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TokenResponse readTokenFromFile() {
        try {
            return objectMapper.readValue(new File(TOKEN_FILE_PATH), TokenResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
