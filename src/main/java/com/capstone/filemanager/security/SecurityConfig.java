package com.capstone.filemanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/files/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form.disable());

        return http.build();
    }
}



//    if the lines below is active, this temporarily deactivates spring security login.
//    doing this just for right now as i build the other features

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).formLogin(form->form.disable());
//
//        return http.build();
//    }
//

//    if the lines below is active, this temporarily deactivates spring security login.
//    doing this just for right now as i build the other features
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).formLogin(form->form.disable());
//
//        return http.build();
//    }
//
