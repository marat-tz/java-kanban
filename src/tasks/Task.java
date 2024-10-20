package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status = TaskStatus.NEW;
    protected Duration duration;
    protected LocalDateTime startTime;

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
        if (Objects.isNull(this.startTime) && Objects.isNull(task.startTime)) {
            return 0;
        } else if (Objects.nonNull(this.startTime) && Objects.isNull(task.startTime)) {
            return 1;
        } else if (Objects.isNull(this.startTime)) {
            return -1;
        }
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
        return TaskType.TASK;
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
        return Objects.equals(id, task.id) && name.equals(task.name)
                && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskType.TASK, name, status, description, duration.toMinutes(), startTime);
    }

}
