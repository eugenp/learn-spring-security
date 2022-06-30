package com.baeldung.lsso;

public class TokenLiveTest {

    private static final String AUTH_SERVICE_BASE_URL = "http://localhost:8083/auth/realms/baeldung";
    private static final String CONNECT_TOKEN = AUTH_SERVICE_BASE_URL + "/protocol/openid-connect/token";
    private static final String SERVER_API_PROJECTS = "http://localhost:8081/lsso-resource-server/api/projects";

}
