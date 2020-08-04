const VIEWS = {
  PROJECTS: 'projects',
  PROFILE: 'profile',
  LOGIN: 'login',
};

// App component:
const App = () => {
  const [user, setUser] = React.useState(null),
    [auth, _setAuth] = React.useState(null),
    [authRequest, _setAuthRequest] = React.useState({
      codeVerifier: '',
      state: '',
    }),
    [modal, setModal] = React.useState(null),
    [currentView, setCurrentView] = React.useState(VIEWS.LOGIN),
    [scopes, _setScopes] = React.useState({
      read: true,
      write: true,
    });

  // use refs for callbacks
  const refAuthRequest = React.useRef(authRequest);
  const setAuthRequest = (data) => {
    refAuthRequest.current = data;
    _setAuthRequest(data);
  };
  const refScopes = React.useRef(scopes);
  const setScopes = (data) => {
    refScopes.current = data;
    _setScopes(data);
  };
  const refAuth = React.useRef(auth);
  const setAuth = (data) => {
    refAuth.current = data;
    _setAuth(data);
  };
  let refTokenRefreshTimeout = React.useRef(null);

  const onLoginFn = async () => {
    const { state, codeVerifier, authorizationURL, queryParamsObject } = await generateAuthURL();
    window.addEventListener('message', onChildResponseFn, { once: true, capture: false });
    console.log('1- Opening modal pointing to Authorization URL...');
    console.log('1- Query params:', queryParamsObject);
    let newModal = window.open(authorizationURL, 'external_login_page', 'width=800,height=600,left=200,top=100');
    setModal(newModal);
    setAuthRequest({
      codeVerifier,
      state,
    });
  };

  const generateAuthURL = async () => {
    const { AUTH_URL, CLIENT_ID, CONFIGURED_REDIRECT_URI } = PROVIDER_CONFIGS;
    const state = generateState();
    console.log(`0- Generated state: ${state}`);
    const codeVerifier = generateCodeVerifier();
    console.log(`0- Generated Code Verifier: ${codeVerifier}`);
    const codeChallenge = await generateCodeChallenge(codeVerifier);
    console.log(`0- Generated Code Challenge from Code Verifier: ${codeChallenge}`);
    const scopesString = generateScopesString();
    const queryParamsObject = {
      client_id: CLIENT_ID,
      response_type: 'code',
      scope: scopesString,
      redirect_uri: CONFIGURED_REDIRECT_URI,
      state,
      code_challenge_method: 'S256',
      code_challenge: codeChallenge,
    };
    const params = new URLSearchParams();
    Object.entries(queryParamsObject).forEach(([key, value]) => params.append(key, value));
    const authorizationURL = `${AUTH_URL}\?${params.toString()}`;
    return { state, codeVerifier, authorizationURL, queryParamsObject };
  };

  React.useEffect(() => {
    // clean event listener on unmount
    return () => {
      window.removeEventListener('message', onChildResponseFn);
      if (refTokenRefreshTimeout.current) clearTimeout(refTokenRefreshTimeout.current);
    };
  }, []);

  const generateScopesString = () => {
    return Object.entries(refScopes.current)
      .filter(([scope, enabled]) => enabled)
      .map(([scope]) => scope)
      .concat(PROVIDER_CONFIGS.DEFAULT_SCOPES)
      .join(' ');
  };

  const onChildResponseFn = (e) => {
    const receivedValues = { state: e.data.state, code: e.data.authCode };
    console.log('2- Received state and Authorization Code from the modal');
    console.log('2- Values:', receivedValues);
    if (receivedValues.state !== refAuthRequest.current.state) {
      window.alert('Retrieved state [' + receivedValues.state + "] didn't match stored one! Try again");
      return;
    }
    const { CLIENT_ID, CONFIGURED_REDIRECT_URI } = PROVIDER_CONFIGS;
    const tokenRequestBody = {
      grant_type: 'authorization_code',
      redirect_uri: CONFIGURED_REDIRECT_URI,
      code: receivedValues.code,
      code_verifier: refAuthRequest.current.codeVerifier,
      client_id: CLIENT_ID,
    };

    requestAccessToken(tokenRequestBody);
  };

  const requestAccessToken = (tokenRequestBody) => {
    let headers = {
      'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
    };
    const params = new URLSearchParams();
    Object.entries(tokenRequestBody).forEach(([key, value]) => params.append(key, value));
    console.log('3- Sending request to Token Endpoint...');
    console.log('3- Body:', tokenRequestBody);
    axios
      .post(PROVIDER_CONFIGS.TOKEN_URI, params, { headers })
      .then((response) => {
        const newAuth = response.data;
        console.log('4- Received Access Token:', newAuth);
        setTimeoutForRefreshToken(newAuth);
        fetchUserInfo(newAuth);
        setAuth(newAuth);
      })
      .catch((error) => {
        const errorMessage = `Error retrieving token: Provider probably doesn't have CORS enabled for the Token endpoint... ${error}`;
        window.alert(errorMessage);
      })
      .finally(() => {
        if (refAuthRequest.current.state) {
          setModal(null);
          setCurrentView(VIEWS.PROJECTS);
          setAuthRequest({
            codeVerifier: '',
            state: '',
          });
        }
      });
  };

  const setTimeoutForRefreshToken = (newAuth) => {
    const defaultRefreshTokenPeriod = PROVIDER_CONFIGS.REFRESH_TOKEN_PERIOD;
    const refreshTokenIn =
      newAuth.expires_in < defaultRefreshTokenPeriod ? newAuth.expires_in : defaultRefreshTokenPeriod;
    refTokenRefreshTimeout.current = setTimeout(refreshTokenFn, refreshTokenIn * 1000);
  };

  const refreshTokenFn = () => {
    const scopesString = generateScopesString();
    const tokenRequestBody = {
      grant_type: 'refresh_token',
      refresh_token: refAuth.current.refresh_token,
      scope: scopesString,
      client_id: PROVIDER_CONFIGS.CLIENT_ID,
    };

    requestAccessToken(tokenRequestBody);
  };

  const extractProfileField = (data, fieldString) => {
    if (!fieldString) return;
    let fields = fieldString.split('.');
    let dataValue = { ...data };
    for (let field of fields) {
      dataValue = dataValue[field];
    }
    return dataValue;
  };

  const fetchUserInfo = (newAuth) => {
    const { USERINFO_URI, USER_FIELDS } = PROVIDER_CONFIGS;
    const headers = newAuth
      ? {
          headers: {
            Authorization: 'Bearer ' + newAuth.access_token,
          },
        }
      : {};
    console.log('5- Fetching User Info...');
    axios
      .get(USERINFO_URI, headers)
      .then((response) => {
        const username = extractProfileField(response.data, USER_FIELDS.USERNAME);
        const lastName = extractProfileField(response.data, USER_FIELDS.LAST_NAME);
        const firstName = extractProfileField(response.data, USER_FIELDS.FIRST_NAME);
        const picture = extractProfileField(response.data, USER_FIELDS.PICTURE);
        const user = { username, firstName, lastName, picture };
        setUser(user);
      })
      .catch((error) => {
        const errorMessage = 'Error retrieving user information: ' + error;
        window.alert(errorMessage);
      });
  };

  const onLogoutFn = () => {
    clearTimeout(refTokenRefreshTimeout.current);
    window.removeEventListener('message', onChildResponseFn);
    setUser(null);
    setAuth(null);
    setAuthRequest({
      codeVerifier: '',
      state: '',
    });
    setCurrentView(VIEWS.LOGIN);
    setModal(null);
  };

  const changeView = (view) => {
    setCurrentView(view);
  };

  const scopeChangeFn = (field, e) => {
    const currentScopes = { ...refScopes.current };
    currentScopes[field] = !currentScopes[field];
    setScopes(currentScopes);
  };

  let CurrentView = null;
  switch (currentView) {
    case VIEWS.PROJECTS:
      CurrentView = Projects;
      break;
    case VIEWS.PROFILE:
      CurrentView = Profile;
      break;
    case VIEWS.LOGIN:
      CurrentView = Login;
      break;
  }

  return (
    <div>
      {modal && <div className='dimmer'></div>}
      <div className='baeldung-container'>
        <Navbar user={user} auth={auth} onLoginFn={onLoginFn} onLogoutFn={onLogoutFn} changeView={changeView} />
        <div className='content-container'>
          <CurrentView user={user} auth={auth} scopes={scopes} scopeChangeFn={scopeChangeFn} />
        </div>
      </div>
    </div>
  );
};

window.App = App;
