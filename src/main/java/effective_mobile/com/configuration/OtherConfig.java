package effective_mobile.com.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class OtherConfig {

    @Bean
    public Executor jobExecutor() {
        return Executors.newCachedThreadPool();
    }

}
