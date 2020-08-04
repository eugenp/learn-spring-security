const AUTH_SERVER_BASE_URL = 'http://localhost:8083/auth/realms/baeldung';
const PROVIDER_CONFIGS = {
  AUTH_URL: AUTH_SERVER_BASE_URL + '/protocol/openid-connect/auth',
  CLIENT_ID: 'lssoClient',
  CONFIGURED_REDIRECT_URI: 'http://localhost:8082/lsso-client/modal-code-handler.html',
  TOKEN_URI: AUTH_SERVER_BASE_URL + '/protocol/openid-connect/token',
  USERINFO_URI: AUTH_SERVER_BASE_URL + '/protocol/openid-connect/userinfo',
  JWKS_URI: AUTH_SERVER_BASE_URL + '/protocol/openid-connect/certs',
  DEFAULT_SCOPES: ['profile'],
  USER_FIELDS: {
    USERNAME: 'preferred_username',
    LAST_NAME: 'family_name',
    FIRST_NAME: 'given_name',
    PICTURE: 'picture',
  },
  // Max Refresh Token Period for in seconds:
  REFRESH_TOKEN_PERIOD: 180,
};

window.PROVIDER_CONFIGS = PROVIDER_CONFIGS;
