### Message Delegates

Message Delegates are the Java representation of the [Message Events](../../concepts/bpmn/messaging.md) in your BPMN model.
You link a Message Delegate to a certain [Message Event](../../concepts/bpmn/messaging.md) by selecting the Message Event
in the [Camunda Modeler](https://camunda.com/download/modeler/) and adding a Java class to the `Implementation` field.
Make sure you use the fully qualified class name. Like this:
```
org.package.myClass
```

You will only need Message Delegates for [Message Send Events](../../concepts/bpmn/messaging.md). Incoming messages will
be resolved to the correct [BPMN process execution](../../concepts/dsf/bpmn-process-execution.md) automatically using [Message Correlation](../../concepts/dsf/message-correlation.md)
and the message inputs will be added to that execution's [process variables](../../concepts/dsf/bpmn-process-variables.md).

To make a Message Delegate for [Message Send Events](../../concepts/bpmn/messaging.md), your Java class needs to implement
`MessageSendTask`. Most of the time, you will not be adding any processing logic to your Message Delegates, therefore you usually won't be overwriting
the `execute` method like with [Service Delegates](../../concepts/dsf/service-delegates.md). Instead, you most likely want to
aggregate the information you processed in earlier steps and attach it to a message. For this you need to implement the
`getAdditionalInputParamters` method. The DSF translates BPMN messages
into FHIR [Task](../../concepts/fhir/task.md) resources to execute the communication modeled by your BPMN diagrams. The information
you are sending to another BPMN process is specified in the Task.input elements a.k.a. [Input Parameters](../../concepts/fhir/task.md#task-input-parameters),
hence the name of the method.
