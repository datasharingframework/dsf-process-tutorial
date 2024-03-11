### BPMN Process Variables

BPMN process variables hold additional information which has to be available during BPMN process execution.
Variables can be directly related to BPMN elements like the boolean value for [Conditions](../../concepts/bpmn/conditions.md), but
do not have to be. BPMN process variables are stored as key-value pairs with the key being the variable name.
They are accessible during the entirety of the execution to all [Service](../../concepts/dsf/service-delegates.md) /
[Message](../../concepts/dsf/message-delegates.md) Delegates.

You can learn how to access to the BPMN process variables [here](../../guides/accessing-bpmn-process-variables.md).