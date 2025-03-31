### Messaging

In order to enable communication with other lanes, pools or even entirely separate processes you need to be able
to exchange information. In BPMN, you can use [Message Events](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/)
to model this information exchange. Modeling communication with [Message Events](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/) in the same diagram
uses Message Flow. Message Flow is typically represented by a dashed line arrow between BPMN elements with a black (send) or white (receive) envelope icon.
The following BPMN collaboration diagram shows message exchange between two processes.

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="../../../exercises/figures/exercise3_message_flow_inverted.svg">
  <source media="(prefers-color-scheme: light)" srcset="../../../exercises/figures/exercise3_message_flow.svg">
  <img alt="BPMN collaboration diagram with two processes using message flow to exchange information between two organizations" src="../../../exercises/figures/exercise3_message_flow.svg">
</picture>

#### Message Start Event

[Message Start Events](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/#message-start-event)
allow a BPMN process to be started by an incoming message. In the DSF, all BPMN processes are started via messages. Therefore,
you will have to include a [Message Start Event](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/#message-start-event) at
the beginning of all of your BPMN models.

#### Message Intermediate Throwing Event
[Message Intermediate Throwing Events](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/#message-intermediate-throwing-event)
are used to send messages during process execution.

#### Message Intermediate Catching Event
[Message Intermediate Catching Events](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/#message-intermediate-catching-event) serve as
the counterpart to [Message Intermediate Throwing Events](messaging.md#message-intermediate-throwing-event).
Use them whenever you expect to receive a message from another process or organization during execution.

#### Message End Event
The [Message End Event](https://docs.camunda.org/manual/7.20/reference/bpmn20/events/message-events/#message-end-event) will
stop the execution of a BPMN process and finish by sending a message.