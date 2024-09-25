package effective_mobile.com.configuration.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {
    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
