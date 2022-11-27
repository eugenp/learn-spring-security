// Projects Page
const Projects = () => {
  const [projects, setProjects] = React.useState(null);
  const [selectedProject, setSelectedProject] = React.useState(null);
  const [newProjectName, setNewProjectName] = React.useState("");
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    refreshProjects();
  }, []);

  const selectProject = (projectId) => {
    setSelectedProject(projects.find((p) => p.id === projectId));
  };

  const refreshProjects = () => {
    axios
      .get(RESOURCE_CONFIGS.GET_PROJECTS_URL, {
        withCredentials: true,
      })
      .then((response) => {
        setProjects(response.data);
      })
      .finally(() => {
        setLoading(false);
      });
    setLoading(true);
  };

  const onCreateFn = () => {
    axios
      .post(
        RESOURCE_CONFIGS.SAVE_PROJECT_URL,
        { name: newProjectName },
        { withCredentials: true }
      )
      .then(() => {
        refreshProjects();
      })
      .catch((error) => {
        console.error(error);
        const alertMessage = `Error creating project:
          
        ${error}
        
        ${error.response?.data}`;
        window.alert(alertMessage);
      });
  };

  return (
    <div className="projects-container">
      {projects ? (
        <div className="projects-list">
          <div className="create-project" disabled={loading}>
            <input
              type="text"
              value={newProjectName}
              placeholder="New Project name"
              onChange={(e) => setNewProjectName(e.target.value)}
            />
            <button onClick={onCreateFn}>Create!</button>
          </div>
          <div className="projects">
            {!loading ? (
              projects.map((element) => (
                <ProjectItem
                  key={element.id}
                  id={element.id}
                  name={element.name}
                  selectProject={selectProject}
                />
              ))
            ) : (
              <Spinner />
            )}
          </div>
          <div className="tasks">
            <Tasks project={selectedProject} />
          </div>
        </div>
      ) : !loading ? (
        <div className="empty-projects-message">
          <p>Couldn't find projects</p>
          <p>Log in with 'read' permissions to retrieve Projects</p>
          <button
            className="login-button"
            onClick={() => (window.location.href = LOGIN_CONFIGS.LOGIN_URL)}
          >
            Log In
          </button>
        </div>
      ) : (
        <Spinner />
      )}
    </div>
  );
};

window.Projects = Projects;
