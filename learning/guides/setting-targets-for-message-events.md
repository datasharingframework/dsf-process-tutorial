### Setting Targets for Message Events

Setting a target for a message event requires a `Target` object. To create one, you require a target's organization identifier, endpoint identifier and endpoint address.
You can find these values by visiting the DSF FHIR server's web interface. In the top right corner, click
the `Show Bookmarks` button, then select `Endpoint`. You will be taken to a list of all Endpoints available to the FHIR server.  
There are two ways of adding `targets` to the BPMN execution variables:
#### 1. Adding the target in the message event implementation
In your message event implementation (the class extending `AbstractTaskMessageSend`), you can override `AbstractTaskMessageSend#execute`,
add your targets and then call the super-method.
#### 2. Adding the target in a service  task right before the message event
This is the preferred method of this tutorial but both methods will work perfectly fine. For our use cases, we usually prefer this one
since there is enough complexity to warrant putting it into a separate BPMN [Service Task](../concepts/bpmn/service-tasks.md).

In both cases you can access methods to create and set `targets` through the `Variables` instance.
