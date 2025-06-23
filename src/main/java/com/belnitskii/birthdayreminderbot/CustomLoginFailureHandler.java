package com.belnitskii.birthdayreminderbot;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String errorMessage = "Invalid username or password";

        if (exception.getCause() instanceof RuntimeException
                && exception.getCause().getMessage().contains("did not confirm")) {
            errorMessage = "Please confirm your email first! Check your inbox.";
        }

        response.sendRedirect(request.getContextPath() + "/login?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
