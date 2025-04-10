package com.belnitskii.birthdayreminderbot;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/person/list";

        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))){
            redirectUrl = "admin/list";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/person/list";
        }
        response.sendRedirect(redirectUrl);
    }
}
