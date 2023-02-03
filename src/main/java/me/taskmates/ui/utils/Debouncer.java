package me.taskmates.ui.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public abstract class Debouncer implements DocumentListener, ActionListener {

    private Timer timer;

    public Debouncer(int delay) {
        // Create a timer with the given delay and this object as the listener
        timer = new Timer(delay, this);
        // Set the timer to only fire once after the delay
        timer.setRepeats(false);
    }

    private void debounce() {
        // Call the actionPerformed method of the debouncer
        timer.restart();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        debounce();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        debounce();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        debounce();
    }

    @Override
    abstract public void actionPerformed(ActionEvent e);
}
