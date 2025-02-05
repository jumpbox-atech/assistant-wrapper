package africa.za.atech.spring.aio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;

@Slf4j
@Configuration
public class Database {

    @Value("${atech.spring.datasource.type.driver-class-name}")
    private String driverClassName;

    @Value("${atech.spring.datasource.jdbc_url}")
    private String jdbcHostUrl;

    @Value("${atech.spring.datasource.default-schema}")
    private String schema;

    @Value("${atech.spring.datasource.uname}")
    private String uname;

    @Value("${atech.spring.datasource.pwd}")
    private String pwd;

    @Value("${atech.json.serialise.date-format}")
    private String jacksonDateFormat;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setSchema(schema);
        dataSource.setUsername(uname);
        dataSource.setPassword(pwd);
        dataSource.setUrl(jdbcHostUrl);
        return dataSource;
    }

    @Bean
    @ConditionalOnProperty(name = "atech.json.serialise.date-format")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (!jacksonDateFormat.isEmpty()) {
            if (!jacksonDateFormat.equals("null")) {
                log.info("Jackson serialization pattern registered as: {}", jacksonDateFormat);
                SimpleDateFormat dateFormat = new SimpleDateFormat(jacksonDateFormat);
                objectMapper.setDateFormat(dateFormat);
            }
        }
        return objectMapper;
    }

}
