package tasks;

import java.util.*;

public class Epic extends Task {
    private List<Integer> epicSubtasks = new ArrayList<>();

    protected final TaskType type = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Epic(int id) {
        super(id);
    }

    public ArrayList<Integer> getEpicSubtasksId() {
        return new ArrayList<>(epicSubtasks);
    }

    public void removeSubtask(Integer id) {
        epicSubtasks.remove(id);
    }

    public TaskType getType() {
        return type;
    }

    public void addSubtask(Subtask subtask) {
        if (Objects.nonNull(subtask)) {
            epicSubtasks.add(subtask.getId());
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
        return String.format("%s,%s,%s,%s,%s", id, type, name, status, description);
    }

}
