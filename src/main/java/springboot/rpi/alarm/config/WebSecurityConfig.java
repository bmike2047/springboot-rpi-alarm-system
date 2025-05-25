package springboot.rpi.alarm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import springboot.rpi.alarm.ApplicationProperties;

/**
 * Configure application security
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Allow only components for the web interface and actuator.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/assets/**", "/actuator/health/**", "/actuator/info").permitAll() // <-- remove this ***
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/hub", true)
                        .permitAll()
                )

                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    /**
     * Setup authentication with credentials from applicationProperties.
     *
     * @return InMemoryUserDetailsManager
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername(applicationProperties.getDefaultUser())
                .password(passwordEncoder().encode(applicationProperties.getDefaultPassword()))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Setup hashing function.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}