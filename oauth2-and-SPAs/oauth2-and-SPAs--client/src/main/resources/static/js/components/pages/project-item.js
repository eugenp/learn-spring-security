// Project Item
const ProjectItem = ({ id, name }) => (
  <div key={id} className='project-item'>
    <div className='project-name'>
      Id: {id} - Name: {name}
    </div>
  </div>
);

window.ProjectItem = ProjectItem;
