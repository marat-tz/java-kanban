import com.sun.net.httpserver.HttpServer;
import exceptions.ManagerLoadException;
import handlers.TaskHandler;
import managers.FileBackedTaskManager;
import managers.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager;

    public static void main(String[] args) throws IOException, ManagerLoadException {
        File file = new File("J:\\_YandexProjects\\java-kanban\\files\\backup.csv");
        manager = FileBackedTaskManager.loadFromFile(file);

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager));
//        httpServer.createContext("/subtasks", new SubtaskHandler());
//        httpServer.createContext("/epics", new EpicHandler());
//        httpServer.createContext("/history", new HistoryHandler());
//        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
        System.out.println("Server started");

    }

}
