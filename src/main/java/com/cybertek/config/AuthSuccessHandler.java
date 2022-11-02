package com.cybertek.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities()); // it gets the roles from the GrantedAuthority class which we added from UserPrincipal class

        // redirect to a controller based on the role
        if (roles.contains("Admin")) {
            response.sendRedirect("/admin/user-create");
        }
        if (roles.contains("Manager")) {
            response.sendRedirect("/manager/task-create");
        }
        if (roles.contains("Employee")) {
            response.sendRedirect("/employee/pending-tasks");
        }
    }

}
