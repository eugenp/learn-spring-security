// Projects Page

const Projects = (props) => {
  const [projects, setProjects] = React.useState(null);
  const [newProjectName, setNewProjectName] = React.useState('');
  const [loading, setLoading] = React.useState(true);
  const authToken = props.auth && props.auth.access_token;

  React.useEffect(() => {
    refreshProjects();
  }, []);

  const refreshProjects = () => {
    if (authToken) {
      const headers = {
        Authorization: 'Bearer ' + authToken,
      };
      console.log('5- Fetching Projects...');
      axios
        .get(RESOURCE_CONFIGS.GET_PROJECTS_URL, { headers })
        .then((response) => {
          setProjects(response.data);
        })
        .catch((error) => {
          console.error(error);
          const alertMessage = `Error retrieving projects. Please make sure:
        • the resource server is accessible
        • you're logged in
        • you have the 'read' scope checked on the intial page

      ${error}`;
          window.alert(alertMessage);
        })
        .finally(() => {
          setLoading(false);
        });
      setLoading(true);
    }
  };

  const onCreateFn = () => {
    const headers = {
      Authorization: 'Bearer ' + props.auth.access_token,
      'Content-type': 'application/json; charset=UTF-8',
    };
    axios
      .post(RESOURCE_CONFIGS.SAVE_PROJECT_URL, { name: newProjectName }, { headers })
      .then(() => {
        refreshProjects();
      })
      .catch((error) => {
        console.error(error);
        const alertMessage = `Error creating project. Please make sure:
          • the resource server is accessible
          • you're logged in
          • you have the 'write' scope checked on the initial page
          
        ${error}`;
        window.alert(alertMessage);
      });
  };

  return (
    <div className='projects-container'>
      {authToken && projects ? (
        <div>
          <div className='create-project' disabled={loading}>
            <input
              type='text'
              value={newProjectName}
              placeholder='New Project name'
              onChange={(e) => setNewProjectName(e.target.value)}
            />
            <button onClick={onCreateFn}>Create!</button>
          </div>
          <div className='projects'>
            {!loading ? (
              projects.map((element) => <ProjectItem key={element.id} id={element.id} name={element.name} />)
            ) : (
              <Spinner />
            )}
          </div>
        </div>
      ) : !loading ? (
        <div className='empty-projects-message'>Log in with 'read' permissions to retrieve Projects</div>
      ) : (
        <Spinner />
      )}
    </div>
  );
};

window.Projects = Projects;
