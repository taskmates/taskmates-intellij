package me.taskmates.clients.taskmates;

import com.intellij.openapi.diagnostic.Logger;
import me.taskmates.clients.Signals;
import me.taskmates.lib.utils.JsonUtils;
import me.taskmates.lib.utils.WebSocketUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.atmosphere.wasync.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TaskmatesCompletionRequest {
    protected static final Logger LOG = Logger.getInstance(TaskmatesCompletionRequest.class);
    protected final String wsUrl;
    protected final Map<String, Object> payload;
    protected final Signals signals;
    private final String requestId;
    protected Socket socket;

    public TaskmatesCompletionRequest(Map<String, Object> payload, Signals signals) {
        this.wsUrl = "ws://localhost:5000" + Endpoint.TASKMATES_COMPLETIONS.getPath();
        this.payload = payload;
        this.signals = signals;

        this.requestId = String.format("%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS-%1$tL", System.currentTimeMillis());
    }

    public CompletableFuture<Void> performRequest() {
        LOG.info("Performing request to " + wsUrl + " with payload " + payload);

        signals.send("request.start", null);

        CompletableFuture<Void> future = new CompletableFuture<>();

        Client client = WebSocketUtils.getClient();
        RequestBuilder request = WebSocketUtils.getRequestBuilder(client, wsUrl);
        final AsyncHttpClient asyncHttpClient = WebSocketUtils.getAsyncHttpClient();

        this.socket = WebSocketUtils.createSocket(client, asyncHttpClient);

        socket.on(Event.MESSAGE, new Function<String>() {
            @Override
            public void on(String rawMessage) {
                LOG.info("Received message: " + rawMessage);

                try {
                    Map<String, Object> message = JsonUtils.parseJson(rawMessage);
                    String messageType = (String) message.get("type");
                    if (messageType.equals("completion")) {
                        signals.send("completion", ((Map<String, Object>) message.get("payload")).get("markdown_chunk"));
                    }
                } catch (Exception e) {
                    LOG.error("Error occurred: " + e.getMessage(), e);
                    signals.send("error", e);
                    future.completeExceptionally(e);
                }
            }
        }).on(Event.OPEN, new Function<String>() {
            @Override
            public void on(String event) {
                LOG.info("Connection opened." + event);
                signals.send("open", null);
            }
        }).on(Event.CLOSE, new Function<String>() {
            @Override
            public void on(String event) {
                LOG.info("Connection closed: " + event);
                signals.send("close", null);
                signals.send("request.complete", null);

                try {
                    asyncHttpClient.close();
                    future.complete(null);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                    throw new RuntimeException(e);
                }
            }
        }).on(Event.ERROR, new Function<Throwable>() {
            @Override
            public void on(Throwable throwable) {
                LOG.error("Error occurred: " + throwable.getMessage(), throwable);
                signals.send("error", throwable);
                signals.send("request.complete", null);
                future.completeExceptionally(throwable);
            }
        });

        try {
            LOG.info("Opening socket");
            socket.open(request.build(), 500, TimeUnit.MILLISECONDS);
            LOG.info("Firing socket");

            ((Map<String, Object>) payload.get("context")).put("request_id", requestId);

            socket.fire(JsonUtils.dump(payload));
        } catch (IOException e) {
            LOG.error("Error occurred: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return future;
    }

    public void interrupt() {
        try {
            socket.fire(JsonUtils.dump(Map.of(
                "type", "interrupt",
                "context", Map.of(
                    "request_id", requestId
                )
            )));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void kill() {
        try {
            socket.fire(JsonUtils.dump(Map.of(
                "type", "kill",
                "context", Map.of(
                    "request_id", requestId
                )
            )));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void closeConnection() {
        if (socket != null) {
            socket.close();
        }
    }
}
