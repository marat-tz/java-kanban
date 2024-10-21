package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class SubtaskHandler extends TaskHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        InputStream inputStreamBody = h.getRequestBody();
        String requestMethod = h.getRequestMethod();
        String requestPath = h.getRequestURI().getPath();
        String body = new String(inputStreamBody.readAllBytes(), StandardCharsets.UTF_8);

        if (Pattern.matches("/subtasks/", requestPath) || Pattern.matches("/subtasks", requestPath)) {
            switch (requestMethod) {
                case "GET":
                    sendText(h, taskListSerialize(manager.getAllSubtasks()), 200);
                    break;
                case "POST":
                    if (!body.contains("\"id\"")) {
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
    }

    private void addSubtask(HttpExchange h, String body) throws IOException {
        Subtask addedSubtask = manager.addNewTask(gson.fromJson(body, Subtask.class));
        if (Objects.nonNull(addedSubtask)) {
            sendText(h, taskSerialize(addedSubtask), 201);
        } else {
            sendText(h, "Epic does not exist or subtask time overlaps with existing tasks", 406);
        }
    }

    private void updateSubtask(HttpExchange h, String body) throws IOException {
        Subtask updatedSubtask = manager.updateTask(gson.fromJson(body, Subtask.class));
        if (Objects.nonNull(updatedSubtask)) {
            sendText(h, taskSerialize(updatedSubtask), 201);
        } else {
            sendText(h, "Subtask id does not exist or time overlaps with existing tasks", 406);
        }
    }

    private void getSubtask(HttpExchange h, Integer subtaskId) throws IOException {
        Subtask subtask = manager.getSubtask(subtaskId);
        if (Objects.isNull(subtask)) {
            sendText(h, "Subtask with id " + subtaskId + " is not exist", 404);
        } else {
            sendText(h, taskSerialize(subtask), 200);
        }
    }

    private void deleteSubtask(HttpExchange h, Integer subId) throws IOException {
        Subtask delSub = manager.deleteSubtask(subId);
        if (Objects.nonNull(delSub)) {
            String response = "Successful remove subtask: " + "id: "
                    + delSub.getId() + ", type: " + delSub.getType();
            sendText(h, response, 200);
        } else {
            sendText(h, "Subtask with id " + subId + " does not exist", 404);
        }
    }
}
