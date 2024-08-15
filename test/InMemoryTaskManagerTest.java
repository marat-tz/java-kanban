import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @AfterEach
    void cleanUp() {

    }

    @Test
    void addNewTask_shouldSaveTask() {
        // prepare
        Task task = new Task("задача_1", "описание_1");
        Task expectedTask = new Task(1, "задача_1", "описание_1");

        // do
        Task actualTask = taskManager.addNewTask(task);

        // check
        Assertions.assertNotNull(actualTask);
        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedId() {
        // prepare
        Task task = new Task("задача_2_updated", "описание_2_updated");
        Task savedTask = taskManager.addNewTask(task);
        Task updatedTask = new Task(savedTask.getId(), "задача_2_updated", "описание_2_updated");

        Task expectedUpdatedTask = new Task(savedTask.getId(), "задача_2_updated", "описание_2_updated");

        // do
        Task actualUpdatedTask = taskManager.updateTask(updatedTask);

        // check
        Assertions.assertEquals(expectedUpdatedTask, actualUpdatedTask);



    }
}