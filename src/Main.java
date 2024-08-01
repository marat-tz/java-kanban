import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task newTask = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Task("Задача 2", "Описание задачи", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Task("Задача 3", "Описание задачи", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Task("Задача 4", "Описание задачи", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        Task newEpic1 = new Epic("Эпик 1", "Описание задачи", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newEpic1));
        System.out.println("-----------------------------------------");

        Task newEpic2 = new Epic("Эпик 2", "Описание задачи", TaskStatus.DONE);
        System.out.println(taskManager.addNewTask(newEpic2));
        System.out.println("-----------------------------------------");

        Task newEpic3 = new Epic("Эпик 3", "Описание задачи", TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.addNewTask(newEpic3));
        System.out.println("-----------------------------------------");

        Task newEpic4 = new Epic("Эпик 4", "Описание задачи");
        System.out.println(taskManager.addNewTask(newEpic4));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 1", "Описание задачи", newEpic1);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 2", "Описание задачи", newEpic1);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 3", "Описание задачи", newEpic1);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 1", "Описание задачи", newEpic2, TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 2", "Описание задачи", newEpic2, TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 3", "Описание задачи", newEpic2, TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 1", "Описание задачи", newEpic3, TaskStatus.DONE);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 2", "Описание задачи", newEpic3, TaskStatus.DONE);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask("Подзадача 3", "Описание задачи", newEpic3, TaskStatus.DONE);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask(12, "Подзадача 1 обновление", "Описание", TaskStatus.NEW);
        System.out.println(taskManager.updateTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask(13, "Подзадача 2 обновление", "Описание", TaskStatus.NEW);
        System.out.println(taskManager.updateTask(newTask));
        System.out.println("-----------------------------------------");

        newTask = new Subtask(14, "Подзадача 3 обновление", "Описание", TaskStatus.NEW);
        System.out.println(taskManager.updateTask(newTask));
        System.out.println("-----------------------------------------");

        System.out.println(taskManager.getEpicSubtasks(newEpic3));
        taskManager.deleteEpicSubtasks(newEpic3);
        System.out.println(taskManager.getEpicSubtasks(newEpic3));



    }
}
