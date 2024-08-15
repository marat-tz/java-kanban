package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private List<Integer> epicSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(int id) {
        super(id);
    }

    public List<Integer> getEpicSubtasksId() {
        return epicSubtasks;
    }

    public void removeSubtask(Integer id) {
        epicSubtasks.remove(id);
    }

    public void addSubtask(Integer id) {
        epicSubtasks.add(id);
    }

    public void cloneSubtask(List<Integer> id) {
        epicSubtasks = id;
    }

    public void clearSubtasks() {
        epicSubtasks.clear();
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtasks=" + epicSubtasks +
                '}';
    }

}
