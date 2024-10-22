package manual.testing;

import exceptions.ManagerLoadException;
import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class MainFileManager {

    public static void main(String[] args) throws ManagerLoadException {

        Duration duration = Duration.ofMinutes(40);
        LocalDateTime time1 = LocalDateTime.of(2024, 10, 10, 20, 10);

        File file = new File("J:\\_YandexProjects\\java-kanban\\files\\backup.csv");

        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtasks());

        Subtask subtask = new Subtask(1, "Сабтаск 1", "Описание сабтаска 1", TaskStatus.NEW, duration, time1, 0);
        taskManager.updateTask(subtask);

        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteTask(1);

        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtasks());

    }
}
