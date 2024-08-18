package tasks;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1", 1);

        // do
        subtask.setEpicId(2);
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
}