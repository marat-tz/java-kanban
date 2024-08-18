package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault_shouldReturnNewTaskManager() {
        // prepare
        TaskManager taskManager;

        // do
        taskManager = Managers.getDefault();

        // check
        assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistory_shouldReturnNewHistoryManager() {
        // prepare
        HistoryManager historyManager;

        // do
        historyManager = Managers.getDefaultHistory();

        // check
        assertNotNull(historyManager);
    }
}