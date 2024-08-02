package tasks;

public class Subtask extends Task {

    private Epic currentEpic;

    public Subtask(String name, String description, Epic currentEpic, TaskStatus status) {
        super(name, description, status);
        this.currentEpic = currentEpic;

    }

    public Subtask(String name, String description, Epic currentEpic) {
        super(name, description);
        this.currentEpic = currentEpic;
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
