### Conditions

[Conditions](https://docs.camunda.org/manual/7.20/user-guide/process-engine/expression-language/#conditions)
allow you to change the behaviour of BPMN processes during execution. There are two ways you
are able to add decision logic to Conditions. The [Camunda Modeler](https://camunda.com/download/modeler/) refers to them as `Type`. You can find them in the ``Condition`` tab of
certain BPMN elements. The first one is `Script`. This allows you to add arbitrary complexity
to your decisions logic and is rarely used for process plugins. The more common Type is `Expression`.
Expressions have the following syntax: `${expression}`. For this tutorial, _expression_ will
use a boolean condition like `var == true`. You can learn more advanced features of Expressions [here](https://docs.camunda.org/manual/7.20/user-guide/process-engine/expression-language/).
For this to work during BPMN process execution, the variable you want to use for the boolean
condition must be available in the BPMN process variables before [Sequence Flow](../../concepts/bpmn/sequence-flow.md)
reaches the evaluation of the expression.