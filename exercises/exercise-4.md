[Prerequisites](prerequisites.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • **Exercise 4** • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
___

# Exercise 4 - Messaging
Communication between organizations in BPMN processes is modeled using message flow. The third exercise shows how a process at one organization can trigger a process at another organization.

To demonstrate communication between two organizations we will configure message flow between the processes `dsfdev_dicProcess` and `dsfdev_cosProcess`. After that, the processes are to be executed at the organizations `Test_DIC` and `Test_COS` respectively in the docker dev setup, with the former triggering execution of the latter by automatically sending a [Task](http://hl7.org/fhir/R4/task.html) resource from organization `Test_DIC` to organization `Test_COS`.

In order to solve this exercise, you should have solved exercise 2 and read the topics on
[Messaging](basic-concepts-and-guides.md#messaging),
[Message Delegates](basic-concepts-and-guides.md#message-delegates),
[Version Pattern](basic-concepts-and-guides.md#version-pattern),
[URLs](basic-concepts-and-guides.md#urls) 
and [Setting Targets for Message Events](basic-concepts-and-guides.md#setting-targets-for-message-events).

Solutions to this exercise are found on the branch `solutions/exercise-4`.

## Exercise Tasks
1. Replace the [End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/none-events/#none-end-event) of the `dsfdev_dicProcess` process in the `hello-dic.bpmn` file with a [Message End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/message-events/#message-end-event). Give the [Message End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/message-events/#message-end-event) a name and an ID and set its implementation to the `HelloCosMessage` class.  
   Configure field injections `instantiatesCanonical`, `profile` and `messageName` in the BPMN model for the [Message End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/message-events/#message-end-event).
    Use `http://dsf.dev/fhir/StructureDefinition/task-hello-cos|#{version}` as the profile and `cosProcess` as the message name. Figure out what the appropriate `instantiatesCanonical` value is, based on the name (process definition key) of the process to be triggered.
   <details>
   <summary>Can't remember how instantiatesCanonical is built?</summary>

   Read the concept [here](basic-concepts-and-guides.md#urls) again.
    </details>
1. Modify the `dsfdev_cosProcess` process in the `cos-process.bpmn` file and configure the message name of the [Message Start Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/message-events/#message-start-event) with the same value as the message name of the [Message End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/message-events/#message-end-event) in the `dsfdev_dicProcess` process. 
1. Create a new [StructureDefinition](http://hl7.org/fhir/R4/structuredefinition.html) with a [Task](http://hl7.org/fhir/R4/task.html) profile for the `cosProcess` message.
    <details>
   <summary>Don't know how to get started?</summary>
   
   You can base this [Task](http://hl7.org/fhir/R4/task.html) profile off the `StructureDefinition/task-start-dic-process.xml` resource. Then look for elements that need to be added, changed or can be omitted.
    </details>
1. Create a new [ActivityDefinition](http://hl7.org/fhir/R4/activitydefinition.html) resource for the `dsfdev_cosProcess` process and configure the authorization extension to allow the `Test_DIC` organization as the requester and the `Test_COS` organization as the recipient.
   <details>
   <summary>Don't know how to get started?</summary>

   You can base this ActivityDefinition off the `ActivityDefinition/hello-dic.xml` resource. Then look for elements that need to be added, changed or can be omitted.
   Or you can take a look at the [guide on creating ActivityDefinitions](basic-concepts-and-guides.md#creating-an-activitydefinition).
   </details>
1. Add the `dsfdev_cosProcess` process and its resources to the `TutorialProcessPluginDefinition` class. This will require a new mapping entry with the full process name of the `cosProcess` process as the key and a List of associated FHIR resources as the value.
1. Modify `DicTask` service class to set the `target` process variable for the `Test_COS` organization.
1. Configure the `HelloCosMessage` class as a Spring Bean in the `TutorialConfig` class. Don't forget the right scope.

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-4
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `dsfdev_dicProcess` and `dsfdev_cosProcess` processes can be executed successfully, we need to deploy them into DSF instances and execute the `dsfdev_dicProcess` process. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Don't forget that you will have to add the client certificate for the `COS` instance to your browser the same way you added it for the `DIC` instance
in [exercise 1](exercise-1.md) or use the Keycloak user you created in [exercise 3](exercise-3.md) for the `cos` realm. Otherwise, you won't be able to access [https://cos/fhir](https://cos/fhir). You can find the client certificate
in `.../dsf-process-tutorial/test-data-generator/cert/cos-client/cos-client_certificate.p12` (password: password).

1. Start the DSF FHIR server for the `Test_DIC` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully.

2. Start the DSF BPE server for the `Test_DIC` organization in another console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess` process.

3. Start the DSF FHIR server for the `Test_COS` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully. You can access the webservice of the DSF FHIR server at https://cos/fhir.

4. Start the DSF BPE server for the `Test_COS` organization in another console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_cosProcess` process. The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new [ActivityDefinition](http://hl7.org/fhir/R4/activitydefinition.html) resource. Go to https://cos/fhir/ActivityDefinition to check if the expected resource was created by the BPE while deploying the process. The returned FHIR [Bundle](http://hl7.org/fhir/R4/bundle.html) should contain two [ActivityDefinition](http://hl7.org/fhir/R4/activitydefinition.html) resources. Also, go to https://cos/fhir/StructureDefinition?url=http://dsf.dev/fhir/StructureDefinition/task-hello-cos to check if the expected [Task](http://hl7.org/fhir/R4/task.html) profile was created.

5. Start the `dsfdev_dicProcess` process by posting a specific FHIR [Task](http://hl7.org/fhir/R4/task.html) resource to the DSF FHIR server of the `Test_DIC` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](basic-concepts-and-guides.md#starting-a-process-via-task-resources) again if you are unsure.

   Verify that the FHIR [Task](http://hl7.org/fhir/R4/task.html) resource was created at the DSF FHIR server and the `dsfdev_dicProcess` process was executed by the DSF BPE server of the `Test_DIC` organization. The DSF BPE server of the `Test_DIC` organization should print a message showing that a [Task](http://hl7.org/fhir/R4/task.html) resource to start the `dsfdev_cosProcess` process was send to the `Test_COS` organization.  
   Verify that a FHIR [Task](http://hl7.org/fhir/R4/task.html) resource was created at the DSF FHIR server of the `Test_COS` organization and the `dsfdev_cosProcess` process was then executed by the DSF BPE server of the `Test_COS` organization.

___
[Prerequisites](prerequisites.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • **Exercise 4** • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)