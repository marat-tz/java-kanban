package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Logger logger = Logger.getAnonymousLogger();

    protected void sendText(HttpExchange h, String text, Integer code) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(code, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            sendText(h, "internal server error", 500);
            logger.log(Level.SEVERE, "error while send text", e);
        }
    }
}
