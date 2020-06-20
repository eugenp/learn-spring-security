package com.baeldung.lss.spring;

import javax.servlet.Filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.provider.SamlServerConfiguration;
import org.springframework.security.saml.provider.service.authentication.SamlAuthenticationResponseFilter;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderServerBeanConfiguration;

import com.baeldung.lss.spring.security.SamlAuthenticationManager;

@Configuration
public class SamlSPBeanConfig extends SamlServiceProviderServerBeanConfiguration {

    private final SamlServerConfig config;

    public SamlSPBeanConfig(SamlServerConfig config) {
        this.config = config;
    }

    @Override
    protected SamlServerConfiguration getDefaultHostSamlServerConfiguration() {
        return config;
    }

    @Override
    public Filter spAuthenticationResponseFilter() {
        SamlAuthenticationResponseFilter filter = (SamlAuthenticationResponseFilter) super.spAuthenticationResponseFilter();
        filter.setAuthenticationManager(new SamlAuthenticationManager());
        return filter;
    }
}