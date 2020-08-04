// Login
const Login = ({ scopes, scopeChangeFn }) => {
  return (
    <div className='login'>
      <div className='scopes-container'>
        <div> Here we can indicate the scopes that the application will request in the Authorization Endpoint URL</div>
        <div className='scope-options-container'>
          <div className='scope-option'>
            <input key='read' type='checkbox' checked={scopes.read} onChange={scopeChangeFn.bind(this, 'read')} />
            'read' scope (Get Projects)
          </div>
          <div className='scope-option'>
            <input
              key='write'
              id='writeCb'
              type='checkbox'
              checked={scopes.write}
              onChange={scopeChangeFn.bind(this, 'write')}
            />
            'write' scope (Create Projects)
          </div>
        </div>
      </div>
    </div>
  );
};

window.Login = Login;
