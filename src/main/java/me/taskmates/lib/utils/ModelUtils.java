package me.taskmates.lib.utils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils<T> {
    public static <T> List<T> toList(DefaultListModel<T> defaultListModel) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < defaultListModel.getSize(); i++) {
            list.add(defaultListModel.get(i));
        }
        return list;
    }
}
