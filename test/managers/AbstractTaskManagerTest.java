package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Duration duration;
    protected LocalDateTime time1;
    protected LocalDateTime time2;
    protected LocalDateTime time3;
    protected LocalDateTime time4;

    @BeforeEach
    void init() {
        duration = Duration.ofMinutes(10);
        time1 = LocalDateTime.of(2024, 10, 10, 10, 10);
        time2 = LocalDateTime.of(2024, 10, 10, 11, 10);
        time3 = LocalDateTime.of(2024, 10, 10, 12, 10);
        time4 = LocalDateTime.of(2024, 10, 10, 13, 10);
    }

    @Test
    void addNewTask_shouldSaveTask() {
        // prepare
        Task task = new Task("task_1", "description_1", duration, time1);
        Task expectedTask = new Task(0, "task_1", "description_1", TaskStatus.NEW, duration, time1);

        // do
        Task actualTask = taskManager.addNewTask(task);

        // check
        assertNotNull(actualTask);
        assertNotNull(actualTask.getId());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void addNewTask_shouldSaveEpic() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic expectedEpic = new Epic(0, "epic_1", "epic_description_1", TaskStatus.NEW, duration, time1);

        // do
        Epic actualEpic = taskManager.addNewTask(epic);

        // check
        assertNotNull(actualEpic);
        assertNotNull(actualEpic.getId());
        assertEquals(expectedEpic, actualEpic);
    }

    @Test
    void addNewTask_shouldNotSaveEpicAsTask() {
        // prepare
        Task epic = new Epic("epic_1", "epic_description_1");

        // do
        Task actualEpic = taskManager.addNewTask(epic);

        // check
        assertNull(actualEpic);
    }

    @Test
    void addNewTask_shouldNotSaveSubtaskAsTask() {
        // prepare
        Task subtask = new Subtask("sub_1", "sub_description_1", 0);

        // do
        Task actualSubtask = taskManager.addNewTask(subtask);

        // check
        assertNull(actualSubtask);
    }

    @Test
    void addNewTask_shouldSaveSubtaskWithExistEpic() {
        // prepare
        Epic epic = new Epic("Epic 1", "Epic Description", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask(1, "subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask expectedSubtask = new Subtask(1, "subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        // check
        assertNotNull(actualSubtask);
        assertNotNull(actualSubtask.getId());
        assertEquals(expectedSubtask, actualSubtask);
    }

    @Test
    void addNewTask_shouldNotSaveSubtaskWithoutExistEpic() {
        // prepare
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 1);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        // check
        assertNull(actualSubtask);
    }



    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedId() {
        // prepare
        Task task = new Task("task_1", "description_1", duration, time1);
        Task savedTask = taskManager.addNewTask(task);
        Task updatedTask = new Task(savedTask.getId(), "task_1_updated", "description_1_updated",
                TaskStatus.NEW, duration, time1);

        Task expectedUpdatedTask = new Task(savedTask.getId(), "task_1_updated", "description_1_updated",
                TaskStatus.NEW, duration, time1);

        // do
        Task actualUpdatedTask = taskManager.updateTask(updatedTask);

        // check
        assertEquals(expectedUpdatedTask, actualUpdatedTask);
    }

    @Test
    void updateTask_shouldUpdateEpicWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic savedEpic = taskManager.addNewTask(epic);
        Epic updatedEpic = new Epic(savedEpic.getId(), "epic_1_updated", "epic_description_1_updated",
                TaskStatus.NEW, duration, time1);

        Epic expectedUpdatedEpic = new Epic(savedEpic.getId(), "epic_1_updated",
                "epic_description_1_updated", TaskStatus.NEW, duration, time1);

        // do
        Epic actualUpdatedEpic = taskManager.updateTask(updatedEpic);

        // check
        assertEquals(expectedUpdatedEpic, actualUpdatedEpic);
    }

    @Test
    void updateTask_shouldUpdateSubtaskWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", TaskStatus.NEW, duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask savedSubtask = taskManager.addNewTask(subtask);
        Subtask updatedSubtask = new Subtask(savedSubtask.getId() ,"subtask_1_updated",
                "subtask_description_1_updated", TaskStatus.NEW, duration, time3, 0);

        Subtask expectedUpdatedSubtask = new Subtask(savedSubtask.getId(), "subtask_1_updated",
                "subtask_description_1_updated", TaskStatus.NEW, duration, time3, 0);

        // do
        Subtask actualUpdatedSubtask = taskManager.updateTask(updatedSubtask);

        // check
        assertEquals(expectedUpdatedSubtask, actualUpdatedSubtask);
    }

    @Test
    void updateTask_shouldUpdateSubtaskEpicId() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic epic2 = new Epic("epic_2", "epic_description_2", duration, time2);

        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);

        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", duration, time3, 0);
        Subtask updateSubtask = new Subtask(2,"subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time3,1);

        Subtask savedSubtask = taskManager.addNewTask(subtask);

        // do
        // check
        assertEquals(0, savedSubtask.getEpicId());
        assertEquals(2, epic1.getEpicSubtasksId().get(0));
        Subtask updatedSubtask = taskManager.updateTask(updateSubtask);
        assertEquals(1, updatedSubtask.getEpicId());
        assertEquals(2, epic2.getEpicSubtasksId().get(0));
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusToNew() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", TaskStatus.DONE, duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusToDone() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(new Subtask(1, "subtask_u_1", "subtask_description_u_1",
                TaskStatus.DONE, duration, time2));
        taskManager.updateTask(new Subtask(2, "subtask_u_2", "subtask_description_u_2",
                TaskStatus.DONE, duration, time3));
        taskManager.updateTask(new Subtask(3, "subtask_u_3", "subtask_description_u_3",
                TaskStatus.DONE, duration, time4));
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusToInProgress() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(new Subtask(1, "subtask_u_1", "subtask_description_u_1",
                duration, time2));
        taskManager.updateTask(new Subtask(2, "subtask_u_2", "subtask_description_u_2",
                TaskStatus.DONE, duration, time3));
        taskManager.updateTask(new Subtask(3, "subtask_u_3", "subtask_description_u_3",
                TaskStatus.DONE, duration, time4));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusSubtasksInProgress() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(new Subtask(1, "subtask_u_1", "subtask_description_u_1",
                TaskStatus.IN_PROGRESS, duration, time2));
        taskManager.updateTask(new Subtask(2, "subtask_u_2", "subtask_description_u_2",
                TaskStatus.IN_PROGRESS, duration, time3));
        taskManager.updateTask(new Subtask(3, "subtask_u_3", "subtask_description_u_3",
                TaskStatus.IN_PROGRESS, duration, time4));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void deleteTask_shouldDeleteTaskWithSpecifiedId() {
        // prepare
        Task task = new Task("task_1", "description_1", duration, time1);
        Task savedTask = taskManager.addNewTask(task);

        // do
        // check
        assertNotNull(taskManager.getTask(savedTask.getId()));
        taskManager.deleteTask(savedTask.getId());
        assertNull(taskManager.getTask(savedTask.getId()));
    }

    @Test
    void deleteTask_shouldDeleteEpicWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic savedEpic = taskManager.addNewTask(epic);

        // do
        // check
        assertNotNull(taskManager.getEpic(savedEpic.getId()));
        taskManager.deleteEpic(savedEpic.getId());
        assertNull(taskManager.getEpic(savedEpic.getId()));
    }

    @Test
    void deleteTask_shouldDeleteEpicSubtasks() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic savedEpic = taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertNotNull(taskManager.getEpicSubtasks(savedEpic.getId()));
        taskManager.deleteEpicSubtasks(savedEpic.getId());
        assertTrue(taskManager.getEpicSubtasks(savedEpic.getId()).isEmpty());
    }

    @Test
    void deleteTask_shouldDeleteSubtaskWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask savedSubtask = taskManager.addNewTask(subtask);

        // do
        // check
        assertNotNull(taskManager.getSubtask(savedSubtask.getId()));
        taskManager.deleteSubtask(savedSubtask.getId());
        assertNull(taskManager.getSubtask(savedSubtask.getId()));
    }

    @Test
    void deleteTask_shouldDeleteAllTasks() {
        // prepare
        Task task1 = new Task("task_1", "description_1", duration, time1);
        Task task2 = new Task("task_2", "description_2", duration, time2);
        Task task3 = new Task("task_3", "description_3", duration, time3);
        Task savedTask1 = taskManager.addNewTask(task1);
        Task savedTask2 = taskManager.addNewTask(task2);
        Task savedTask3 = taskManager.addNewTask(task3);

        // do
        // check
        assertNotNull(taskManager.getTask(savedTask1.getId()));
        assertNotNull(taskManager.getTask(savedTask2.getId()));
        assertNotNull(taskManager.getTask(savedTask3.getId()));
        taskManager.deleteAllTasks();
        assertNull(taskManager.getTask(savedTask1.getId()));
        assertNull(taskManager.getTask(savedTask2.getId()));
        assertNull(taskManager.getTask(savedTask3.getId()));
    }

    @Test
    void deleteTask_shouldDeleteAllEpics() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic epic2 = new Epic("epic_2", "epic_description_2", duration, time2);
        Epic epic3 = new Epic("epic_3", "epic_description_3", duration, time3);
        Epic savedEpic1 = taskManager.addNewTask(epic1);
        Epic savedEpic2 = taskManager.addNewTask(epic2);
        Epic savedEpic3 = taskManager.addNewTask(epic3);

        // do
        // check
        assertNotNull(taskManager.getEpic(savedEpic1.getId()));
        assertNotNull(taskManager.getEpic(savedEpic2.getId()));
        assertNotNull(taskManager.getEpic(savedEpic3.getId()));
        taskManager.deleteAllEpic();
        assertNull(taskManager.getEpic(savedEpic1.getId()));
        assertNull(taskManager.getEpic(savedEpic2.getId()));
        assertNull(taskManager.getEpic(savedEpic3.getId()));
    }

    @Test
    void deleteTask_shouldDeleteAllSubtasks() {
        // prepare
        Epic epic1 = new Epic(0, "epic_1", "Epic epic_description_1",
                TaskStatus.NEW, duration, time1);
        taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", TaskStatus.NEW,
                duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", TaskStatus.NEW,
                duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", TaskStatus.NEW,
                duration, time4, 0);
        Subtask savedSubtask1 = taskManager.addNewTask(subtask1);
        Subtask savedSubtask2 = taskManager.addNewTask(subtask2);
        Subtask savedSubtask3 = taskManager.addNewTask(subtask3);

        // do
        // check
        assertNotNull(taskManager.getSubtask(savedSubtask1.getId()));
        assertNotNull(taskManager.getSubtask(savedSubtask2.getId()));
        assertNotNull(taskManager.getSubtask(savedSubtask3.getId()));
        taskManager.deleteAllSubtasks();
        assertNull(taskManager.getSubtask(savedSubtask1.getId()));
        assertNull(taskManager.getSubtask(savedSubtask2.getId()));
        assertNull(taskManager.getSubtask(savedSubtask3.getId()));
    }

    @Test
    void getTask_shouldGetTask() {
        // prepare
        Task task = new Task("task_1", "description_1", TaskStatus.NEW, duration, time1);
        Task expectedTask = new Task(0,"task_1", "description_1", TaskStatus.NEW, duration, time1);

        // do
        Task actualTask = taskManager.addNewTask(task);

        // check
        assertNotNull(taskManager.getTask(actualTask.getId()));
        assertEquals(expectedTask, taskManager.getTask(actualTask.getId()));
    }

    @Test
    void getTask_shouldGetEpic() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic expectedEpic = new Epic(0,"epic_1", "epic_description_1",
                TaskStatus.NEW, duration, time1);

        // do
        Epic actualEpic = taskManager.addNewTask(epic);

        // check
        assertNotNull(taskManager.getEpic(actualEpic.getId()));
        assertEquals(expectedEpic, taskManager.getEpic(actualEpic.getId()));
    }

    @Test
    void getTask_shouldGetSubtask() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask expectedSubtask = new Subtask(1,"subtask_1", "subtask_description_1", duration, time2);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        // check
        assertNotNull(taskManager.getSubtask(actualSubtask.getId()));
        assertEquals(expectedSubtask, taskManager.getSubtask(actualSubtask.getId()));
    }

    @Test
    void getTask_shouldGetAllTasks() {
        // prepare
        Task task1 = new Task("task_1", "description_1", duration, time1);
        Task task2 = new Task("task_2", "description_2", duration, time2);
        Task task3 = new Task("task_3", "description_3", duration, time3);

        Task expectedTask1 = new Task(0,"task_1", "description_1", duration, time1);
        Task expectedTask2 = new Task(1,"task_2", "description_2", duration, time2);
        Task expectedTask3 = new Task(2,"task_3", "description_3", duration, time3);

        // do
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        // check
        assertNotNull(taskManager.getAllTasks());
        assertEquals(expectedTask1, taskManager.getAllTasks().get(0));
        assertEquals(expectedTask2, taskManager.getAllTasks().get(1));
        assertEquals(expectedTask3, taskManager.getAllTasks().get(2));

    }

    @Test
    void getTask_shouldGetAllEpics() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic epic2 = new Epic("epic_2", "epic_description_2", duration, time2);
        Epic epic3 = new Epic("epic_3", "epic_description_3", duration, time3);
        Epic expectedEpic1 = new Epic(0,"epic_1", "epic_description_1",
                TaskStatus.NEW, duration, time1);
        Epic expectedEpic2 = new Epic(1,"epic_2", "epic_description_2",
                TaskStatus.NEW, duration, time2);
        Epic expectedEpic3 = new Epic(2,"epic_3", "epic_description_3",
                TaskStatus.NEW, duration, time3);

        // do
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);
        taskManager.addNewTask(epic3);

        // check
        assertNotNull(taskManager.getAllEpic());
        assertEquals(expectedEpic1, taskManager.getAllEpic().get(0));
        assertEquals(expectedEpic2, taskManager.getAllEpic().get(1));
        assertEquals(expectedEpic3, taskManager.getAllEpic().get(2));
    }

    @Test
    void getTask_shouldGetAllSubtasks() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        Subtask expectedSubtask1 = new Subtask(1,"subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask expectedSubtask2 = new Subtask(2,"subtask_2", "subtask_description_2",
                TaskStatus.NEW, duration, time3, 0);
        Subtask expectedSubtask3 = new Subtask(3,"subtask_3", "subtask_description_3",
                TaskStatus.NEW, duration, time4, 0);

        // do
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // check
        assertNotNull(taskManager.getAllSubtasks());
        assertEquals(expectedSubtask1, taskManager.getAllSubtasks().get(0));
        assertEquals(expectedSubtask2, taskManager.getAllSubtasks().get(1));
        assertEquals(expectedSubtask3, taskManager.getAllSubtasks().get(2));
    }

    @Test
    void getTask_shouldGetSubtasksOfEpic() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1", duration, time1);
        Epic savedEpic = taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", duration, time2, 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", duration, time3, 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", duration, time4, 0);
        Subtask expectedSubtask1 = new Subtask(1,"subtask_1", "subtask_description_1",
                TaskStatus.NEW, duration, time2, 0);
        Subtask expectedSubtask2 = new Subtask(2,"subtask_2", "subtask_description_2",
                TaskStatus.NEW, duration, time3, 0);
        Subtask expectedSubtask3 = new Subtask(3,"subtask_3", "subtask_description_3",
                TaskStatus.NEW, duration, time4, 0);

        // do
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // check
        List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(savedEpic.getId());
        assertNotNull(epicSubtasks);
        assertEquals(expectedSubtask1, epicSubtasks.get(0));
        assertEquals(expectedSubtask2, epicSubtasks.get(1));
        assertEquals(expectedSubtask3, epicSubtasks.get(2));
    }
}