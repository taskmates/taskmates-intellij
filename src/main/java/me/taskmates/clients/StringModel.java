package me.taskmates.clients;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StringModel {
    private String text;
    private transient PropertyChangeSupport support;

    public StringModel() {
        this.support = new PropertyChangeSupport(this);

    }
    public StringModel(String text) {
        this();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        support.firePropertyChange("text", oldText, text);
    }

    public void addTextChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener("text", listener);
    }

    public void removeTextChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return text;
    }
}
