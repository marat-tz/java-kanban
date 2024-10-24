package manual.testing;

import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class MainTime {

    public static void main(String[] args) {

        Duration duration = Duration.ofMinutes(10);
        LocalDateTime time1 = LocalDateTime.of(2024, 10, 11, 10, 10);
        LocalDateTime time2 = LocalDateTime.of(2024, 10, 11, 11, 10);
        LocalDateTime time3 = LocalDateTime.of(2024, 10, 11, 12, 10);

        File file = new File("J:\\_YandexProjects\\java-kanban\\files\\backup.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(file);

        Epic newEpic;
        Task task;
        Subtask newSubtask;

        System.out.println("Создание Task-ов:");

        newEpic = new Epic("Эпик 1", "Описание эпика 1");
        System.out.println(taskManager.addNewTask(newEpic));

        task = new Task("Task 1", "Description 1", TaskStatus.NEW, duration, time1);
        System.out.println(taskManager.addNewTask(task));

        task = new Task("Task 2", "Description 2", TaskStatus.NEW, duration, time2);
        System.out.println(taskManager.addNewTask(task));

        task = new Task("Task 3", "Description 3", TaskStatus.NEW, duration, time3);
        System.out.println(taskManager.addNewTask(task));

        newSubtask = new Subtask("Сабтаск 1", "Описание сабтаска 1", TaskStatus.NEW, duration, time1, 0);
        System.out.println(taskManager.addNewTask(newSubtask));

//        newSubtask = new Task("Сабтаск 2", "Описание сабтаска 2", TaskStatus.NEW);
//        System.out.println(taskManager.addNewTask(newTask));
//        System.out.println("--------------------");

    }

}
