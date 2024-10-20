package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;
import type_adapters.DurationAdapter;
import type_adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        InputStream inputStreamBody = h.getRequestBody();
        String requestMethod = h.getRequestMethod();
        String requestPath = h.getRequestURI().getPath();
        String body = new String(inputStreamBody.readAllBytes(), StandardCharsets.UTF_8);

        if (Pattern.matches("/epics/", requestPath) || Pattern.matches("/epics", requestPath)) {
            switch (requestMethod) {
                case "GET":
                    sendText(h, taskListSerialize(manager.getAllEpic()), 200);
                    break;
                case "POST":
                    if (!body.contains("\"id\"")) {
                        addEpic(h, body);
                    } else {
                        updateEpic(h, body);
                    }
                    break;
            }

        } else if (Pattern.matches("/epics/\\d+", requestPath)
                || Pattern.matches("/epics/\\d+/", requestPath)) {
            Optional<Integer> id = getId(requestPath);
            if (id.isPresent()) {
                switch (requestMethod) {
                    case "GET" -> getEpic(h, id.get());
                    case "DELETE" -> deleteTask(h, id.get());
                }
            }

        } else {
            sendText(h, "Unknown request", 404);
        }
    }

    private void addEpic(HttpExchange h, String body) throws IOException {
        Epic addedEpic = manager.addNewTask(gson.fromJson(body, Epic.class));
        if (Objects.nonNull(addedEpic)) {
            sendText(h, taskSerialize(addedEpic), 201);
        } else {
            sendText(h, "Epic time overlaps with existing tasks", 406);
        }
    }

    private void updateEpic(HttpExchange h, String body) throws IOException {
        Epic updatedEpic = null;
        try {
            updatedEpic = manager.updateTask(gson.fromJson(body, Epic.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(updatedEpic)) {
            sendText(h, taskSerialize(updatedEpic), 201);
        } else {
            sendText(h, "Epic time overlaps with existing tasks", 406);
        }
    }

    private void getEpic(HttpExchange h, Integer epicId) throws IOException {
        Epic epic = manager.getEpic(epicId);
        if (Objects.isNull(epic)) {
            sendText(h, "Task with id " + epicId + " is not exist", 404);
        } else {
            sendText(h, taskSerialize(epic), 200);
        }
    }
}
