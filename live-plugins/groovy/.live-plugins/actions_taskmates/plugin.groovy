package actions_taskmates

import com.intellij.openapi.actionSystem.AnAction
import me.taskmates.intellij.actions.PerformCompletionAction
import me.taskmates.intellij.actions.assistance.FileChatAction
import me.taskmates.intellij.actions.session.NewChatAction
import me.taskmates.intellij.actions.session.PruneChatsAction

import static liveplugin.PluginUtil.*

// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/org.commonmark/commonmark/0.20.0/91778279177a8b8e6db81137f223e0ef2c49f22a/commonmark-0.20.0.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/org.atmosphere/wasync/3.0.2/7f4becbb91869b64357b375bdf8f49e13ff6ea36/wasync-3.0.2.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/org.asynchttpclient/async-http-client/2.12.3/6dfc91814cc8b3bc3327246d0e5df36911b9a623/async-http-client-2.12.3.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/org.asynchttpclient/async-http-client-netty-utils/2.12.3/ad99d8622931ed31367d0fef7fa17eb62e033fb3/async-http-client-netty-utils-2.12.3.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/com.google.inject/guice/7.0.0/ccc518677b9367d45f99dfa592c9b039f07687d3/guice-7.0.0.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/jakarta.inject/jakarta.inject-api/2.0.1/4c28afe1991a941d7702fe1362c365f0a8641d1e/jakarta.inject-api-2.0.1.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/aopalliance/aopalliance/1.0/235ba8b489512805ac13a8f9ea77a1ca5ebe3e8/aopalliance-1.0.jar
// add-to-classpath $HOME/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-exec/1.3/8dfb9facd0830a27b1b5f29f84593f0aeee7773b/commons-exec-1.3.jar
// add-to-classpath $PROJECT_PATH/build/instrumented/instrumentCode
// add-to-classpath $PROJECT_PATH/build/classes/java/main/

void unregisterAndRegisterAction(String actionId, String shortcut, AnAction action) {
  unregisterAction(actionId)
  registerAction(actionId, shortcut, "liveplugins", actionId, pluginDisposable,
    action
  )
}


unregisterAction(FileChatAction.class.getCanonicalName())

unregisterAndRegisterAction("Taskmates New Chat", "control shift Y", new NewChatAction())
unregisterAndRegisterAction("Taskmates Prune Chats", "control shift Y", new PruneChatsAction())

for (String model : ["gpt-4o",
                     "gpt-4-turbo",
                     "gpt-4",
                     "gpt-3.5-turbo-16k",
                     "gpt-3.5-turbo",
                     "llama3-70b-8192",
                     "codellama-7b-instruct.Q4_0.gguf"]) {
  unregisterAction(PerformCompletionAction.class.getCanonicalName() + ":" + model)
}


unregisterAndRegisterAction(PerformCompletionAction.class.getCanonicalName() + ":claude-3-haiku-20240307", "control shift 1", new PerformCompletionAction())
unregisterAndRegisterAction(PerformCompletionAction.class.getCanonicalName() + ":llama3-70b-8192", "control shift 2", new PerformCompletionAction())
unregisterAndRegisterAction(PerformCompletionAction.class.getCanonicalName() + ":gpt-4", "control shift 3", new PerformCompletionAction())
unregisterAndRegisterAction(PerformCompletionAction.class.getCanonicalName() + ":gpt-4-turbo", "control shift 4", new PerformCompletionAction())
unregisterAndRegisterAction(PerformCompletionAction.class.getCanonicalName() + ":gpt-4o", "control shift 5", new PerformCompletionAction())
unregisterAndRegisterAction(FileChatAction.class.getCanonicalName(), "control shift T", new FileChatAction())


if (!isIdeStartup) show("Loaded")

