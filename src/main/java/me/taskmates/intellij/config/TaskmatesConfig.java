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
    public String serverHost = "127.0.0.1:55000";
    public boolean useSSL = false;
    public String lastSelectedModel = null;

    public static TaskmatesConfig getInstance() {
        return ServiceManager.getService(TaskmatesConfig.class);
    }

    public String getHttpUrl() {
        return (useSSL ? "https://" : "http://") + serverHost;
    }

    public String getWebSocketUrl() {
        return (useSSL ? "wss://" : "ws://") + serverHost;
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
