package manual.testing;

import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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

        System.out.println("Список всех добавленных задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Изменение задач:");

        newTask = new Task(1,"Задача 1 Update IN_PROGRESS", "Описание задачи", TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.updateTask(newTask));

        newTask = new Task(2,"Задача 2 Update DONE", "Описание задачи", TaskStatus.DONE);
        System.out.println(taskManager.updateTask(newTask));

        newTask = new Task(3,"Задача 3 Update NO STATUS", "Описание задачи");
        System.out.println(taskManager.updateTask(newTask));
        System.out.println("--------------------");

        newSubtask = new Subtask(7, "Подзадача 1 Update DONE", "Описание задачи", TaskStatus.DONE);
        System.out.println(taskManager.updateTask(newSubtask));

        newSubtask = new Subtask(8, "Подзадача 2 Update DONE", "Описание задачи", TaskStatus.DONE);
        System.out.println(taskManager.updateTask(newSubtask));

        newSubtask = new Subtask(9, "Подзадача 3 Update IN_PROGRESS", "Описание задачи", TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.updateTask(newSubtask));
        System.out.println("--------------------");

        newEpic = new Epic(4, "Эпик 1 Update", "Описание задачи");
        System.out.println(taskManager.updateTask(newEpic));

        System.out.println("Список всех измененных задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Метод getSubtask: " + taskManager.getSubtask(7));
        System.out.println("-----------------------------------------");

        System.out.println("Сабтаски эпика 4: " + taskManager.getEpicSubtasks(4));

        System.out.println("Удаление сабтасков эпика 4:");
        taskManager.deleteEpicSubtasks(4);
        System.out.println("-----------------------------------------");

        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Удаление всех сабтасков:");
        taskManager.deleteAllSubtasks();
        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Удаление всех эпиков:");
        taskManager.deleteAllEpic();
        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Удаление трёх задач по одной:");
        taskManager.deleteTask(1);
        taskManager.deleteTask(2);
        taskManager.deleteTask(3);
        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Сабтаски удалённого эпика 4:");
        System.out.println(taskManager.getEpicSubtasks(4));
        System.out.println("-----------------------------------------");

        System.out.println("Создание ещё трёх Task-ов:");

        newTask = new Task("Задача 10", "Added Task");
        System.out.println(taskManager.addNewTask(newTask));

        newTask = new Task("Задача 11", "Added Task");
        System.out.println(taskManager.addNewTask(newTask));

        newTask = new Task("Задача 12", "Added Task");
        System.out.println(taskManager.addNewTask(newTask));
        System.out.println("--------------------");

        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        System.out.println("Список всех задач:");
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("-----------------------------------------");

        taskManager.getTask(10);
        taskManager.getTask(11);
        taskManager.getTask(12);
        taskManager.getTask(10);
        taskManager.getTask(11);
        taskManager.getTask(12);
        taskManager.getTask(10);
        taskManager.getTask(11);
        taskManager.getTask(12);
        taskManager.getTask(10);
        taskManager.getTask(11);
        taskManager.getTask(12);

        System.out.println(taskManager.getHistoryManager().getHistory());

    }
}
