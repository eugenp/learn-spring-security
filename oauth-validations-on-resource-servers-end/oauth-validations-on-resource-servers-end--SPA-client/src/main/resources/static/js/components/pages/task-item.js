// Task Item
const TaskItem = ({id, name, description}) => (
    <div key={id} className='task-item'>
        <div className='task-name'>
            Task Id: {id} - Task Name: {name} <br />
            <span>Task Description: {description}</span>
        </div>
    </div>
);

window.TaskItem = TaskItem;
