package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void getEpicSubtasksId() {
    }

    @Test
    void removeSubtask() {
    }

    @Test
    void addSubtask() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1");

        // do
        epic1.addSubtask(subtask);

        // check
        Assertions.assertNotNull(epic1.getEpicSubtasksId());
    }

    @Test
    void cloneSubtask() {
    }

    @Test
    void clearSubtasks() {
    }
}