package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.HistoryManager;
import tasks.Task;
import type.adapters.DurationAdapter;
import type.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {

    protected final HistoryManager manager;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandler(HistoryManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();

            if (Pattern.matches("/history/", requestPath) || Pattern.matches("/history", requestPath)) {
                if ("GET".equals(requestMethod)) {
                    sendText(h, taskListSerialize(manager.getHistory()), 200);

                } else {
                    sendText(h, "Unknown request", 404);
                }
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle history request", e);
        }
    }

    protected String taskListSerialize(List<? extends Task> tasks) {
        return gson.toJson(tasks);
    }
}
