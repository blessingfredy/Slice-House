package com.slice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.slice.security.CustomAuthenticationSuccessHandler;
import com.slice.security.CustomUserDetailsService;
import com.slice.security.OAuthUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private OAuthUserService oAuthUserService;


@Bean
public SecurityFilterChain filterChain(
        HttpSecurity http,
        DaoAuthenticationProvider authenticationProvider,
        CustomAuthenticationSuccessHandler successHandler
) throws Exception {

    http
        .authenticationProvider(authenticationProvider)

        .csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/", "/home", "/menu", "/about", "/contact",
                "/register", "/verify", "/login",
                "/css/**", "/images/**", "/uploads/**"
            ).permitAll()

            .requestMatchers("/cart/**", "/orders/**", "/checkout/**")
                .hasRole("CUSTOMER")

            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/staff/**").hasRole("STAFF")

            .anyRequest().authenticated()
        )

        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .successHandler(successHandler)
            .failureUrl("/login?error") 
            .permitAll()
        )
        .oauth2Login(oauth -> oauth
        	    .loginPage("/login")
        	    .userInfoEndpoint(userInfo ->
                userInfo.userService(oAuthUserService)
            )
        	    .successHandler(successHandler)
        	)

        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .permitAll()
        );

    return http.build();
}


    // 🔥 EXPLICIT AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider authenticationProvider) {

        return new ProviderManager(authenticationProvider);
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
