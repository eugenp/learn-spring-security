// Project Item
const ProjectItem = ({ id, name, selectProject }) => {
  return (
    <div key={id} className="project-item">
      <div className="project-name">
        Id: {id} - Name: {name}
        <button className="view-tasks-button" onClick={() => selectProject(id)}>
          View Tasks
        </button>
      </div>
    </div>
  );
};

window.ProjectItem = ProjectItem;
