package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import type.adapters.EpicAdapter;
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

public class EpicHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;

    Gson epicGson = new GsonBuilder()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    Gson subtaskGson = new GsonBuilder()
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
            .create();

    public EpicHandlerTest() throws IOException {

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
    public void addEpic_shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String epicJson = epicGson.toJson(epic);

        System.out.println(epicJson);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> epicsFromManager = manager.getAllEpic();

        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("Epic 1", epicsFromManager.get(0).getName());
    }

    @Test
    public void updateEpic_shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(epic);

        Epic updEpic = new Epic(0,"UPDATE", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String epicJson = epicGson.toJson(updEpic);

        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> epicsFromManager = manager.getAllEpic();

        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("UPDATE", epicsFromManager.get(0).getName());
    }

    @Test
    public void getEpic_shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Epic actualEpic = manager.addNewTask(epic);

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic jsonEpic = epicGson.fromJson(response.body(), Epic.class);
        assertEquals(actualEpic, jsonEpic);
    }

    @Test
    public void deleteEpic_shouldRemoveEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(epic);

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getAllEpic().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllEpic().size());
    }

    @Test
    public void getEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addEpic_return400WithEmptyBody() throws IOException, InterruptedException {
        String taskJson = "";

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getEpicSubtasks_shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30), 0);
        manager.addNewTask(epic);
        Subtask actualSubtask = manager.addNewTask(subtask);

        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> subs = subtaskGson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(actualSubtask, subs.get(0));
    }
}
class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

}

