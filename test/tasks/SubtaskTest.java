package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {

    @Test
    void getEpicId_shouldReturnEpicId() {
        // prepare
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1", 1);

        // do
        // check
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void setEpic_shouldSetEpicId() {
        // prepare
        Epic epic = new Epic(2, "epic_1", "epic_description_1");
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1", 1);

        // do
        subtask.setEpicId(epic);
        // check
        assertEquals(2, subtask.getEpicId());
    }

    @Test
    void setEpic_shouldNotSetEpicIdIfNull() {
        // prepare
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1", 1);

        // do
        subtask.setEpicId(null);

        // check
        assertNotNull(subtask.getEpicId());
    }

    @Test
    void checkEquals_subtaskShouldBeEqualsSubtask() {
        // prepare
        Subtask subtask1 = new Subtask(1, "subtask_1", "subtask_description_1");
        Subtask subtask2 = new Subtask(1, "subtask_1", "subtask_description_1");

        // do
        // check
        assertEquals(subtask1, subtask2);
    }
}