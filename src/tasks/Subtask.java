package tasks;

import java.util.Objects;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(int id, String name, String description, Integer epicId, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;

    }

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Integer epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description) {
        super(id, name, description);
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

    public void setEpicId(Epic epic) {
        if (Objects.nonNull(epic)) {
            this.epicId = epic.getId();
        } else {
            System.out.println("Epic can't be null");
        }
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
