package me.taskmates.lib.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ThreadUtils {
    public static <T> CompletableFuture<T> runInEdt(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                future.complete(supplier.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> runInWriteAction(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    future.complete(supplier.get());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        });
        return future;
    }

    public static <T> CompletableFuture<T> runInReadAction(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runReadAction(() -> {
                try {
                    future.complete(supplier.get());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        });
        return future;
    }

    public static <T> CompletableFuture<T> runInWriteCommand(Project project, Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();

        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    future.complete(supplier.get());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        });

        return future;
    }

    public static void dumpThreadInfo(String location) {
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();

        boolean onEdt = SwingUtilities.isEventDispatchThread();
        boolean canWrite = ApplicationManager.getApplication().isWriteAccessAllowed();
        boolean canRead = ApplicationManager.getApplication().isReadAccessAllowed();

        System.out.println(location + " threadName = " + threadName + " onEdt = " + onEdt + " canWrite = " + canWrite + " canRead = " + canRead);
    }
}
