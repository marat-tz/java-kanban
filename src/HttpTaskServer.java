import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandler;
import handlers.HistoryHandler;
import handlers.PrioritizedHandler;
import handlers.SubtaskHandler;
import handlers.TaskHandler;
import managers.HistoryManager;
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
        HistoryManager historyManager = manager.getHistoryManager();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        httpServer.start();
        System.out.println("Server started");

    }

}
