package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest<TaskManager> {
    @BeforeEach
    void initManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkIntersection_shouldNotSaveTaskIfIntersection() {
        // prepare
        Task task1 = new Task("task_1", "task_description_1", duration, time1);
        Task task2 = new Task("task_2", "task_description_2", duration, time1);

        // do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // check
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void checkIntersection_shouldSaveTaskIfNotIntersection() {
        // prepare
        Task task1 = new Task("task_1", "task_description_1", duration, time1);
        Task task2 = new Task("task_2", "task_description_2", duration, time2);

        // do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // check
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    void addTaskInSet_shouldSaveDiffTasks() {
        // prepare
        Task task1 = new Task("task_1", "task_description_1", duration, time1);
        Task task2 = new Task("task_2", "task_description_2", duration, time2);

        // do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // check
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void addTaskInSet_shouldNotSaveSameTask() {
        // prepare
        Task task1 = new Task("task_1", "task_description_1", duration, time1);
        Task task2 = new Task("task_1", "task_description_1", duration, time1);

        // do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        // check
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void checkIntersection_shouldSaveEpicIfNotIntersection() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        Task task2 = new Task("task_2", "task_description_2", duration, time2);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(task2);

        // check
        assertEquals(2, taskManager.getAllTasks().size() + taskManager.getAllEpic().size());
    }

    @Test
    void setStartTime_shouldSetTimeDurationToEpic() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(subtask);

        // check
        assertEquals(time2, taskManager.getEpic(0).getStartTime());
        assertEquals(duration, taskManager.getEpic(0).getDuration());
    }

    @Test
    void setStartTime_shouldSetTimeWithFewSubtasks() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time4, 0);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // check
        assertEquals(time2, taskManager.getEpic(0).getStartTime());
        assertEquals(time4.plus(duration), taskManager.getEpic(0).getEndTime());
        assertEquals(Duration.ofMinutes(30), taskManager.getEpic(0).getDuration());
    }

    @Test
    void setStartTime_shouldUpdateTimeRemoveLastSubtask() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time4, 0);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // check
        assertEquals(time2, taskManager.getEpic(0).getStartTime());
        assertEquals(time4.plus(duration), taskManager.getEpic(0).getEndTime());
        assertEquals(Duration.ofMinutes(30), taskManager.getEpic(0).getDuration());

        taskManager.deleteSubtask(3);
        assertEquals(Duration.ofMinutes(20), taskManager.getEpic(0).getDuration());
        assertEquals(time3.plus(duration), taskManager.getEpic(0).getEndTime());
    }

    @Test
    void setStartTime_shouldUpdateTimeRemoveFirstSubtask() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time4, 0);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // check
        assertEquals(time2, taskManager.getEpic(0).getStartTime());
        assertEquals(Duration.ofMinutes(30), taskManager.getEpic(0).getDuration());

        taskManager.deleteSubtask(1);
        assertEquals(Duration.ofMinutes(20), taskManager.getEpic(0).getDuration());
        assertEquals(time3, taskManager.getEpic(0).getStartTime());
    }
}
