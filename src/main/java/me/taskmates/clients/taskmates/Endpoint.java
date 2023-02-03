package me.taskmates.clients.taskmates;

public enum Endpoint {
    TASKMATES_COMPLETIONS("/v2/taskmates/completions");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
