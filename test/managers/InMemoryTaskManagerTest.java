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

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask_shouldSaveTask() {
        // prepare
        Task task = new Task("task_1", "description_1", TaskStatus.NEW);
        Task expectedTask = new Task(0, "task_1", "description_1", TaskStatus.NEW);

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
        Epic epic = new Epic("epic_1", "epic_description_1");
        Epic expectedEpic = new Epic(0, "epic_1", "epic_description_1");

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
    void addNewTask_shouldSaveSubtaskWithExistEpic() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask expectedSubtask = new Subtask(1 ,"subtask_1", "subtask_description_1", 0, TaskStatus.NEW);

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
        Task task = new Task("task_1", "description_1");
        Task savedTask = taskManager.addNewTask(task);
        Task updatedTask = new Task(savedTask.getId(), "task_1_updated", "description_1_updated");

        Task expectedUpdatedTask = new Task(savedTask.getId(), "task_1_updated", "description_1_updated");

        // do
        Task actualUpdatedTask = taskManager.updateTask(updatedTask);

        // check
        assertEquals(expectedUpdatedTask, actualUpdatedTask);
    }

    @Test
    void updateTask_shouldUpdateEpicWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        Epic savedEpic = taskManager.addNewTask(epic);
        Epic updatedEpic = new Epic(savedEpic.getId(), "epic_1_updated", "epic_description_1_updated");

        Epic expectedUpdatedEpic = new Epic(savedEpic.getId(), "epic_1_updated", "epic_description_1_updated");

        // do
        Epic actualUpdatedEpic = taskManager.updateTask(updatedEpic);

        // check
        assertEquals(expectedUpdatedEpic, actualUpdatedEpic);
    }

    @Test
    void updateTask_shouldUpdateSubtaskWithSpecifiedId() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask savedSubtask = taskManager.addNewTask(subtask);
        Subtask updatedSubtask = new Subtask(savedSubtask.getId() ,"subtask_1_updated", "subtask_description_1_updated", 0);

        Subtask expectedUpdatedSubtask = new Subtask(savedSubtask.getId(), "subtask_1_updated", "subtask_description_1_updated", 0);

        // do
        Subtask actualUpdatedSubtask = taskManager.updateTask(updatedSubtask);

        // check
        assertEquals(expectedUpdatedSubtask, actualUpdatedSubtask);
    }

    @Test
    void updateTask_shouldUpdateSubtaskEpicId() {
        // prepare
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Epic epic2 = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic1);
        taskManager.addNewTask(epic2);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask updateSubtask = new Subtask(2,"subtask_1", "subtask_description_1", 1);
        Subtask savedSubtask = taskManager.addNewTask(subtask);

        // do
        // check
        assertEquals(0, savedSubtask.getEpicId());
        Subtask updatedSubtask = taskManager.updateTask(updateSubtask);
        assertEquals(1, updatedSubtask.getEpicId());
        assertEquals(new ArrayList<>(), epic1.getEpicSubtasksId());
        assertEquals(2, epic2.getEpicSubtasksId().get(0));
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusToDone() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(new Subtask(1, "subtask_u_1", "subtask_description_u_1", TaskStatus.DONE));
        taskManager.updateTask(new Subtask(2, "subtask_u_2", "subtask_description_u_2", TaskStatus.DONE));
        taskManager.updateTask(new Subtask(3, "subtask_u_3", "subtask_description_u_3", TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void refreshEpicStatus_shouldUpdateEpicStatusToInProgress() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(subtask3);

        // do
        // check
        assertEquals(TaskStatus.NEW, epic.getStatus());
        taskManager.updateTask(new Subtask(1, "subtask_u_1", "subtask_description_u_1"));
        taskManager.updateTask(new Subtask(2, "subtask_u_2", "subtask_description_u_2", TaskStatus.DONE));
        taskManager.updateTask(new Subtask(3, "subtask_u_3", "subtask_description_u_3", TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void deleteTask_shouldDeleteTaskWithSpecifiedId() {
        // prepare
        Task task = new Task("task_1", "description_1");
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
        Epic epic = new Epic("epic_1", "epic_description_1");
        Epic savedEpic = taskManager.addNewTask(epic);

        // do
        // check
        assertNotNull(taskManager.getEpic(savedEpic.getId()));
        taskManager.deleteTask(savedEpic.getId());
        assertNull(taskManager.getEpic(savedEpic.getId()));
    }

    @Test
    void deleteTask_shouldDeleteEpicSubtasks() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        Epic savedEpic = taskManager.addNewTask(epic);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
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
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask savedSubtask = taskManager.addNewTask(subtask);

        // do
        // check
        assertNotNull(taskManager.getSubtask(savedSubtask.getId()));
        taskManager.deleteTask(savedSubtask.getId());
        assertNull(taskManager.getSubtask(savedSubtask.getId()));
    }

    @Test
    void deleteTask_shouldDeleteAllTasks() {
        // prepare
        Task task1 = new Task("task_1", "description_1");
        Task task2 = new Task("task_2", "description_2");
        Task task3 = new Task("task_3", "description_3");
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
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Epic epic2 = new Epic("epic_2", "epic_description_2");
        Epic epic3 = new Epic("epic_3", "epic_description_3");
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
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
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
        Task task = new Task("task_1", "description_1");
        Task expectedTask = new Task(0,"task_1", "description_1");

        // do
        Task actualTask = taskManager.addNewTask(task);

        // check
        assertNotNull(taskManager.getTask(actualTask.getId()));
        assertEquals(expectedTask, taskManager.getTask(actualTask.getId()));
    }

    @Test
    void getTask_shouldGetEpic() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        Epic expectedEpic = new Epic(0,"epic_1", "epic_1");

        // do
        Epic actualEpic = taskManager.addNewTask(epic);

        // check
        assertNotNull(taskManager.getEpic(actualEpic.getId()));
        assertEquals(expectedEpic, taskManager.getEpic(actualEpic.getId()));
    }

    @Test
    void getTask_shouldGetSubtask() {
        // prepare
        Epic epic = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic);
        Subtask subtask = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask expectedSubtask = new Subtask(1,"subtask_1", "subtask_description_1", 0);

        // do
        Subtask actualSubtask = taskManager.addNewTask(subtask);

        // check
        assertNotNull(taskManager.getSubtask(actualSubtask.getId()));
        assertEquals(expectedSubtask, taskManager.getSubtask(actualSubtask.getId()));
    }

    @Test
    void getTask_shouldGetAllTasks() {
        // prepare
        Task task1 = new Task("task_1", "description_1");
        Task task2 = new Task("task_2", "description_2");
        Task task3 = new Task("task_3", "description_3");

        Task expectedTask1 = new Task(0,"task_1", "description_1");
        Task expectedTask2 = new Task(1,"task_2", "description_2");
        Task expectedTask3 = new Task(2,"task_3", "description_3");

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
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Epic epic2 = new Epic("epic_2", "epic_description_2");
        Epic epic3 = new Epic("epic_3", "epic_description_3");
        Epic expectedEpic1 = new Epic(0,"epic_1", "epic_description_1");
        Epic expectedEpic2 = new Epic(1,"epic_2", "epic_description_2");
        Epic expectedEpic3 = new Epic(2,"epic_3", "epic_description_3");

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
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
        Subtask expectedSubtask1 = new Subtask(1,"subtask_1", "subtask_description_1", 0);
        Subtask expectedSubtask2 = new Subtask(2,"subtask_2", "subtask_description_2", 0);
        Subtask expectedSubtask3 = new Subtask(3,"subtask_3", "subtask_description_3", 0);

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
        Epic epic1 = new Epic("epic_1", "epic_description_1");
        Epic savedEpic = taskManager.addNewTask(epic1);
        Subtask subtask1 = new Subtask("subtask_1", "subtask_description_1", 0);
        Subtask subtask2 = new Subtask("subtask_2", "subtask_description_2", 0);
        Subtask subtask3 = new Subtask("subtask_3", "subtask_description_3", 0);
        Subtask expectedSubtask1 = new Subtask(1,"subtask_1", "subtask_description_1", 0);
        Subtask expectedSubtask2 = new Subtask(2,"subtask_2", "subtask_description_2", 0);
        Subtask expectedSubtask3 = new Subtask(3,"subtask_3", "subtask_description_3", 0);

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