[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6** • [Exercise 7](exercise-7.md)
___

# Exercise 6 - Event Based Gateways and Intermediate Events
In this exercise we will look at message flow between three organizations as well as how to continue a waiting process if no return message arrives. 
With this exercise we will add a third process and complete a message loop from `dic.dsf.test` to `cos.dsf.test` to `hrp.dsf.test` and back to `dic.dsf.test`.

In order to solve this exercise, you should have solved exercise 5 and read the topics on 
[Managing Multiple Incoming Messages and Missing Messages](https://dsf.dev/process-development/api-v2/guides/managing-mutiple-incoming-messages-and-missing-messages.html)
and [Message Correlation](https://dsf.dev/process-development/api-v2/dsf/message-correlation.html).

Solutions to this exercise are found on the branch `solutions/exercise-6`.

## Exercise Tasks

1. Modify the `tutorialorg_dicProcess`:
   * Change the [Message End Event](https://dsf.dev/process-development/api-v2/bpmn/messaging.html#message-end-event) to an [Intermediate Message Throw Event](https://dsf.dev/process-development/api-v2/bpmn/messaging.html#message-intermediate-throwing-event). This also means that `HelloCosMessage.java` needs to implement `MessageIntermediateThrowEvent` instead of `MessageEndEvent`.
   * Add an [Event Based Gateway](https://dsf.dev/process-development/api-v2/bpmn/gateways.html#event-based-gateway) after the throw event
   * Configure two cases for the [Event Based Gateway](https://dsf.dev/process-development/api-v2/bpmn/gateways.html#event-based-gateway):
      1. An [Intermediate Message Catch Event](https://dsf.dev/process-development/api-v2/bpmn/messaging.html#message-intermediate-catching-event) to catch the `goodbyeDic` message from the `tutorialorg_hrpProcess`.
      1. An [Intermediate Timer Catch Event](https://dsf.dev/process-development/api-v2/bpmn/timer-intermediate-catching-events.html) to end the process if no message is sent by the `tutorialorg_hrpProcess` after two minutes.
         Make sure both cases finish with a process End Event.
1. Modify the `tutorialorg_cosProcess` to use a [Message End Event](https://dsf.dev/process-development/api-v2/bpmn/messaging.html#message-end-event) to trigger the process in file `hrp-process.bpmn`. Figure out the values for the `instantiatesCanonical`, `profile` and `messageName` input parameters of the [Message End Event](https://dsf.dev/process-development/api-v2/dsf/messaging.html#message-end-event) based on the [AcitvityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) in file `hrp-process.xml`. Change the `Cos Task` element into a Service Task and include the `CosTask` as the implementation.
1. Modify the process in file `hrp-process.bpmn` and set the _process definition key_ and _version_. Figure out the appropriate values based on the [AcitvityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) in file `hrp-process.xml`.
1. Add a new process authorization extension element to the ActivityDefinition for `tutorialorg_dicProcess` using the [parent organization role coding](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html) where
     only remote organizations which are part of `medizininformatik-initiative.de` and have the `HRP` role are allowed to request `goodByeDic` messages and only
     organizations which are part of `medizininformatik-initiative.de` and have the `DIC` role are allowed to receive `goodByeDic` messages
     <details>
     <summary>Don't know which values to choose for roles?</summary>

     Take a look at the [dsf-organization-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-organization-role-2.0.0.xml) CodeSystem.
     </details>
1. Forward the value from the [Task.input](https://dsf.dev/process-development/api-v2/fhir/task.html) parameter of the `dicProcess` [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) to the `tutorialorg_cosProcess` using the `HelloCosMessage`. To do this, you need to override `HelloCosMessage#getAdditionalInputParameters`. Don't forget to also add the definition of your `tutorial-input` [Input Parameter](https://dsf.dev/process-development/api-v2/fhir/task.html#task-input-parameters) from `task-start-dic-process.xml` to `task-hello-cos.xml`. 
1. Add the process in file `hrp-process.bpmn` to the `TutorialProcessPluginDefinition` and configure the FHIR resources needed for the three processes.
1. Add the `CosTask`, `HelloHrpMessage `, `HrpTask` and `GoodbyeDicMessage` classes as Spring Beans. Don't forget the scope.
1. Again, we introduced changes that break compatibility. Older plugin versions won't execute the HRP process because the process ID in the BPMN model is still invalid and it is missing a version. Increment your resource version to `1.4`.


## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-6
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `tutorialorg_dicProcess`, `tutorialorg_cosProcess` and `tutorialorg_hrpProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `tutorialorg_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Don't forget that you will have to add the client certificate for the `HRP` instance to your browser the same way you added it for the `DIC` and `COS` instances
in [exercise 1](exercise-1.md) and [exercise 4](exercise-5.md) or use the Keycloak user `Tyler Tester` with username `test` and password `test`. Otherwise, you won't be able to access [https://hrp/fhir](https://hrp/fhir). You can find the client certificate
in `.../dsf-process-tutorial/browser-certs/hrp/hrp-client.p12` (password: password).

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_dicProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_dicProcess`.


5. Start the DSF FHIR server for the `hrp.dsf.test` organization in a fifth at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-fhir
   ```
   Verify the DSF FHIR server started successfully at https://hrp/fhir.

6. Start the DSF BPE server for the `hrp.dsf.test` organization in a sixth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_hrpProcess`. The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resource. Go to https://hrp/fhir/ActivityDefinition to check if the expected resource was created by the BPE while deploying the process. The returned FHIR [Bundle](http://hl7.org/fhir/R4/bundle.html) should contain a three [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resources. Also, go to https://hrp/fhir/StructureDefinition?url=http://example.org/fhir/StructureDefinition/task-hello-hrp to check if the expected [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) profile was created.

7. Start the `tutorialorg_dicProcess` by posting a specific FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure.

   Verify that the FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server and the `tutorialorg_dicProcess` was executed by the DSF BPE server of the `dic.dsf.test` organization. The DSF BPE server of the `dic.dsf.test` organization should print a message showing that a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to start the `tutorialorg_cosProcess` was sent to the `cos.dsf.test` organization.  
   Verify that a FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server of the `cos.dsf.test` organization and the `tutorialorg_cosProcess` was executed by the DSF BPE server of the `cos.dsf.test` organization. The DSF BPE server of the `cos.dsf.test` organization should print a message showing that a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to start the `tutorialorg_hrpProcess` was sent to the `hrp.dsf.test` organization.  
   
   Based on the value of the Task.input parameter you send, the `tutorialorg_hrpProcess` will either send a `goodbyeDic` message to the `dic.dsf.test` organization or finish without sending a message.
   
   To trigger the `goodbyeDic` message, use `send-response` as the `tutorial-input` input parameter.
   
   Verify that the `tutorialorg_dicProcess` either finishes with the arrival of the `goodbyeDic` message or after waiting for two minutes.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6** • [Exercise 7](exercise-7.md)