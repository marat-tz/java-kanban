package tasks;

public class Subtask extends Task {

    private Integer currentEpicId;

    public Subtask(String name, String description, Integer epicId, TaskStatus status) {
        super(name, description, status);
        this.currentEpicId = epicId;

    }

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.currentEpicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Subtask(int id) {
        super(id);
    }

    public Integer getCurrentEpicId() {
        return currentEpicId;
    }

    public void setCurrentEpic(Integer currentEpicId) {
        this.currentEpicId = currentEpicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epic=" + currentEpicId +
                '}';
    }
}
