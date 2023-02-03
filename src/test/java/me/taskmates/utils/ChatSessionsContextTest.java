package me.taskmates.utils;

// public class ChatSessionsContextTest extends BasePlatformTestCase {
//
//     private ChatSessionsContext chatSessionsContext;
//     private ChatActions chatDirsActions;
//
//     @Override
//     protected void setUp() throws Exception {
//         super.setUp();
//         chatSessionsContext = new ChatSessionsContext(getProject());
//         chatDirsActions = new ChatActions(getProject());
//     }
//
//     public void testCreateChatFile() {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile = chatFileFuture.get(); // Wait for the future to complete
//                 assertNotNull("Chat file should not be null", chatFile);
//                 assertTrue("Chat file should exist", chatFile.exists());
//                 assertEquals("Chat file should have the correct extension", "md", chatFile.getExtension());
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("Exception should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetChatDirWithEditorNull() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 // Ensure the base directory exists
//                 VirtualFile baseDir = myFixture.getProject().getBaseDir();
//                 assertNotNull("Base directory should not be null", baseDir);
//
//                 // Create a mock .taskmates directory to simulate the environment
//                 VirtualFile mockTaskmatesDir = baseDir.createChildDirectory(this, ".taskmates");
//                 assertNotNull("Mock .taskmates directory should not be null", mockTaskmatesDir);
//
//                 // Add a chat file to the mock .taskmates directory
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile1 = chatFileFuture.get();
//
//                 // Now call getChatDir with a null editor, which should return the .taskmates directory
//                 VirtualFile chatDir = chatSessionsContext.getChatDir(null);
//                 assertNotNull("Chat directory should not be null", chatDir);
//                 assertTrue("Chat directory should exist", chatDir.exists());
//                 assertTrue("Chat directory should be a directory", chatDir.isDirectory());
//                 assertEquals("Chat directory should be last chat in .taskmates", chatFile1.getParent(), chatDir);
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testInitializeChatDir() throws IOException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 String dirName = "testInitializeChatDir";
//                 VirtualFile chatDir = chatDirsActions.initializeChatDir(dirName);
//                 assertNotNull(chatDir);
//                 assertTrue(chatDir.exists());
//                 assertTrue(chatDir.isDirectory());
//                 assertEquals(dirName, chatDir.getName());
//             } catch (IOException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetChatDirWithEditor() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 // Create a chat file and open it in an editor
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 Editor editor = chatDirsActions.openChatFileInEditor(chatFile);
//                 assertNotNull("Editor should not be null", editor);
//
//                 // Call getChatDir with the editor
//                 VirtualFile chatDir = chatSessionsContext.getChatDir(editor);
//                 assertNotNull("Chat directory should not be null", chatDir);
//                 assertTrue("Chat directory should exist", chatDir.exists());
//                 assertTrue("Chat directory should be a directory", chatDir.isDirectory());
//                 assertEquals("Chat directory should be the parent of the chat file", chatFile.getParent(), chatDir);
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testOpenChatFile() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 Editor editor = chatDirsActions.openChatFileInEditor(chatFile);
//                 assertNotNull(editor);
//                 assertEquals(chatFile, FileDocumentManager.getInstance().getFile(editor.getDocument()));
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetChatFileWithEditor() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 Editor editor = chatDirsActions.openChatFileInEditor(chatFile);
//                 VirtualFile foundChatFile = chatSessionsContext.getChatFile(editor);
//                 assertNotNull(foundChatFile);
//                 assertEquals(chatFile, foundChatFile);
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetChatFileWithoutEditor() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 VirtualFile foundChatFile = chatSessionsContext.getChatFile(null);
//                 assertNotNull(foundChatFile);
//                 assertEquals(chatFile, foundChatFile);
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetOrCreateChatFileExisting() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> existingChatFileFuture = chatDirsActions.createChatFile(this);
//                 VirtualFile existingChatFile = existingChatFileFuture.get();
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.getOrCreateChatFile(this, null);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 assertNotNull(chatFile);
//                 assertEquals(existingChatFile, chatFile);
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetOrCreateChatFileNonExisting() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 Future<VirtualFile> chatFileFuture = chatDirsActions.getOrCreateChatFile(this, null);
//                 VirtualFile chatFile = chatFileFuture.get();
//                 assertNotNull(chatFile);
//                 assertTrue(chatFile.exists());
//                 assertEquals("md", chatFile.getExtension());
//             } catch (IOException | InterruptedException | ExecutionException e) {
//                 fail("IOException should not have been thrown");
//             }
//         });
//     }
//
//     public void testGetChatsBaseDir() throws IOException, InterruptedException, ExecutionException {
//         WriteCommandAction.runWriteCommandAction(getProject(), () -> {
//             try {
//                 // Ensure the .taskmates directory exists
//                 VirtualFile baseDir = myFixture.getProject().getBaseDir();
//                 assertNotNull("Base directory should not be null", baseDir);
//                 VirtualFile TaskmatesDir = baseDir.createChildDirectory(this, ".taskmates");
//                 assertNotNull("Mock .taskmates directory should not be null", TaskmatesDir);
//
//                 VirtualFile chatsBaseDir = chatSessionsContext.getChatsBaseDir();
//                 assertNotNull(chatsBaseDir);
//                 assertTrue(chatsBaseDir.exists());
//                 assertTrue(chatsBaseDir.isDirectory());
//                 assertEquals(".taskmates", chatsBaseDir.getName());
//             } catch (Exception e) {
//                 fail("Exception should not have been thrown");
//             }
//         });
//     }
//
//     @NotNull
//     @Override
//     protected String getTestDataPath() {
//         // Specify the path to your test data directory
//         return "$PROJECT_ROOT/src/test/testData";
//     }
// }
