package managers;

import exceptions.ManagerLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest<TaskManager> {

    private FileBackedTaskManager fileBackedTaskManager;

    private File file;

    @BeforeEach
    void initFile() {
        file = null;
        try {
            file = java.io.File.createTempFile("backup", "csv");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        fileBackedTaskManager = Managers.getFileBackedTaskManager(file);
        taskManager = fileBackedTaskManager;
    }

    @Test
    void save_shouldSaveEmptyFile() {
        // prepare
        Task task = null;

        // do
        fileBackedTaskManager.addNewTask(task);

        // check
        try {
            assertTrue(Files.readString(file.toPath()).isEmpty());

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void save_shouldLoadEmptyFile() {
        // prepare
        Task task = null;

        // do
        fileBackedTaskManager.addNewTask(task);
        FileBackedTaskManager newManager = Managers.getFileBackedTaskManager(file);
        try {
            newManager.loadFromFile(file);
        } catch (ManagerLoadException exception) {
            exception.printStackTrace();
        }

        // check
        assertTrue(newManager.idTask.isEmpty());
        assertTrue(newManager.idEpic.isEmpty());
        assertTrue(newManager.idSubtask.isEmpty());
    }

    @Test
    void save_shouldThrowsExceptionLoadWrongFile() {
        // prepare
        // do
        // check
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(new File("file")));
    }

    @Test
    void save_shouldSaveFewTasksInFile() {
        // prepare
        Task task = new Task(0, "Task 1", "Task Description", TaskStatus.NEW, duration, time1);
        Epic epic = new Epic(1, "Epic 1", "Epic Description", TaskStatus.NEW, duration, time2);

        // do
        Task actualTask = fileBackedTaskManager.addNewTask(task);
        Epic actualEpic = fileBackedTaskManager.addNewTask(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Subtask Description",
                TaskStatus.NEW, duration, time3, epic.getId());
        Subtask actualSub = fileBackedTaskManager.addNewTask(subtask);

        // check
        boolean isTaskInFile = false;
        boolean isEpicInFile = false;
        boolean isSubInFile = false;
        try {
            for (String s : Files.readAllLines(file.toPath())) {
                if (s.contains(actualTask.getDescription())) {
                    isTaskInFile = true;
                } else if (s.contains(actualEpic.getDescription())) {
                    isEpicInFile = true;
                } else if (s.contains(actualSub.getDescription())) {
                    isSubInFile = true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        assertTrue(isTaskInFile && isEpicInFile && isSubInFile);
    }

    @Test
    void save_shouldSaveFewTasksWithTime() {
        // prepare
        Task task = new Task("Task 1", "Task Description", TaskStatus.NEW, duration, time1);
        Epic epic = new Epic("Epic 1", "Epic Description");

        // do
        Task actualTask = fileBackedTaskManager.addNewTask(task);
        Epic actualEpic = fileBackedTaskManager.addNewTask(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description",
                TaskStatus.NEW, duration, time3, epic.getId());
        Subtask actualSub = fileBackedTaskManager.addNewTask(subtask);

        // check
        boolean isTaskTime = false;
        boolean isEpicTime = false;
        boolean isSubTime = false;

        try {
            for (String s : Files.readAllLines(file.toPath())) {
                if (s.contains(actualTask.getStartTime().toString())) {
                    isTaskTime = true;

                } else if (s.contains(actualEpic.getStartTime().toString()) && !isEpicTime) {
                    isEpicTime = true;

                } else if (s.contains(actualSub.getStartTime().toString())) {
                    isSubTime = true;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        assertTrue(isTaskTime);
        assertTrue(isEpicTime);
        assertTrue(isSubTime);
    }

    @Test
    void save_shouldLoadTasks() {
        // prepare
        Task task = new Task(0, "Task 1", "Task Description", TaskStatus.NEW, duration, time1);
        Epic epic = new Epic(1, "Epic 1", "Epic Description", TaskStatus.NEW, duration, time2);

        // do
        Task actualTask = fileBackedTaskManager.addNewTask(task);
        Epic actualEpic = fileBackedTaskManager.addNewTask(epic);

        Subtask subtask = new Subtask(2, "Subtask 1", "Subtask Description",
                TaskStatus.NEW, duration, time3, epic.getId());
        Subtask actualSub = fileBackedTaskManager.addNewTask(subtask);

        // check
        FileBackedTaskManager manager = null;

        try {
            manager = fileBackedTaskManager.loadFromFile(file);
        } catch (ManagerLoadException | NullPointerException ex) {
            ex.printStackTrace();
        }

        Task loadTask = manager.getTask(0);
        Epic loadEpic = manager.getEpic(1);
        Subtask loadSub = manager.getSubtask(2);

        assertEquals(actualTask, loadTask);
        assertEquals(actualEpic, loadEpic);
        assertEquals(actualSub, loadSub);
    }
}
