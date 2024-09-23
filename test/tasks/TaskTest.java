package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void getName() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1");

        // do
        // check
        assertEquals("task_1", task.getName());
    }

    @Test
    void getId() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1");

        // do
        // check
        assertEquals("task_1", task.getName());
    }

    @Test
    void setId() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1");

        // do
        task.setId(2);
        // check
        assertEquals(2, task.getId());
    }

    @Test
    void setId_shouldNotSetIdIfIdIsNull() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1");

        // do
        task.setId(null);

        // check
        assertEquals(1, task.getId());
    }

    @Test
    void setId_shouldNotSetIdIfIdIsLessThanZero() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1");

        // do
        task.setId(-1);

        // check
        assertEquals(1, task.getId());
    }

    @Test
    void getStatus() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1", TaskStatus.NEW);

        // do
        // check
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void setStatus() {
        // prepare
        Task task = new Task(1, "task_1", "task_description_1", TaskStatus.NEW);

        // do
        task.setStatus(TaskStatus.DONE);
        // check
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void checkEquals_taskShouldBeEqualsTask() {
        // prepare
        Task task1 = new Task(1, "task_1", "description_1");
        Task task2 = new Task(1, "task_1", "description_1");

        // do
        // check
        assertEquals(task1, task2);
    }
}