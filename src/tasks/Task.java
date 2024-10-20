package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status = TaskStatus.NEW;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected final TaskType type = TaskType.TASK;

    public Task(int id) {
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(int id, String name, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Task task) {
        return this.startTime.compareTo(task.startTime);
    }

    public LocalDateTime getEndTime() {
        if (Objects.isNull(duration) || Objects.isNull(startTime)) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public void setId(Integer id) {
        if (Objects.nonNull(id) && id >= 0) {
            this.id = id;
        } else {
            System.out.println("id can't be null or less than 0");
        }
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (Objects.nonNull(status)) {
            this.status = status;
        } else {
            System.out.println("Status can't be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name)
                && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, type, name, status, description, duration.toMinutes(), startTime);
    }

}
