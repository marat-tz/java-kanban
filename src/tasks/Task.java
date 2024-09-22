package tasks;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    protected TaskType type = TaskType.TASK;

    public Task(int id) {
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status, TaskType type) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public Task(String name, String description, TaskType type) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = type;
    }

    public Task(int id, String name, String description, TaskStatus status, TaskType type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;
    }

    public Task(int id, String name, String description, TaskType type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.type = type;
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
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return  id + '\''
                + type.toString() + '\''
                + name + '\''
                + status + '\''
                + description;
    }

}
