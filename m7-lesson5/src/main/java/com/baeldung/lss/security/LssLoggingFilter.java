package com.baeldung.lss.security;

import java.io.IOException;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class LssLoggingFilter extends GenericFilterBean {

    private final Logger log = Logger.getLogger(LssLoggingFilter.class);

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String url = httpServletRequest.getRequestURL()
            .toString();
        final String queryString = Optional.ofNullable(httpServletRequest.getQueryString())
            .map(value -> "?" + value)
            .orElse("");
        log.info(String.format("applying LssLoggingFilter for URI: %s%s", url, queryString));

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
