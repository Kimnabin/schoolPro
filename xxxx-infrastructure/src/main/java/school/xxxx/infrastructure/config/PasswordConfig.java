package school.xxxx.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Configuration - TÁCH RIÊNG để tránh circular dependency
 */
@Configuration
public class PasswordConfig {

    /**
     * Password Encoder Bean - TÁCH RIÊNG khỏi SecurityConfig
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
