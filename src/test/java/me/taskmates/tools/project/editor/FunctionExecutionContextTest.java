package me.taskmates.tools.project.editor;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.runners.FunctionExecutionContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FunctionExecutionContextTest extends BasePlatformTestCase {
    private VirtualFile chatDir;
    private VirtualFile projectDir;
    private VirtualFile chatFile;
    private VirtualFile overriddenProjectDir;

    public static final LightProjectDescriptor LIGHT_PROJECT_DESCRIPTOR = new LightProjectDescriptor();
    private HashMap<String, Object> metadata;

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return LIGHT_PROJECT_DESCRIPTOR;
    }

    @Override
    protected boolean runInDispatchThread() {
        return false;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        metadata = new HashMap<>();
        projectDir = myFixture.getTempDirFixture().findOrCreateDir("my_project");
        overriddenProjectDir = myFixture.getTempDirFixture().findOrCreateDir("overridden_project_dir");

        ThreadUtils.runInWriteAction(() -> {
            try {
                chatDir = projectDir.createChildDirectory(this, ".taskmates").createChildDirectory(this, "chat");
                chatFile = chatDir.createChildData(this, "chat.md");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }).get();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testComputeContextDefaults() throws Exception {
        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals("localhost", context.get("host"));
    }

    public void testComputeContextIDE() throws Exception {
        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals(projectDir.getPath(), context.get("project_dir"));
        assertEquals(chatDir.getPath(), context.get("chat_dir"));
        assertEquals(chatFile.getPath(), context.get("chat_file"));
    }

    public void testComputeContextChatMetadata() throws Exception {
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals("value1", context.get("key1"));
        assertEquals("value2", context.get("key2"));
    }

    public void testComputeContextDerivedCwd() throws Exception {
        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals(projectDir.getPath(), context.get("cwd"));
    }


    public void testProjectDirWithRepo() throws Exception {
        // Setup a repo directory
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                VirtualFile repoDir = chatDir.createChildDirectory(this, "repo");
                assertNotNull(repoDir);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        String repoDirPath = chatDir.findChild("repo").getPath();
        assertEquals(repoDirPath, context.get("project_dir"));
    }

    public void testHostWithDockerCompose() throws Exception {
        // Setup docker-compose.yaml
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                projectDir.createChildData(this, "docker-compose.yaml");
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals("localhost", context.get("host"));
    }

    // TODO
    // public void testHostWithDockerComposeYml() throws Exception {
    //     // Setup docker-compose.yml
    //     WriteCommandAction.runWriteCommandAction(getProject(), () -> {
    //         try {
    //             projectDir.createChildData(this, "docker-compose.yml");
    //         } catch (IOException e) {
    //             fail(e.getMessage());
    //         }
    //     });
    //
    //     Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
    //     assertEquals("localhost", context.get("host"));
    //     assertEquals(chatDir.getPath(), context.get("cwd"));
    // }

    // TODO
    // public void testHostWithDockerfile() throws Exception {
    //     // Setup Dockerfile
    //     WriteCommandAction.runWriteCommandAction(getProject(), () -> {
    //         try {
    //             projectDir.createChildData(this, "Dockerfile");
    //         } catch (IOException e) {
    //             fail(e.getMessage());
    //         }
    //     });
    //
    //     Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
    //     assertEquals("localhost", context.get("host"));
    //     assertEquals(chatDir.getPath(), context.get("cwd"));
    // }

    public void testHostWithNoSpecialFiles() throws Exception {
        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals("localhost", context.get("host"));
    }


    public void testMetadataOverridesProjectDirAndChatDir() throws Exception {
        metadata.put("project_dir", overriddenProjectDir.getPath());
        metadata.put("chat_dir", overriddenProjectDir.getPath());

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals(overriddenProjectDir.getPath(), context.get("project_dir"));
        assertEquals(overriddenProjectDir.getPath(), context.get("chat_dir"));
    }

    public void testMetadataNotOverridden() throws Exception {
        String originalValue = "originalValue";
        metadata.put("container_name", originalValue);

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals(originalValue, context.get("container_name"));
    }

    public void testMetadataOverridesHostAndCwd() throws Exception {
        String overriddenHost = "overriddenHost";
        String overriddenCwd = overriddenProjectDir.getPath();
        metadata.put("host", overriddenHost);
        metadata.put("cwd", overriddenCwd);

        Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);
        assertEquals(overriddenHost, context.get("host"));
        assertEquals(overriddenCwd, context.get("cwd"));
    }
}
