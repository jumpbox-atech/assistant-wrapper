package africa.za.atech.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class WebApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    private final Environment environment;

    @Value("${atech.log.print-properties}")
    private boolean printProperties;

    @Value("${atech.spring.datasource.jdbc_url}")
    private String jdbcHostUrl;

    @Override
    public void run(String... args) {
        printProps();
    }

    private void printProps() {
        if (printProperties) {
            for (PropertySource<?> propertySource : ((org.springframework.core.env.AbstractEnvironment) environment).getPropertySources()) {
                if (propertySource.getName().contains("application") && propertySource.getName().contains(".properties")) {
                    if (propertySource instanceof org.springframework.core.env.MapPropertySource) {
                        ((org.springframework.core.env.MapPropertySource) propertySource).getSource().forEach((key, value) -> {
                            if (key.startsWith("atech.")) {
                                log.info("*** ATECH property: {} :: [{}]", key, environment.getProperty(key));
                            }
                        });
                    }
                }
            }
        }
        log.info("*** SPRING jdbc url: [{}]", jdbcHostUrl);
        log.info("*** SPRING property: {} :: [{}]", "logging.file.name", environment.getProperty("logging.file.name"));
    }

}