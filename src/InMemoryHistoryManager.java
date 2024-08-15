import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistoryIds = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        if (!taskHistoryIds.isEmpty()) {
            return new ArrayList<>(taskHistoryIds);
        }
        return null;
    }

    @Override
    public void add(Task task) {
        if (taskHistoryIds.size() == 10) {
            taskHistoryIds.remove(0);
            taskHistoryIds.add(task);
        } else if (taskHistoryIds.size() < 10){
            taskHistoryIds.add(task);
        }
    }

}
