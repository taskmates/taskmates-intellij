# Taskmates for IntelliJ

IntelliJ plugin for the [Taskmates](https://github.com/taskmates/taskmates) ecosystem.

## Taskmates

Taskmates provides a powerful ecosystem of AI Agents that can interact with your everyday CLI tools and APIs.

The use of Taskmates requires an active subscription. Sign up for the Beta program [here](https://taskmates.me).

⚠️ **Warning:** Taskmates is experimental and can potentially execute arbitrary commands on your system (e.g. via shell or python interpreter). Use at your own risk. We take no responsibility for any actions performed by Taskmates or any resulting damages.

[//]: # (## Template ToDo list)

[//]: # (- [x] Create a new [IntelliJ Platform Plugin Template][template] project.)

[//]: # (- [ ] Get familiar with the [template documentation][template].)

[//]: # (- [ ] Verify the [pluginGroup]&#40;./gradle.properties&#41;, [plugin ID]&#40;./src/main/resources/META-INF/plugin.xml&#41; and [sources package]&#40;./src/main/kotlin&#41;.)

[//]: # (- [ ] Review the [Legal Agreements]&#40;https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate&#41;.)

[//]: # (- [ ] [Publish a plugin manually]&#40;https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate&#41; for the first time.)

[//]: # (- [ ] Set the Plugin ID in the above README badges.)

[//]: # (- [ ] Set the [Plugin Signing]&#40;https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate&#41; related [secrets]&#40;https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables&#41;.)

[//]: # (- [ ] Set the [Deployment Token]&#40;https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate&#41;.)

[//]: # (- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.)

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "taskmates-intellij"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/taskmates/taskmates-intellij/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
