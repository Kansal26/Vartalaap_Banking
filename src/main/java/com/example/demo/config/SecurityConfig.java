package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserService userService;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(userService)
                                .passwordEncoder(passwordEncoder())
                                .and()
                                .build();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/index", "/login", "/register", "/css/**", "/js/**", "/h2-console/**", "/images/**",
                                                                "/forgot-password", "/verify-otp", "/reset-password")
                                                .permitAll()

                                                .requestMatchers("/manager_dashboard", "/manager/**").hasRole("MANAGER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/role-redirect", true)
                                                .permitAll())
                                .logout(logout -> logout.permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**") // Allow POST requests to H2
                                )
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()) // Allow H2 UI to render in
                                                                                           // frame
                                                .cacheControl(cache -> cache.disable()) // Disable caching
                                                .addHeaderWriter((request, response) -> {
                                                        response.setHeader("Pragma", "no-cache");
                                                        response.setHeader("Expires", "0");
                                                        // Stronger Cache-Control
                                                        response.setHeader("Cache-Control",
                                                                        "no-cache, no-store, must-revalidate, max-age=0");
                                                }));

                return http.build();
        }
}
