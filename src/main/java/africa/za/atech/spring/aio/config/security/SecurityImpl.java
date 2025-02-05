package africa.za.atech.spring.aio.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityImpl {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth

                .requestMatchers("/css/*.css").permitAll()
                .requestMatchers("/js/*.js").permitAll()
                .requestMatchers("/images/*.png").permitAll()

                .requestMatchers("/error").permitAll()
                .requestMatchers("/login").permitAll()

                .requestMatchers("/register/**").permitAll()
                .requestMatchers("/forgot").permitAll()

                .requestMatchers("/admin/**").hasAnyRole("MANAGER", "ADMIN")

                .requestMatchers("/chat/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/profile/**").hasAnyRole("USER", "MANAGER", "ADMIN")

                .anyRequest().authenticated()
        );

        http.formLogin(auth -> auth
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll());

        http.logout(auth -> auth
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
        );
        return http.build();
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}
