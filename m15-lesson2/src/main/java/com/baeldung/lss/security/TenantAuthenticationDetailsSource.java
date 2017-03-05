package com.baeldung.lss.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, String> {

    @Override
    public String buildDetails(HttpServletRequest request) {
        return request.getParameter("tenant");
    }

}
