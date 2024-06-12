package thermolearn.backend.api.configs;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/user/register").permitAll()
                .requestMatchers("/api/v1/user/authenticate").permitAll()
                .requestMatchers("/api/v1/user/send-new-registration-code").permitAll()
                .requestMatchers("/api/v1/user/verify-registration-code").permitAll()
                .requestMatchers("/api/v1/user/send-forgotten-password-code").permitAll()
                .requestMatchers("/api/v1/user/verify-forgotten-password-code").permitAll()
                .requestMatchers("/api/v1/user/reset-forgotten-password").permitAll()
                .requestMatchers("/api/v1/thermostat/get-thermostat-by-mac-address").permitAll()
                .requestMatchers("/api/v1/thermostat/update-temperature").permitAll()
                .requestMatchers("/api/v1/thermostat/get-schedule-by-thermostat-id").permitAll()
                .requestMatchers("/api/v1/log/get-user-logs-by-thermostat-id").permitAll()
                .requestMatchers("/api/v1/thermostat-log/save-log").permitAll()
                .requestMatchers("/api/v1/log/get-latest-user-log-by-thermostat-id").permitAll()
                .requestMatchers("/api/v1/user/get-user-distance-from-home").permitAll()
                .requestMatchers("/api/v1/thermostat/set-thermostat-fingerprint").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
