package me.taskmates.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "TaskmatesConfig",
    storages = {@Storage("TaskmatesConfig.xml")}
)
public class TaskmatesConfig implements PersistentStateComponent<TaskmatesConfig> {
    public String serverUrl = "ws://localhost:55000";

    public static TaskmatesConfig getInstance() {
        return ServiceManager.getService(TaskmatesConfig.class);
    }

    @Nullable
    @Override
    public TaskmatesConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TaskmatesConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
