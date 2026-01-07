### The Process Plugin Definition

In order for the DSF BPE server to load your plugin you need to provide it with the following information:
* A plugin [version](../../concepts/dsf/about-version-placeholders-and-urls.md#version-pattern)
* A release date
* A plugin name
* The BPMN model files
* The FHIR resources grouped by BPMN process ID. Your plugin may have any number of BPMN models. Each has their own BPMN process ID and FHIR resources specific to that BPMN process (think [Task](../../concepts/fhir/task.md) resources needed for messages specific to that BPMN model)
* The Class holding your [Spring Configuration](../../concepts/dsf/spring-integration.md)

You will provide this information by extending the `dev.dsf.bpe.v2.AbstractProcessPluginDefinition` class.
It implements the `ProcessPluginDefinition` interface by reading the required information from the `plugin.properties` file located in `src/main/resources/plugin.properties`.
The DSF BPE server then searches for classes implementing this interface using the
Java [ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html) mechanism. Therefore, you will have to register your interface implementation in the `src/main/resources/META-INF/services/dev.dsf.bpe.ProcessPluginDefinition` file.
For this tutorial, the class extending `AbstractProcessPluginDefinition`, `TutorialProcessPluginDefinition`,
has already been added to the files as well as the `plugin.properties` file. You can use it as a reference for later when you want to create your own plugin.