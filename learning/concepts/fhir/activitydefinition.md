### ActivityDefinition

[ActivityDefinitions](http://hl7.org/fhir/R4/activitydefinition.html) are used by the DSF to advertise which processes are
available at any given instance and who is allowed to request and who is allowed to execute a process. The DSF defined elements
for this purpose in the [dsf-activity-definition](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-activity-definition-2.0.0.xml) profile.


The most important elements in ActivityDefinitions are:
- `message-name`
- `task-profile`
- `requester`
- `recipient`

The `message-name` element contains the name of the [BPMN message start event](../../concepts/bpmn/messaging.md#message-start-event) or
[BPMN message intermediate catching event](../../concepts/bpmn/messaging.md#message-intermediate-catching-event) which expects
a [Task](../../concepts/fhir/task.md) resource complying to the profile defined by `task-profile`.

The `requester` and `recipient` elements define the organisation(s) or person(s) who are allowed to request or receive the message
specified by `message-name`. The receiving DSF instance is the one who will execute the process connected to the message.

You will have to create your own [ActivityDefinitions](../../concepts/fhir/activitydefinition.md) when developing a process plugin.
If you are fluent in reading XML FHIR definitions and translating them into XML resources, you can take a look at the
DSF's profile for ActivityDefinitions [here](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-activity-definition-2.0.0.xml).
ActivityDefinitions also reference other resource definitions. Depending on the resource, you will find them in one of [these folders](https://github.com/datasharingframework/dsf/tree/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir).  
If you are not as comfortable with these requirements you might want to check out the guide on [creating ActivityDefinitions](../../guides/creating-an-activity-definition.md).

You can also find examples for all possible `requester` and `recipient` elements [here](../../concepts/dsf/examples-for-requester-and-recipient-elements.md).