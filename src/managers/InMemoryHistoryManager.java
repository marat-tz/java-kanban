package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
            return new ArrayList<>(taskHistory);
    }

    @Override
    public void add(Task task) {
        if (Objects.nonNull(task)) {
            if (taskHistory.size() >= 10) {
                taskHistory.remove(0);
            }

            Task copyTask = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
            taskHistory.add(copyTask);

        } else {
            System.out.println("Task is null");
        }
    }
}
