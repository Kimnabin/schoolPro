package school.xxxx.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Tạo Bean PasswordEncoder để hash password
     * BCryptPasswordEncoder là một trong những thuật toán hash mạnh nhất
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strength = 12 (càng cao càng an toàn nhưng chậm hơn)
    }

    /**
     * Cấu hình bảo mật cho ứng dụng
     * Tạm thời disable security để không ảnh hưởng đến API hiện tại
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF cho REST API
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Cho phép tất cả request (tạm thời)
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // Ngăn clickjacking // Ngăn clickjacking
                );

        return http.build();
    }
}