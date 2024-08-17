package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        if (!taskHistory.isEmpty()) {
            return new ArrayList<>(taskHistory);
        }
        return null;
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() > 10) {
            taskHistory.removeFirst();
        }

        taskHistory.add(task);
    }

}
