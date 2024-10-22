package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import type.adapters.SubtaskAdapter;

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

public class SubtaskHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
            .create();

    public SubtaskHandlerTest() throws IOException {
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
    public void addSubtask_shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        String subJson = gson.toJson(sub);

        manager.addNewTask(epic);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());
        assertEquals("Sub 1", subtasksFromManager.get(0).getName());
    }

    @Test
    public void updateSubtask_shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);

        manager.addNewTask(epic);
        manager.addNewTask(sub);

        Subtask subUpd = new Subtask(1, "UPDATE", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        String subJson = gson.toJson(subUpd);

        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());
        assertEquals("UPDATE", subtasksFromManager.get(0).getName());
    }

    @Test
    public void getSubtask_shouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        manager.addNewTask(epic);
        Task actualSub = manager.addNewTask(sub);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task jsonSub = gson.fromJson(response.body(), Subtask.class);
        assertEquals(actualSub, jsonSub);
    }

    @Test
    public void deleteSubtask_shouldRemoveSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        manager.addNewTask(epic);
        manager.addNewTask(sub);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getAllSubtasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void getSubtask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteSubtask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addSubtask_shouldNotAddSubtaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub1 = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        Subtask sub2 = new Subtask("Sub 2", "Testing sub 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        manager.addNewTask(epic);
        manager.addNewTask(sub1);
        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void updateTask_shouldNotUpdateSubtaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub1 = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        Subtask sub2 = new Subtask(1, "UPDATE", "Testing sub 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30), 0);
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));

        manager.addNewTask(epic);
        manager.addNewTask(sub1);
        manager.addNewTask(task);
        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void addTask_return400WithEmptyBody() throws IOException, InterruptedException {
        String taskJson = "";

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
