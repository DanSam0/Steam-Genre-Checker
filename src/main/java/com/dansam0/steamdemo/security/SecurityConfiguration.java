package com.dansam0.steamdemo.security;

import com.dansam0.steamdemo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ROLE_CREATOR > ROLE_ADMIN \n ROLE_ADMIN > ROLE_USER";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {
        http
            .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers("/register").permitAll()
                            .requestMatchers(HttpMethod.GET, "/", "/searchByName", "/search", "/info", "/styles/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/find").hasRole("USER")
                            .requestMatchers(HttpMethod.POST, "/find").hasRole("USER")
                            .requestMatchers("/my/**").hasRole("ADMIN")
                            .requestMatchers("/users/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
            )
            .formLogin(form ->
                    form
                            .loginPage("/login")
                            .loginProcessingUrl("/authenticateUser")
                            .successHandler(customAuthenticationSuccessHandler)
                            .permitAll()
            )
            .logout(LogoutConfigurer::permitAll
            )
            .exceptionHandling(configurer ->
                    configurer.accessDeniedPage("/forbidden")
            );

        //http.csrf(csrf -> csrf.disable());

        return http.build();
    }

}
