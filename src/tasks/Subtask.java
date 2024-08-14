package tasks;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Integer epicId, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;

    }

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Subtask(int id) {
        super(id);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpic(Integer currentEpicId) {
        this.epicId = currentEpicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epic=" + epicId +
                '}';
    }
}
