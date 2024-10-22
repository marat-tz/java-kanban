package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Logger logger = Logger.getAnonymousLogger();

    protected void sendText(HttpExchange h, String text, Integer code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try {
            h.sendResponseHeaders(code, resp.length);
        } catch (IOException io) {
            logger.log(Level.SEVERE, "error while try to response headers", io);
        }
        h.getResponseBody().write(resp);
        h.close();
    }
}
