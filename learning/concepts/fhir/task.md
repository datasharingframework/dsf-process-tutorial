### Task

The [FHIR Task](https://www.hl7.org/fhir/R4/task.html) resource enables the DSF's distributed communication.
Whenever a BPMN process instance communicates with a different process instance, the DSF will create a Task resource
based on parameters you set in the BPMN model and during execution. It will then
automatically send the Task resource to the recipient to start or continue whatever process the Task resource referred to.
All Task resources used in the DSF derive from the [DSF Task profile](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-task-2.0.0.xml).
This profile includes a splicing for `Task.input` with three additional [Input Parameters](../../concepts/fhir/task.md#task-input-parameters):
- `message-name`
- `business-key`
- `correlation-key`

When creating your own plugin, you will want to create your own profiles based on the [DSF Task profile](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-task-2.0.0.xml) and put them into `tutorial-process/src/resources/fhir/StructureDefinition`.

#### Task Input Parameters

Task Input Parameters allow you to add additional information to [Task](task.md#task) resources.
For example, if your particular data exchange requires additional medical data, you would add a slice to your Task profile in the same
way the [DSF Task profile](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-task-2.0.0.xml) adds slices to the original [FHIR Task](https://www.hl7.org/fhir/R4/task.html) resource. Notice that this also requires creating a [CodeSystem](../../concepts/fhir/codesystem.md) and
including it in a [ValueSet](../../concepts/fhir/valueset.md) to be able to use it in the Task resource.

If these instructions are insufficient you can check out the guide on [how to add Task Input Parameters](../../guides/adding-task-input-parameters-to-task-profiles.md).

#### Task Output Parameters

Task Output Parameters server a similar purpose to [Task Input Parameters](task.md#task-input-parameters). They add additional information to a [Task](task.md#task) resource, but for a different context.
While [Task Input Parameters](task.md#task-input-parameters) provide additional information which is required to process the [Task](task.md#task), Task Output Parameters provide information on the [Task's](task.md#task) completion.
For example, in the context of a voting process, you might add the results of the vote as a Task Output Parameter. You would add a slice to your Task profile in the same
way the [DSF Task profile](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-task-2.0.0.xml) adds slices to the original [FHIR Task](https://www.hl7.org/fhir/R4/task.html) resource. Notice that this also requires creating a [CodeSystem](../../concepts/fhir/codesystem.md) and
including it in a [ValueSet](../../concepts/fhir/valueset.md) to be able to use it in the Task resource.

If these instructions are insufficient you can check out the guide on [how to add Task Input Parameters](../../guides/adding-task-input-parameters-to-task-profiles.md). Since Input Parameters and Output Parameters are created in the same way, you can follow the same guide
as the one for creating Input Parameters and replace 'input' with 'output' in your result.