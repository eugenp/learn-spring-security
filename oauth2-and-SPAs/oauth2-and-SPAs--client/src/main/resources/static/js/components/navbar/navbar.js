// Navbar Section

const Navbar = (props) => {
  const [logoutVisible, setLogoutVisible] = React.useState(false);

  const toggleLogout = () => {
    setLogoutVisible(!logoutVisible);
  };

  const { user, auth } = props;
  const { firstName, lastName, picture } = user || {};
  return (
    <nav>
      <div className='menu'>
        <ul>
          {auth && <li onClick={props.changeView.bind(this, VIEWS.PROJECTS)}>Projects</li>}
          {user && <li onClick={props.changeView.bind(this, VIEWS.PROFILE)}>Profile</li>}
        </ul>
      </div>
      <div className='login-container'>
        {auth ? (
          <div onClick={toggleLogout}>
            <div className='user-info'>
              <div className='picture'>
                <img src={picture || 'images/default-profile.png'} />
              </div>
              <div className='greeting'>
                Welcome {firstName ? firstName + ' ' : ''} {lastName || ''}!
              </div>
            </div>
            {logoutVisible && (
              <div className='logout'>
                <button onClick={props.onLogoutFn}>Logout</button>
              </div>
            )}
          </div>
        ) : (
          <div className='login'>
            <button onClick={props.onLoginFn}>Login</button>
          </div>
        )}
      </div>
    </nav>
  );
};

window.Navbar = Navbar;
