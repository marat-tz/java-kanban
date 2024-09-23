package tasks;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    // Отсутствует возможность попытки передать Epic в Epic в качестве Subtask.
    // На этапе компиляции возникает ClassCastException. Epic принимает только Subtask.
    @Test
    void getEpicSubtasksId_shouldReturnSubtaskIds() {
        // prepare
        Epic epic = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask1 = new Subtask(2, "subtask_1", "subtask_description_1");
        Subtask subtask2 = new Subtask(3, "subtask_2", "subtask_description_2");
        Subtask subtask3 = new Subtask(4, "subtask_3", "subtask_description_3");
        ArrayList<Integer> checkIds = new ArrayList<>();
        checkIds.add(2);
        checkIds.add(3);
        checkIds.add(4);

        // do
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);

        // check
        assertEquals(checkIds, epic.getEpicSubtasksId());
    }

    @Test
    void removeSubtask_shouldRemoveSubtask() {
        // prepare
        Epic epic = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask1 = new Subtask(2, "subtask_1", "subtask_description_1");
        Subtask subtask2 = new Subtask(3, "subtask_2", "subtask_description_2");
        Subtask subtask3 = new Subtask(4, "subtask_3", "subtask_description_3");
        ArrayList<Integer> checkIds = new ArrayList<>();
        checkIds.add(2);
        checkIds.add(3);
        checkIds.add(4);

        // do
        // check
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);
        assertEquals(checkIds, epic.getEpicSubtasksId());
        epic.removeSubtask(subtask1.getId());
        epic.removeSubtask(subtask2.getId());
        epic.removeSubtask(subtask3.getId());
        checkIds.clear();
        assertEquals(checkIds, epic.getEpicSubtasksId());
    }

    @Test
    void addSubtask_shouldAddSubtask() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1");

        // do
        epic1.addSubtask(subtask);

        // check
        assertNotNull(epic1.getEpicSubtasksId());
    }

    @Test
    void addSubtask_shouldNotAddNullSubtask() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask = null;

        // do
        // check
        epic1.addSubtask(subtask);
        assertEquals(new ArrayList<>(), epic1.getEpicSubtasksId());
    }

    @Test
    void replaceSubtasks_shouldReplaceSubtaskIdsList() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1");
        List<Integer> ids = new ArrayList<>();
        ids.add(3);

        // do
        // check
        epic1.addSubtask(subtask);
        assertEquals(2, epic1.getEpicSubtasksId().get(0));
        epic1.replaceSubtasks(ids);
        assertEquals(3, epic1.getEpicSubtasksId().get(0));
    }

    @Test
    void clearSubtasks() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Subtask subtask = new Subtask(2, "subtask_1", "subtask_description_1");

        // do
        // check
        epic1.addSubtask(subtask);
        assertNotNull(epic1.getEpicSubtasksId());
        epic1.clearSubtasks();
        assertEquals(new ArrayList<>(), epic1.getEpicSubtasksId());
    }

    @Test
    void checkEquals_epicShouldBeEqualsEpic() {
        // prepare
        Epic epic1 = new Epic(1, "epic_1", "epic_description_1");
        Epic epic2 = new Epic(1, "epic_1", "epic_description_1");

        // do
        // check
        assertEquals(epic1, epic2);
    }
}