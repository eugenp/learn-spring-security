package com.baeldung.lss.spring;

import static org.springframework.security.saml.provider.service.config.SamlServiceProviderSecurityDsl.serviceProvider;

import java.security.KeyStore;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml.key.SimpleKey;
import org.springframework.security.saml.provider.config.RotatingKeys;
import org.springframework.security.saml.provider.service.config.SamlServiceProviderSecurityConfiguration;

@Configuration
@Order(1)
public class SamlSecurityConfig extends SamlServiceProviderSecurityConfiguration {

    private SamlServerConfig samlServerConfig;

    public SamlSecurityConfig(SamlSPBeanConfig beanConfig, SamlServerConfig samlServerConfig) {
        super("/saml/sp/", beanConfig);
        this.samlServerConfig = samlServerConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.apply(serviceProvider())
            .configure(samlServerConfig)
            .rotatingKeys(rotatingKeys());
    }

    @Bean
    public RotatingKeys rotatingKeys() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new ClassPathResource("samlKeystore.jks").getInputStream(), "nalle123".toCharArray());
        String cer = Base64.getEncoder().encodeToString(keyStore.getCertificate("apollo").getEncoded());
        RotatingKeys keys = new RotatingKeys();
        keys.setActive(new SimpleKey("apollo",null,cer,null,null));
        return keys;
    }
}