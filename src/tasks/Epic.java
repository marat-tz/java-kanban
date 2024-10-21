package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected LocalDateTime endTime;
    private List<Integer> epicSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Epic(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public Epic(int id) {
        super(id);
    }

    public void setStartTime(LocalDateTime time) {
        this.startTime = time;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime time) {
        this.endTime = time;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getEpicSubtasksId() {
        if (Objects.nonNull(epicSubtasks)) {
            return new ArrayList<>(epicSubtasks);
        } else {
            return new ArrayList<>();
        }
    }

    public void removeSubtask(Integer id) {
        epicSubtasks.remove(id);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        if (Objects.nonNull(subtask)) {
            if (Objects.isNull(epicSubtasks)) {
                epicSubtasks = new ArrayList<>();
            }
            epicSubtasks.add(subtask.getId());
            System.out.println("Subtask added in epic " + getId() + " list");
        } else {
            System.out.println("Subtask can't be null");
        }
    }

    public void replaceSubtasks(List<Integer> listId) {
        if (Objects.nonNull(listId)) {
            epicSubtasks = listId;
        } else {
            System.out.println("Subtask ids list can't be null");
        }
    }

    public void clearSubtasks() {
        epicSubtasks.clear();
    }

    @Override
    public String toString() {
        if (Objects.isNull(duration)) {
            return String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, name, status, description, null, startTime);

        } else if (Objects.isNull(startTime)) {
            return String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, name, status, description, null, null);

        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, name, status, description,
                    duration.toMinutes(), startTime);
        }
    }

}
