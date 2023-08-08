package ru.kotomore.excursionapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kotomore.excursionapi.services.AmoCRMServiceUseCase;

@SpringBootApplication
public class ExcursionApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcursionApiApplication.class, args);
    }

}
