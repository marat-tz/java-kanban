package tasks;

public class Subtask extends Task {

    private Epic currentEpic;

    public Subtask(String name, String description, Task currentEpic, TaskStatus status) {
        super(name, description, status);
        try {
            this.currentEpic = (Epic) currentEpic;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

    }

    public Subtask(String name, String description, Task currentEpic) {
        super(name, description);
        try {
            this.currentEpic = (Epic) currentEpic;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public Subtask(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Subtask(int id) {
        super(id);
    }

    public Epic getCurrentEpic() {
        return currentEpic;
    }

    public void setCurrentEpic(Epic currentEpic) {
        this.currentEpic = currentEpic;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epic=" + currentEpic +
                '}';
    }
}
