package com.slice.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        System.out.println("🔥 SUCCESS HANDLER HIT 🔥");

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {

            String role = authority.getAuthority();
            System.out.println("🔥 ROLE FOUND: " + role);

            if ("ROLE_ADMIN".equals(role)) {
                response.sendRedirect("/admin/pizzas");
                return;
            }

            if ("ROLE_STAFF".equals(role)) {
                response.sendRedirect("/staff/dashboard");
                return;
            }

            if ("ROLE_CUSTOMER".equals(role)) {
                response.sendRedirect("/menu");
                return;
            }
        }

        // Safe fallback
        response.sendRedirect("/");
    }
}
