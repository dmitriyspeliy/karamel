package effective_mobile.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "effective_mobile.com.configuration.properties")
public class KaramelApplication {

	public static void main(String[] args) {
		SpringApplication.run(KaramelApplication.class, args);
	}

}
