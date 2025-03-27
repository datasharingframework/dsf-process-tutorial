### BPMN Process Execution

The BPMN process execution is the in-memory representation of a running BPMN process. BPMN processes have their 
executions structured as a tree hierarchy. Each BPMN process 
starts with the [process instance](https://docs.camunda.org/manual/7.20/user-guide/process-engine/process-engine-concepts/#process-instances)
as its root level execution. If, for example, this root execution reaches a parallel gateway with two paths, it would spawn two child executions 
under itself for them to process all tasks along their paths on their own.
Executions can access all the BPMN elements from the BPMN model as well as the [BPMN process variables](../../concepts/dsf/bpmn-process-variables.md).
You have access to this representation in your Java code when overriding certain methods in [Service](../../concepts/dsf/service-delegates.md) / [Message](../../concepts/dsf/message-delegates.md) Delegates
like `execute` or `getAdditionalInputParameters` through the `execution` parameter.