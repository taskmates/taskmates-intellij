package me.taskmates.intellij.config;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;

public class ConfigurationMigrationTest extends BasePlatformTestCase {

    @Test
    public void testConfigurationStructure() {
        TaskmatesConfig config = new TaskmatesConfig();
        
        // Verify default values
        assertEquals("127.0.0.1:55000", config.serverHost);
        assertFalse(config.useSSL);
        
        // Verify HTTP URL generation
        assertEquals("http://127.0.0.1:55000", config.getHttpUrl());
        
        // Verify WebSocket URL generation
        assertEquals("ws://127.0.0.1:55000", config.getWebSocketUrl());
    }

    @Test
    public void testSSLConfiguration() {
        TaskmatesConfig config = new TaskmatesConfig();
        config.serverHost = "secure.example.com";
        config.useSSL = true;
        
        // Verify HTTPS URL generation
        assertEquals("https://secure.example.com", config.getHttpUrl());
        
        // Verify WSS URL generation
        assertEquals("wss://secure.example.com", config.getWebSocketUrl());
    }

    @Test
    public void testVariousHostFormats() {
        TaskmatesConfig config = new TaskmatesConfig();
        
        // Test with IP and port
        config.serverHost = "192.168.1.100:8080";
        config.useSSL = false;
        assertEquals("http://192.168.1.100:8080", config.getHttpUrl());
        assertEquals("ws://192.168.1.100:8080", config.getWebSocketUrl());
        
        // Test with hostname only
        config.serverHost = "localhost";
        config.useSSL = false;
        assertEquals("http://localhost", config.getHttpUrl());
        assertEquals("ws://localhost", config.getWebSocketUrl());
        
        // Test with domain and port
        config.serverHost = "api.example.com:443";
        config.useSSL = true;
        assertEquals("https://api.example.com:443", config.getHttpUrl());
        assertEquals("wss://api.example.com:443", config.getWebSocketUrl());
    }
}
