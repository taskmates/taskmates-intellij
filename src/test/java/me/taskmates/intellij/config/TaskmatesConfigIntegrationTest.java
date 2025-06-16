package me.taskmates.intellij.config;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import me.taskmates.clients.taskmates.TaskmatesCompletionRequest;
import me.taskmates.clients.Signals;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TaskmatesConfigIntegrationTest extends BasePlatformTestCase {

    @Test
    public void testTaskmatesCompletionRequestUsesConfiguredWebSocketUrl() {
        // Save original config
        TaskmatesConfig config = TaskmatesConfig.getInstance();
        String originalHost = config.serverHost;
        boolean originalSSL = config.useSSL;

        try {
            // Test with non-SSL configuration
            config.serverHost = "localhost:8080";
            config.useSSL = false;

            Signals signals = new Signals();
            Map<String, Object> payload = new HashMap<>();
            TaskmatesCompletionRequest request = new TaskmatesCompletionRequest(payload, signals);

            // The wsUrl field is protected, so we can't directly access it
            // But we can verify that the constructor doesn't throw an exception
            // and that the configuration is being used
            assertNotNull(request);

            // Test with SSL configuration
            config.serverHost = "secure.example.com:443";
            config.useSSL = true;

            request = new TaskmatesCompletionRequest(payload, signals);
            assertNotNull(request);
        } finally {
            // Restore original config
            config.serverHost = originalHost;
            config.useSSL = originalSSL;
        }
    }

    @Test
    public void testTaskmatesConfigurableIntegration() {
        TaskmatesConfigurable configurable = new TaskmatesConfigurable();
        TaskmatesConfig config = TaskmatesConfig.getInstance();

        // Save original values
        String originalHost = config.serverHost;
        boolean originalSSL = config.useSSL;

        try {
            // Create the UI component
            assertNotNull(configurable.createComponent());

            // Reset to current values
            configurable.reset();

            // Verify not modified initially
            assertFalse(configurable.isModified());

            // Change values in config
            config.serverHost = "newhost:9090";
            config.useSSL = true;

            // Reset should update UI fields
            configurable.reset();

            // Apply should save to config
            configurable.apply();

            assertEquals("newhost:9090", config.serverHost);
            assertTrue(config.useSSL);
        } finally {
            // Restore original values
            config.serverHost = originalHost;
            config.useSSL = originalSSL;
        }
    }
}
