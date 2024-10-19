package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import type_adapters.DurationAdapter;
import type_adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        InputStream inputStreamBody = httpExchange.getRequestBody();

        String inputMethod = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String body = new String(inputStreamBody.readAllBytes(), StandardCharsets.UTF_8);

        Endpoint endpoint = getEndpoint(path, inputMethod, body);
        Optional<Integer> taskId = getId(httpExchange);

        String response;

        switch (endpoint) {
            case POST_ADD:
                response = taskSerialize(addTask(body));
                break;

            case POST_UPDATE:
                response = "Update task";
                break;

            case GET_ONE: // сделать, чтобы вместо Null было сообщение
                response = getTask(taskId);
                break;

            case GET_ALL:
                response = taskListSerialize(manager.getAllTasks());
                break;

            case DELETE:
                if (taskId.isPresent()) {
                    Task delTask = manager.deleteTask(taskId.get());
                    if (Objects.nonNull(delTask)) {
                        response = "Successful delete task: " + "id: "
                                + delTask.getId() + ", type: " + delTask.getType();
                    } else {
                        response = "Task with id " + taskId.get() + " not exist";
                    }
                } else {
                    response = "Incorrect id";
                }
                break;

            case UNKNOWN:
                response = "Unknown HTTP method was used by client";
                break;

            default:
                response = "Unknown HTTP method was used by client";
        }

        httpExchange.sendResponseHeaders(200, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private Task addTask(String body) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        Task addedTask = manager.addNewTask(gson.fromJson(body, Task.class));
        return addedTask;
    }

    private String getTask(Optional<Integer> taskId) {
        String response;

        if (taskId.isPresent()) {
            if (Objects.isNull(manager.getTask(taskId.get()))) {
                response = "Task with id " + taskId.get() + " is not exist";

            } else {
                response = taskSerialize(manager.getTask(taskId.get()));
            }

        } else {
            response = "Incorrect id"; // нужно вернуть другой код
        }

        return response;
    }

    private String taskSerialize(Task task) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(task);
    }

    private String taskListSerialize(List<Task> tasks) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(tasks);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.GET_ALL;
                case "POST":
                    if (!body.contains("\"id\"")) {
                        return Endpoint.POST_ADD;
                    } else {
                        return Endpoint.POST_UPDATE;
                    }
                case "DELETE":
                    return Endpoint.DELETE;
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                switch (requestMethod) {
                    case "GET":
                        return Endpoint.GET_ONE;
                    case "DELETE":
                        return Endpoint.DELETE;
                }

        }

        return Endpoint.UNKNOWN;
    }

        private Optional<Integer> getId (HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length > 2) {
                try {
                    return Optional.of(Integer.parseInt(pathParts[2]));
                } catch (NumberFormatException exception) {
                    return Optional.empty();
                }
            }
            return Optional.empty();
        }
    }
