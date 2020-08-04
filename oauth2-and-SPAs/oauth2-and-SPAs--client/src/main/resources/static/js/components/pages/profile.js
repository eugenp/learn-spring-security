// Profile
const Profile = ({ user }) => {
  const { username, firstName, lastName, picture } = user;
  return (
    <div className='profile'>
      <h2>Profile</h2>
      <div className='user-info'>
        <div className='name'>
          {firstName ? firstName + ' ' : ''} {lastName || ''}
        </div>
        <div className='picture'>
          <img src={picture || 'images/default-profile.png'} />
        </div>
        <div className='username'>{username || ''}</div>
      </div>
    </div>
  );
};

window.Profile = Profile;
