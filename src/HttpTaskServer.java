import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandler;
import handlers.TaskHandler;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager;

    public static void main(String[] args) throws IOException {
//        File file = new File("J:\\_YandexProjects\\java-kanban\\files\\backup.csv");
//        manager = FileBackedTaskManager.loadFromFile(file);
        manager = Managers.getDefault();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager));

//        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/epics", new EpicHandler(manager));
//        httpServer.createContext("/history", new HistoryHandler());
//        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
        System.out.println("Server started");

    }

}
