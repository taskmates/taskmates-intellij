package me.taskmates.clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Signals {

    protected Map<String, List<Consumer<?>>> listeners = new HashMap<>();

    public <T> Signals on(String name, Consumer<T> listener) {
        if (!listeners.containsKey(name)) {
            listeners.put(name, new ArrayList<>());
        }
        listeners.get(name).add(listener);

        return this;
    }

    public <T> void send(String name, T payload) {
        if (listeners.containsKey(name)) {
            for (Consumer<?> listener : listeners.get(name)) {
                ((Consumer<T>) listener).accept(payload);
            }
        }
    }
}
