package managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void add_shouldSaveTaskInHistory() {
        // prepare
        Task task1 = new Task("task_1", "description_1");
        Task task2 = new Task("task_2", "description_2");
        Task task3 = new Task("task_3", "description_3");
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Epic epic2 = new Epic("epic_2", "epic_description_2");
        Epic epic3 = new Epic("epic_3", "epic_description_3");
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 4);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 4);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 4);

        // do
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);

        // check
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(9, history.size());
    }

    @Test
    void add_shouldRemoveFirstTaskInHistoryIfListSizeIsBiggerThan10() {
        // prepare
        Task task;

        // do
        for (int i = 1; i <= 11; i++) {
            task = new Task("task_" + i, "description_" + i);
            Task savedTask = taskManager.addNewTask(task);
            historyManager.add(savedTask);
        }

        // check
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(10, history.size());
        assertEquals(2, history.get(0).getId());
        assertEquals(11, history.get(9).getId());
    }
}