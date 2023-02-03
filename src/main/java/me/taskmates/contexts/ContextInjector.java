package me.taskmates.contexts;

import com.google.inject.Module;
import com.google.inject.*;
import com.google.inject.name.Names;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ContextInjector {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static CompletableFuture<Injector> createInjector(AnActionEvent e) {
        Editor editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        Project project = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT);


        Callable<Injector> injectorCreationTask = () -> Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Editor.class).toInstance(editor);
                bind(Project.class).toInstance(project);
                // Add other editor-related bindings here
            }
        });

        Future<Injector> future = executorService.submit(injectorCreationTask);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }, executorService);
    }

    public static Injector createChildInjectorWithArguments(Injector injector, Map<String, ?> arguments) {
        Module argumentModule = new AbstractModule() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            protected void configure() {
                arguments.forEach((name, value) -> {
                    if (value instanceof Map) {
                        Key key = Key.get(Map.class, Names.named(name));
                        bind(key).toInstance(value);
                    } else if (value instanceof List) {
                        Key key = Key.get(List.class, Names.named(name));
                        bind(key).toInstance(value);
                    } else if (value instanceof VirtualFile) {
                        Key key = Key.get(VirtualFile.class, Names.named(name));
                        bind(key).toInstance(value);
                    } else {
                        Key key = Key.get(value.getClass(), Names.named(name));
                        bind(key).toInstance(value);
                    }
                });
            }
        };
        return injector.createChildInjector(argumentModule);
    }

    public static <T> Injector createChildInjectorWithClass(Injector injector, Class<T> klazz, T instance) {
        Module classModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(klazz).toInstance(instance);
            }
        };
        return injector.createChildInjector(classModule);
    }

    public static <T> Injector createChildInjectorWithName(Injector injector, Class<T> klazz, String name, T instance) {
        Module namedModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(klazz).annotatedWith(Names.named(name)).toInstance(instance);
            }
        };
        return injector.createChildInjector(namedModule);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean hasBinding(Injector injector, Key key) {
        try {
            injector.getBinding(key);
            return true;
        } catch (ConfigurationException e) {
            return false;
        }
    }
}
