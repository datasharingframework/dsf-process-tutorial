[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6**
___

# Exercise 6 - Event Based Gateways and Intermediate Events
In the final exercise we will look at message flow between three organizations as well as how to continue a waiting process if no return message arrives. 
With this exercise we will add a third process and complete a message loop from `dic.dsf.test` to `Test_COR` to `hrp.dsf.test` back to `dic.dsf.test`.

In order to solve this exercise, you should have solved exercise 5 and read the topics on 
[Managing Multiple Incoming Messages and Missing Messages](../learning/guides/managing-mutiple-incoming-messages-and-missing-messages.md)
and [Message Correlation](../learning/concepts/dsf/message-correlation.md).

Solutions to this exercise are found on the branch `solutions/exercise-6`.

## Exercise Tasks

1. Modify the `dsfdev_dicProcess`:
   * Change the [Message End Event](../learning/concepts/bpmn/messaging.md#message-end-event) to an [Intermediate Message Throw Event](../learning/concepts/bpmn/messaging.md#message-intermediate-throwing-event)
   * Add an [Event Based Gateway](../learning/concepts/bpmn/gateways.md#event-based-gateway) after the throw event
   * Configure two cases for the [Event Based Gateway](../learning/concepts/bpmn/gateways.md#event-based-gateway):
      1. An [Intermediate Message Catch Event](../learning/concepts/bpmn/messaging.md#message-intermediate-catching-event) to catch the `goodbyeDic` message from the `dsfdev_hrpProcess`.
      1. An [Intermediate Timer Catch Event](../learning/concepts/bpmn/timer-intermediate-catching-events.md) to end the process if no message is sent by the `dsfdev_hrpProcess` after two minutes.
         Make sure both cases finish with a process [End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/none-events/).
1. Modify the `dsfdev_cosProcess` to use a [Message End Event](../learning/concepts/bpmn/messaging.md#message-end-event) to trigger the process in file `hrp-process.bpmn`. Figure out the values for the `instantiatesCanonical`, `profile` and `messageName` input parameters of the [Message End Event](../learning/concepts/bpmn/messaging.md#message-end-event) based on the [AcitvityDefinition](../learning/concepts/fhir/activitydefinition.md) in file `hrp-process.xml`.
1. Modify the process in file `hrp-process.bpmn` and set the _process definition key_ and _version_. Figure out the appropriate values based on the [AcitvityDefinition](../learning/concepts/fhir/activitydefinition.md) in file `hrp-process.xml`.
1. Add a new process authorization extension element to the ActivityDefinition for `dsfdev_dicProcess` using the [parent organization role coding](../learning/concepts/dsf/examples-for-requester-and-recipient-elements.md) where
     only remote organizations which are part of `medizininformatik-initiative.de` and have the `HRP` role are allowed to request `goodByeDic` messages and only
     organizations which are part of `medizininformatik-initiative.de` and have the `DIC` role are allowed to receive `goodByeDic` messages
     <details>
     <summary>Don't know which values to choose for roles?</summary>

     Take a look at the [dsf-organization-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-organization-role-1.0.0.xml) CodeSystem.
     </details>
1. Forward the value from the [Task.input](../learning/concepts/fhir/task.md) parameter of the `dicProcess` [Task](../learning/concepts/fhir/task.md) to the `dsfdev_cosProcess` using the `HelloCosMessage`. To do this, you need to override `HelloCosMessage#getAdditionalInputParameters`. Don't forget to also add the definition of your `tutorial-input` [Input Parameter](../learning/concepts/fhir/task.md#task-input-parameters) from `task-start-dic-process.xml` to `task-hello-cos.xml`. 
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
To verify the `dsfdev_dicProcess`, `dsfdev_cosProcess` and `dsfdev_hrpProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `dsfdev_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Don't forget that you will have to add the client certificate for the `HRP` instance to your browser the same way you added it for the `DIC` and `COS` instances
in [exercise 1](exercise-1.md) and [exercise 4](exercise-5.md) or use the Keycloak user you created in [exercise 3](exercise-3.md) for the `hrp` realm. Otherwise, you won't be able to access [https://hrp/fhir](https://hrp/fhir). You can find the client certificate
in `.../dsf-process-tutorial/test-data-generator/cert/hrp-client/hrp-client_certificate.p12` (password: password).

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully.

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`.


5. Start the DSF FHIR server for the `hrp.dsf.test` organization in a fifth at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-fhir
   ```
   Verify the DSF FHIR server started successfully. You can access the webservice of the DSF FHIR server at https://hrp/fhir. To authenticate yourself to the server you can use the client certificate located at `.../dsf-process-tutorial/test-data-generator/cert/hrp-client/hrp-client_certificate.p12` (Password: `password`).

6. Start the DSF BPE server for the `hrp.dsf.test` organization in a sixth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_hrpProcess`. The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new [ActivityDefinition](../learning/concepts/fhir/activitydefinition.md) resource. Go to https://hrp/fhir/ActivityDefinition to check if the expected resource was created by the BPE while deploying the process. The returned FHIR [Bundle](http://hl7.org/fhir/R4/bundle.html) should contain a three [ActivityDefinition](../learning/concepts/fhir/activitydefinition.md) resources. Also, go to https://hrp/fhir/StructureDefinition?url=http://dsf.dev/fhir/StructureDefinition/task-hello-hrp to check if the expected [Task](../learning/concepts/fhir/task.md) profile was created.

7. Start the `dsfdev_dicProcess` by posting a specific FHIR [Task](../learning/concepts/fhir/task.md) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](../learning/guides/starting-a-process-via-task-resources.md) again if you are unsure.

   Verify that the FHIR [Task](../learning/concepts/fhir/task.md) resource was created at the DSF FHIR server and the `dsfdev_dicProcess` was executed by the DSF BPE server of the `dic.dsf.test` organization. The DSF BPE server of the `dic.dsf.test` organization should print a message showing that a [Task](../learning/concepts/fhir/task.md) resource to start the `dsfdev_cosProcess` was sent to the `cos.dsf.test` organization.  
   Verify that a FHIR [Task](../learning/concepts/fhir/task.md) resource was created at the DSF FHIR server of the `cos.dsf.test` organization and the `dsfdev_cosProcess` was executed by the DSF BPE server of the `cos.dsf.test` organization. The DSF BPE server of the `cos.dsf.test` organization should print a message showing that a [Task](../learning/concepts/fhir/task.md) resource to start the `dsfdev_hrpProcess` was sent to the `hrp.dsf.test` organization.  
   
   Based on the value of the Task.input parameter you send, the `dsfdev_hrpProcess` will either send a `goodbyeDic` message to the `dic.dsf.test` organization or finish without sending a message.
   
   To trigger the `goodbyeDic` message, use `send-response` as the `http://dsf.dev/fhir/CodeSystem/tutorial#tutorial-input` input parameter.
   
   Verify that the `dsfdev_dicProcess` either finishes with the arrival of the `goodbyeDic` message or after waiting for two minutes.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6**