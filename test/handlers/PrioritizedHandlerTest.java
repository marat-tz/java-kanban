package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Task;
import tasks.TaskStatus;
import type.adapters.DurationAdapter;
import type.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrioritizedHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpic();
        taskServer.start();

        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handle_shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(tasks);
        System.out.println(tasks);
        assertEquals(3, tasks.size());
        assertEquals("Test 3", tasks.get(0).getName());
        assertEquals("Test 2", tasks.get(1).getName());
        assertEquals("Test 1", tasks.get(2).getName());
    }

    @Test
    public void handle_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritiz");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

