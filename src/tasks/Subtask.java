package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public Subtask(int id, String name, String description,
                   TaskStatus status, Duration duration,
                   LocalDateTime startTime, Integer epicId) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description,
                   TaskStatus status, Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public Subtask(String name, String description,
                   TaskStatus status, Duration duration,
                   LocalDateTime startTime, Integer epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description,
                   Duration duration, LocalDateTime startTime, Integer epicId) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description,
                   Duration duration, LocalDateTime startTime) {
        super(id, name, description, duration, startTime);
    }

    public Subtask(int id) {
        super(id);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", id, TaskType.SUBTASK, name, status,
                description, duration.toMinutes(), startTime, epicId);
    }
}
