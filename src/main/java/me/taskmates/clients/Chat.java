package me.taskmates.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Chat {
    private final List<ChatMessage> messages = new ArrayList<>();
    private String model;
    private Map<String, Object> context = Map.of();
    private Map<String, Object> metadata = Map.of();
    private String basePath;
    private List<String> participants = new ArrayList<>();
    private List<String> availableTools = new ArrayList<>();

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }


    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getAvailableTools() {
        return availableTools;
    }

    public void setAvailableTools(List<String> availableTools) {
        this.availableTools = availableTools;
    }

    public boolean isEchoed() {
        return "echo".equals(this.getMessages().get(this.getMessages().size() - 1).getName());
    }
}
