package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import type.adapters.EpicAdapter;
import type.adapters.SubtaskAdapter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();
            String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/epics/", requestPath) || Pattern.matches("/epics", requestPath)) {
                switch (requestMethod) {
                    case "GET":
                        sendText(h, epicListSerialize(manager.getAllEpic()), 200);
                        break;
                    case "POST":
                        if (body.isEmpty() || body.isBlank()) {
                            sendText(h, "request body is empty", 400);
                        } else if (!body.contains("\"id\"")) {
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
                        case "DELETE" -> deleteEpic(h, id.get());
                    }
                }

            } else if (Pattern.matches("/epics/\\d+/subtasks", requestPath)
                    || Pattern.matches("/epics/\\d+/subtasks/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    if ("GET".equals(requestMethod)) {
                        getEpicSubtasks(h, id.get());
                    }
                }

            } else {
                sendText(h, "Unknown request", 404);
            }

        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle epic request", e);
        }
    }

    private void addEpic(HttpExchange h, String body) {
        try {
            Epic addedEpic = manager.addNewTask(gson.fromJson(body, Epic.class));
            if (Objects.nonNull(addedEpic)) {
                sendText(h, epicSerialize(addedEpic), 201);
            } else {
                sendText(h, "Epic is null", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while add epic", e);
        }
    }

    private void updateEpic(HttpExchange h, String body) {
        try {
            Epic updatedEpic = manager.updateTask(gson.fromJson(body, Epic.class));
            if (Objects.nonNull(updatedEpic)) {
                sendText(h, epicSerialize(updatedEpic), 201);
            } else {
                sendText(h, "Epic id does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while update epic", e);
        }
    }

    private void getEpic(HttpExchange h, Integer epicId) {
        try {
            Epic epic = manager.getEpic(epicId);
            if (Objects.isNull(epic)) {
                sendText(h, "Epic with id " + epicId + " is not exist", 404);
            } else {
                sendText(h, epicSerialize(epic), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get epic", e);
        }
    }

    private void getEpicSubtasks(HttpExchange h, Integer epicId) {
        try {
            Epic epic = manager.getEpic(epicId);
            if (Objects.isNull(epic)) {
                sendText(h, "Epic with id " + epicId + " does not exist", 404);
            } else {
                sendText(h, subtaskListSerialize(manager.getEpicSubtasks(epicId)), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get epic subtasks", e);
        }
    }

    private void deleteEpic(HttpExchange h, Integer epicId) {
        try {
            Epic delEpic = manager.deleteEpic(epicId);
            if (Objects.nonNull(delEpic)) {
                String response = "Successful remove epic: " + "id: "
                        + delEpic.getId() + ", type: " + delEpic.getType();
                sendText(h, response, 200);
            } else {
                sendText(h, "Task with id " + epicId + " does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while delete epic", e);
        }
    }

    protected String epicSerialize(Epic epic) {
        return gson.toJson(epic);
    }

    protected String epicListSerialize(List<? extends Task> epics) {
        return gson.toJson(epics);
    }

    protected String subtaskListSerialize(List<? extends Task> subs) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
        return gson.toJson(subs);
    }
}
