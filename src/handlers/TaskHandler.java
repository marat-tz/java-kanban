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

        Endpoint endpoint = getEndpoint(path, inputMethod);
        Optional<Integer> taskId = getId(httpExchange);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        String response;
        switch (endpoint) {
            case POST_ADD:
                Task addedTask = manager.addNewTask(gson.fromJson(body, Task.class));
                response = taskSerialize(addedTask);
                break;

            case POST_UPDATE: // сюда надо передавать json
                response = "Update task";
                break;

            case GET_ONE: // сделать, чтобы вместо Null было сообщение
                if (taskId.isPresent()) {
                    if (Objects.isNull(manager.getTask(taskId.get()))) {
                        response = "Task with id " + taskId.get() + " is not exist";
                    } else {
                        response = taskSerialize(manager.getTask(taskId.get()));
                    }
                } else {
                    response = "Incorrect id"; // нужно вернуть другой код
                }
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

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            switch(requestMethod) {
                case "GET":
                    return Endpoint.GET_ALL;
                case "POST":
                    return Endpoint.POST_ADD;
                case "DELETE":
                    return Endpoint.DELETE;
            } // добавить Post Update
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

    private Optional<Integer> getId(HttpExchange exchange) {
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


//class SubtitleListTypeToken extends TypeToken<List<SubtitleItem>> {
//
//}

    //List<SubtitleItem> parsed = gson.fromJson(subtitlesJson, new SubtitleListTypeToken().getType());



//    private void handleGetComments(HttpExchange exchange) throws IOException {
//        Optional<Integer> postIdOpt = getPostId(exchange);
//        if(postIdOpt.isEmpty()) {
//            writeResponse(exchange, "Некорректный идентификатор поста", 400);
//            return;
//        }
//        int postId = postIdOpt.get();
//
//        for (Post post : posts) {
//            if (post.getId() == postId) {
//                String response = post.getComments().stream()
//                        .map(Comment::toString)
//                        .collect(Collectors.joining("\n"));
//                writeResponse(exchange, response, 200);
//                return;
//            }
//        }
//
//        writeResponse(exchange, "Пост с идентификатором " + postId + " не найден", 404);
//    }