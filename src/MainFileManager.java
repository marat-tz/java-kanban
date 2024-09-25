import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;

public class MainFileManager {

    public static void main(String[] args) {
        File file = new File("J:\\_YandexProjects\\java-kanban\\files\\backup.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(file);

        Task newTask;
        Epic newEpic;
        Subtask newSubtask;

        System.out.println("Создание Task-ов:");

        newTask = new Task("Задача 1", "Added Task", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));

        newTask = new Task("Задача 2", "Added Task", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));

        newTask = new Task("Задача 3", "Added Task", TaskStatus.NEW);
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("--------------------");

        System.out.println("Создание Epic-ов:");

        newEpic = new Epic("Эпик 1", "Added Epic");
        System.out.println(taskManager.addNewTask(newEpic));

        newEpic = new Epic("Эпик 2", "Added Epic");
        System.out.println(taskManager.addNewTask(newEpic));

        newEpic = new Epic("Эпик 3", "Added Epic");
        System.out.println(taskManager.addNewTask(newEpic));
        System.out.println("--------------------");

        System.out.println("Создание Subtask-ов:");

        newSubtask = new Subtask("Подзадача 1", "Added Subtask", 4);
        System.out.println(taskManager.addNewTask(newSubtask));

        newSubtask = new Subtask("Подзадача 2", "Added Subtask", 4);
        System.out.println(taskManager.addNewTask(newSubtask));

        newSubtask = new Subtask("Подзадача 3", "Added Subtask", 5);
        System.out.println(taskManager.addNewTask(newSubtask));
        System.out.println("--------------------");

        //newSubtask = new Subtask(8, "obnova", "555");
        //taskManager.updateTask(newSubtask);


    }
}
