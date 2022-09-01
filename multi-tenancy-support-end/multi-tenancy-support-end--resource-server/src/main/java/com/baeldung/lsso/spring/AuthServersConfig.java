package com.baeldung.lsso.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "oauth")
public class AuthServersConfig {

    // Map using issuer-uri as keys and the corresponding jwk-set-uri as value
    public final Map<String, String> issuersJwkSetURIs = new HashMap<>();

    public AuthServersConfig(Map<String, AuthServer> authServers) {
        authServers.values()
            .forEach(authServer -> issuersJwkSetURIs.put(authServer.getIssuerUri(), authServer.getJWKSetUrl()));

    }

    public Map<String, String> getIssuersJwkSetURIs() {
        return issuersJwkSetURIs;
    }

    public Set<String> getTrustedIssuerUris() {
        return issuersJwkSetURIs.keySet();
    }

    public static class AuthServer {
        private String jwkSetUrl;
        private String issuerUri;

        public String getJWKSetUrl() {
            return jwkSetUrl;
        }

        public void setJWKSetUrl(String jwkSetUrl) {
            this.jwkSetUrl = jwkSetUrl;
        }

        public String getIssuerUri() {
            return issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }
    }
}
