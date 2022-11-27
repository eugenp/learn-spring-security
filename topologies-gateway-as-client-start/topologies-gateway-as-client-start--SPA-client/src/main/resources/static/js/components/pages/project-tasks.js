// Project's Tasks Page
const Tasks = ({ project }) => {
  const [tasks, setTasks] = React.useState(null);
  const [loading, setLoading] = React.useState(false);

  React.useEffect(() => {
    project && refreshTasks();
  }, [project]);

  const refreshTasks = () => {
    axios
      .get(`${RESOURCE_CONFIGS.GET_TASKS_URL}?projectId=${project.id}`)
      .then((response) => {
        setTasks(response.data);
      })
      .finally(() => {
        setLoading(false);
      });
    setLoading(true);
  };

  return (
    <div className="tasks-container">
      {project && (
        <div className="project-info">
          <p>Project: {project.name}</p>
        </div>
      )}
      {tasks?.length ? (
        <div>
          <div className="task-items">
            {!loading ? (
              tasks.map((element) => (
                <TaskItem
                  key={element.id}
                  id={element.id}
                  name={element.name}
                  description={element.description}
                />
              ))
            ) : (
              <Spinner />
            )}
          </div>
        </div>
      ) : !loading ? (
        <div className="empty-tasks-message">
          <p>{project ? "Project doesn't contain any Task" : ""}</p>
        </div>
      ) : (
        <Spinner />
      )}
    </div>
  );
};

window.Tasks = Tasks;
