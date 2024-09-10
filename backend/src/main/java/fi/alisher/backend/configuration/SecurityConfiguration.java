package fi.alisher.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> {
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            csrf.ignoringRequestMatchers("/api"); // ignore for interaction access, since the client frontend is missing
        })
        .headers(h -> {
            h.xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
            h.contentSecurityPolicy(cps -> cps.policyDirectives("style-src 'self'; script-src 'self'; form-action 'self'"));
        })
        .authorizeHttpRequests(r-> {
            r.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll();
            r.requestMatchers(HttpMethod.POST, "/api").permitAll();
        });
        return http.build();
    }
}
