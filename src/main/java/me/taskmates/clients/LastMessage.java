package me.taskmates.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LastMessage {
    private String recipient = null;
    private String recipientRole = null;
    private List<Map<String, Object>> codeCells = new ArrayList<>();

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public List<Map<String, Object>> getCodeCells() {
        return codeCells;
    }

    public void setCodeCells(List<Map<String, Object>> codeCells) {
        this.codeCells = codeCells;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }
}
