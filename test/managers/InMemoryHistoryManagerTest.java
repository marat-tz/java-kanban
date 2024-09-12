package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Task task1 = new Task(0,"task_1", "description_1");
        Task task2 = new Task(1,"task_2", "description_2");
        Task task3 = new Task(2,"task_3", "description_3");
        Epic epic1 = new Epic(3,"epic_1", "epic_description_1");
        Epic epic2 = new Epic(4,"epic_2", "epic_description_2");
        Epic epic3 = new Epic(5,"epic_3", "epic_description_3");
        Subtask subtask1 = new Subtask(6,"subtask_1", "subtask_description_1", 3);
        Subtask subtask2 = new Subtask(7,"subtask_2", "subtask_description_2", 3);
        Subtask subtask3 = new Subtask(8,"subtask_3", "subtask_description_3", 3);

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
    void add_shouldSavePreviousVersionOfTask() {
        // prepare
        Task task1 = new Task(5,"task_1", "description_1", TaskStatus.NEW);

        // do
        historyManager.add(task1);

        // check
        assertEquals(TaskStatus.NEW, task1.getStatus());
        task1.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.NEW, historyManager.getHistory().get(0).getStatus());
    }

    @Test
    void add_shouldNotSaveNullTaskInHistory() {
        // prepare
        Task task1 = null;

        // do
        historyManager.add(task1);

        // check
        final List<Task> history = historyManager.getHistory();
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList, history);
    }

    @Test
    void add_shouldRemoveOldTaskAndAddNewToEndOfList() {
        // prepare
        Task task1 = new Task(5,"task_1", "description_1");
        Task task2 = new Task(6,"task_2", "description_2");
        Task task3 = new Task(8,"task_3", "description_3");
        Task task4 = new Task(3,"task_4", "description_4");
        Task task5 = new Task(5,"task_1_new", "description_1_new");

        // do
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        // check
        List<Task> history = historyManager.getHistory();
        assertEquals("task_1", history.get(0).getName());
        historyManager.add(task5);
        history = historyManager.getHistory();
        assertEquals(6, history.get(0).getId());
        assertEquals("task_1_new", history.get(history.size() - 1).getName());
    }

    @Test
    void add_shouldRemoveTail() {
        // prepare
        Task task1 = new Task(0,"task_1", "description_1");
        Task task2 = new Task(1,"task_2", "description_2");
        Task task3 = new Task(2,"task_3", "description_3");
        Task task4 = new Task(3,"task_4", "description_4");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        // do
        // check
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(3, history.get(3).getId());
        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(2, history.get(2).getId());
    }

    @Test
    void add_shouldRemoveNode() {
        // prepare
        Task task1 = new Task(0,"task_1", "description_1");
        Task task2 = new Task(1,"task_2", "description_2");
        Task task3 = new Task(2,"task_3", "description_3");
        Task task4 = new Task(3,"task_4", "description_4");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        // do
        // check
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(2, history.get(2).getId());
        historyManager.remove(2);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(3, history.get(2).getId());
    }
}