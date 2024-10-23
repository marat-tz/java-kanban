package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Subtask;
import tasks.Task;
import type.adapters.SubtaskAdapter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class SubtaskHandler extends TaskHandler {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
            .create();

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();
            String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/subtasks/", requestPath) || Pattern.matches("/subtasks", requestPath)) {
                switch (requestMethod) {
                    case "GET":
                        sendText(h, subtaskListSerialize(manager.getAllSubtasks()), 200);
                        break;
                    case "POST":
                        if (body.isEmpty() || body.isBlank()) {
                            sendText(h, "request body is empty", 400);
                        } else if (!body.contains("\"id\"")) {
                            addSubtask(h, body);
                        } else {
                            updateSubtask(h, body);
                        }
                        break;
                }

            } else if (Pattern.matches("/subtasks/\\d+", requestPath)
                    || Pattern.matches("/subtasks/\\d+/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    switch (requestMethod) {
                        case "GET" -> getSubtask(h, id.get());
                        case "DELETE" -> deleteSubtask(h, id.get());
                    }
                }

            } else {
                sendText(h, "Unknown request", 404);
            }

        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle subtask request", e);
        }
    }

    private void addSubtask(HttpExchange h, String body) {
        try {
            Subtask addedSubtask = manager.addNewTask(gson.fromJson(body, Subtask.class));
            if (Objects.nonNull(addedSubtask)) {
                sendText(h, subtaskSerialize(addedSubtask), 201);
            } else {
                sendText(h, "Epic does not exist or subtask time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while add subtask", e);
        }
    }

    private void updateSubtask(HttpExchange h, String body) {
        try {
            Subtask updatedSubtask = manager.updateTask(gson.fromJson(body, Subtask.class));
            if (Objects.nonNull(updatedSubtask)) {
                sendText(h, subtaskSerialize(updatedSubtask), 201);
            } else {
                sendText(h, "Subtask id does not exist or time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while update subtask", e);
        }
    }

    private void getSubtask(HttpExchange h, Integer subtaskId) {
        try {
            Subtask subtask = manager.getSubtask(subtaskId);
            if (Objects.isNull(subtask)) {
                sendText(h, "Subtask with id " + subtaskId + " is not exist", 404);
            } else {
                sendText(h, subtaskSerialize(subtask), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get subtask", e);
        }
    }

    private void deleteSubtask(HttpExchange h, Integer subId) {
        try {
            Subtask delSub = manager.deleteSubtask(subId);
            if (Objects.nonNull(delSub)) {
                String response = "Successful remove subtask: " + "id: "
                        + delSub.getId() + ", type: " + delSub.getType();
                sendText(h, response, 200);
            } else {
                sendText(h, "Subtask with id " + subId + " does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while remove subtask", e);
        }
    }

    protected String subtaskSerialize(Subtask subtask) {
        return gson.toJson(subtask);
    }

    protected String subtaskListSerialize(List<? extends Task> subs) {
        return gson.toJson(subs);
    }
}
