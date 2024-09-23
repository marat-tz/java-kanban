package managers;

import exceptions.ManagerLoadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;

    private File file;

    @BeforeEach
    void init() {
        file = null;
        try {
            file = java.io.File.createTempFile("backup", "csv");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        taskManager = Managers.getFileBackedTaskManager(file);
    }

    @Test
    void save_shouldSaveEmptyFile() {
        // prepare
        Task task = null;

        // do
        taskManager.addNewTask(task);

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
        taskManager.addNewTask(task);
        FileBackedTaskManager newManager = null;
        try {
            newManager = taskManager.loadFromFile(file);
        } catch (ManagerLoadException exception) {
            exception.printStackTrace();
        }

        // check
        assertTrue(newManager.idTask.isEmpty());
        assertTrue(newManager.idEpic.isEmpty());
        assertTrue(newManager.idSubtask.isEmpty());
    }

    @Test
    void save_shouldSaveFewTasksInFile() {
        // prepare
        Task task = new Task("Task 1", "Task Description");
        Epic epic = new Epic("Epic 1", "Epic Description");

        // do
        Task actualTask = taskManager.addNewTask(task);
        Epic actualEpic = taskManager.addNewTask(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        Subtask actualSub = taskManager.addNewTask(subtask);

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
    void save_shouldLoadTasks() {
        // prepare
        Task task = new Task("Task 1", "Task Description");
        Epic epic = new Epic("Epic 1", "Epic Description");

        // do
        Task actualTask = taskManager.addNewTask(task);
        Epic actualEpic = taskManager.addNewTask(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        Subtask actualSub = taskManager.addNewTask(subtask);

        // check
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            manager.loadFromFile(file);
        } catch (ManagerLoadException | NullPointerException ex) {
            ex.printStackTrace();
        }

        Task loadTask = manager.getTask(0);
        Epic loadEpic = manager.getEpic(1);
        Subtask loadSub = manager.getSubtask(2);

        assertTrue(actualTask.equals(loadTask));
        assertTrue(actualEpic.equals(loadEpic));
        assertTrue(actualSub.equals(loadSub));
    }

}
