package me.taskmates.runners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class CodeExecutionOutputAppender {
    public static Path appendImageToDisk(String base64Image, String extension, String codeCellId, String chatFilePath) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        Path chatFileDir = Paths.get(chatFilePath).getParent();
        Path attachmentsDir = chatFileDir.resolve("attachments");
        try {
            Files.createDirectories(attachmentsDir);
            Path imagePath = attachmentsDir.resolve(codeCellId + "." + extension);
            Files.write(imagePath, imageBytes);

            // flush the file system cache
            Runtime.getRuntime().exec("sync");

            return imagePath;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
