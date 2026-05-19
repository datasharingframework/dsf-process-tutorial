[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6** • [Exercise 7](exercise-7.md)
___

# Exercise 6 - Event Based Gateways and Intermediate Events
In this exercise you will complete a three-organization message loop: `dic.dsf.test` → `cos.dsf.test` → `hrp.dsf.test` → back to `dic.dsf.test`. The DIC process will no longer end immediately after sending — it will wait for a reply from HRP (or time out after two minutes).

Solutions to this exercise are found on the branch `solutions/exercise-6`.

<details>
<summary>Background reading (documentation links for this exercise)</summary>

- [Managing Multiple Incoming Messages and Missing Messages](https://dsf.dev/process-development/api-v2/guides/managing-mutiple-incoming-messages-and-missing-messages.html)
- [Message Correlation](https://dsf.dev/process-development/api-v2/dsf/message-correlation.html)
- [Message Intermediate Throwing Event](https://dsf.dev/process-development/api-v2/bpmn/messaging.html#message-intermediate-throwing-event)
- [Timer Intermediate Catching Event](https://dsf.dev/process-development/api-v2/bpmn/timer-intermediate-catching-events.html)
- [Event Based Gateway](https://dsf.dev/process-development/api-v2/bpmn/gateways.html#event-based-gateway)
- [BPMN Process ID and Version Placeholder](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html)
- [ActivityDefinition Process Authorization: Requester and Recipient](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html)
</details>

## Exercise Tasks

1. **Modify `exampleorg_dicProcess`** (`dic-process.bpmn`)

    > **Why this step is needed:** Right now the DIC process *ends* when it sends the `helloCos` message. We need it to *pause and wait* for a `goodbyeDic` reply from HRP instead. To achieve this, the Message End Event must become an Intermediate Message Throw Event — meaning the process continues rather than ending.
   
    <details>
    <summary>Change Message End Event → Intermediate Message Throw Event</summary>
    
    - Click the Message End Event → change its type to **Intermediate Throw Event (Message)** (the envelope in a circle, not filled)
    - The field injections (`profile`, `messageName`, `instantiatesCanonical`) stay the same
    - In `HelloCosMessage.java`, change the implemented interface from `MessageEndEvent` to `MessageIntermediateThrowEvent`
    </details>
    
    <details>
    <summary>Add an Event Based Gateway after the throw event</summary>

    - Draw a sequence flow from the Intermediate Throw Event to a new **Event Based Gateway** (pentagon symbol)
    - The process will now wait at this gateway until one of the configured events fires
    </details>

    <details>
    <summary>Configure two outgoing paths from the gateway: one leading to a Message Intermediate Catch Event and one leading to a Timer Intermediate Catch Event. Both paths must end with a plain End Event.</summary>

    | Path | Event type                                                        | Purpose |
       |---|-------------------------------------------------------------------|---|
    | Path 1 | **Message Intermediate Catch Event** — message name: `goodbyeDic` | Process continues normally when HRP replies |
    | Path 2 | **Timer Intermediate Catch Event** — duration: `PT2M` (2 minutes) | Process ends with a timeout if HRP does not reply |
    
    Click the Event Based Gateway → choose either **Message Intermediate Catch Event** or **Timer Intermediate Catch Event**
    </details>

2. **Modify `exampleorg_cosProcess`** (`cos-process.bpmn`)

    Work through the four sub-steps in order:

    <details>
    <summary>Change the `Cos Task` element into a Service Task.</summary>

    - In Camunda Modeler, click the `Cos Task` element → change its type to **Service Task** (wrench icon)
    - Set **Implementation** to **Java Class** and enter: `org.tutorial.process.tutorial.service.CosTask`

    </details>
   
    > **Why:** A plain Task element in BPMN is just a label — it does not execute any Java code. A Service Task connects to a Java class and calls its `execute()` method when the process reaches it.

    <details>
    <summary>Change the End Event into a Message End Event.</summary>

    - Click the End Event → change its type to **Message End Event** (filled envelope)
    - This event will send a Task to `hrp.dsf.test` to start `exampleorg_hrpProcess`

    </details>
   
    <details>
    <summary>Set the Java class and BPMN message reference.</summary>

    - Set **Implementation** to **Java Class**: `org.tutorial.process.tutorial.message.HelloHrpMessage`
    - In the **Message** tab, create or select a BPMN message and name it `helloHrp`

    </details>
    
    <details>
    <summary>Set the Field Injections.</summary>
    
    Look at `fhir/ActivityDefinition/hrp-process.xml` to find the values:

    | Field | Where to look in hrp-process.xml | Value |
       |---|---|---|
    | `profile` | `<extension url="task-profile"><valueCanonical .../>` | `http://example.org/fhir/StructureDefinition/task-hello-hrp|#{version}` |
    | `messageName` | `<extension url="message-name"><valueString .../>` | `helloHrp` |
    | `instantiatesCanonical` | `<url value="..."/>` → append `|#{version}` | `http://example.org/bpe/Process/hrpProcess|#{version}` |

    </details>

3. **Fix the HRP process id and class names** (`hrp-process.bpmn`)

    <details>
    <summary>Set the process `id` (currently `change_me`) and `version tag`.</summary>

    - The id follows the [pattern](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html) `{publisher}_{processName}`. Derive it from `fhir/ActivityDefinition/hrp-process.xml`:
    - The `<url>` element reads: `http://example.org/bpe/Process/hrpProcess`
    - The last segment is `hrpProcess`
    - The publisher prefix in this tutorial is `exampleorg`
    - Result: **`exampleorg_hrpProcess`**
   
    </details>

    > **Note:** The field is called **`id`** on the `<bpmn:process>` element — not "key". It uniquely identifies the process within the BPE.

    <details>
    <summary>Fix the Java class name in the `HrpTask` service task.</summary>

    Currently the BPMN contains a wrong class name. The correct fully qualified class name is:
    ```
    org.tutorial.process.tutorial.service.HrpTask
    ```

    </details>

    <details>
    <summary>Fix the Java class name in the `GoodbyeDicMessage` end event.</summary>

    Same issue. The correct fully qualified class name is:
    ```
    org.tutorial.process.tutorial.message.GoodbyeDicMessage
    ```

    </details>

4. **Add a new authorization block to the DIC `ActivityDefinition`** (`fhir/ActivityDefinition/dic-process.xml`)

    The DIC process must now also accept the `goodbyeDic` message that HRP sends back. Add a **second** `<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">` block with the following four components:

    **4a) Message name**  
    **4b) Task profile**  
    **4c) [Requester](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html#remote-parent-organization-role)** — who may send `goodbyeDic`? Only remote organizations with the `HRP` role in `medizininformatik-initiative.de`.  
    **4d) [Recipient](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html#local-parent-organization-role-1)** — who may receive `goodbyeDic`? Only local organizations with the `DIC` role.  

    <details>
    <summary>Not sure which role codes are available?</summary>

    Take a look at the [dsf-organization-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-organization-role-2.0.0.xml) CodeSystem.
    </details>
   
    <details>
    <summary>Need a refresher on process authorization in ActivityDefinitions?</summary>

    Take a look at the [guide on creating ActivityDefinitions](https://dsf.dev/process-development/api-v2/guides/creating-activity-definitions.html#_2-extension-process-authorization).
    </details>

5. **Forward the `tutorial-input` parameter from DIC to COS**

    <details>
    <summary>Override `HelloCosMessage#getAdditionalInputParameters`</summary>

    The `HelloCosMessage` class (`HelloCosMessage.java`) currently sends no additional input parameters. Override `getAdditionalInputParameters` to read `tutorial-input` from the DIC start Task and include it in the outgoing COS Task.
    You can model your implementation on `HelloHrpMessage` which already does this.
    </details>
    
    <details>
    <summary>Add the `tutorial-input` slice to `task-hello-cos.xml`</summary>

    The COS Task profile (`fhir/StructureDefinition/task-hello-cos.xml`) must declare the `tutorial-input` input parameter, otherwise the DSF will reject the Task as non-conformant. Copy the `tutorial-input` slice definition from `task-start-dic-process.xml` and add it to `task-hello-cos.xml`.
    </details>

6. **Register all processes and resources in `TutorialProcessPluginDefinition`**

    - Add `bpe/hrp-process.bpmn` to `getProcessModels()`
    - Add a new map entry for `ConstantsTutorial.PROCESS_NAME_FULL_HRP` in `getFhirResourcesByProcessId()` listing its ActivityDefinition, StructureDefinition, and any CodeSystem/ValueSet files

7. **Add the `CosTask`, `HelloHrpMessage `, `HrpTask` and `GoodbyeDicMessage` classes as Spring Beans. Remember to use the right scope.**
8. **Again, we introduced changes that break compatibility. Older plugin versions won't execute the HRP process because the process id in the BPMN model was still invalid. Increment your resource version to `1.4`.**


## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-6
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `exampleorg_dicProcess`, `exampleorg_cosProcess` and `exampleorg_hrpProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `exampleorg_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Don't forget that you will have to add the client certificate for the `HRP` instance to your browser the same way you added it for the `DIC` and `COS` instances
in [exercise 1](exercise-1.md) and [exercise 4](exercise-4.md) or use the Keycloak user `Tyler Tester` with username `test` and password `test`. Otherwise, you won't be able to access [https://hrp/fhir](https://hrp/fhir). You can find the client certificate
in `.../dsf-process-tutorial/browser-certs/hrp/hrp-client.p12` (password: password).

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_dicProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_cosProcess`.

5. Start the DSF FHIR server for the `hrp.dsf.test` organization in a fifth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up hrp-fhir
   ```
   Verify the DSF FHIR server started successfully at https://hrp/fhir.

6. Start the DSF BPE server for the `hrp.dsf.test` organization in a sixth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up hrp-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_hrpProcess`. The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resource. Go to https://hrp/fhir/ActivityDefinition to check if the expected resource was created by the BPE while deploying the process. The returned FHIR [Bundle](http://hl7.org/fhir/R4/bundle.html) should contain three [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resources. Also, go to https://hrp/fhir/StructureDefinition?url=http://example.org/fhir/StructureDefinition/task-hello-hrp to check if the expected [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) profile was created.

7. Start the `exampleorg_dicProcess` by posting a specific FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure.

   Verify that the FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server and the `exampleorg_dicProcess` was executed by the DSF BPE server of the `dic.dsf.test` organization. The DSF BPE server of the `dic.dsf.test` organization should print a message showing that a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to start the `exampleorg_cosProcess` was sent to the `cos.dsf.test` organization.  
   Verify that a FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server of the `cos.dsf.test` organization and the `exampleorg_cosProcess` was executed by the DSF BPE server of the `cos.dsf.test` organization. The DSF BPE server of the `cos.dsf.test` organization should print a message showing that a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to start the `exampleorg_hrpProcess` was sent to the `hrp.dsf.test` organization.  
   
   Based on the value of the Task.input parameter you send, the `exampleorg_hrpProcess` will either send a `goodbyeDic` message to the `dic.dsf.test` organization or finish without sending a message.
   
   To trigger the `goodbyeDic` message, use `send-response` as the `tutorial-input` input parameter.
   
   Verify that the `exampleorg_dicProcess` either finishes with the arrival of the `goodbyeDic` message or after waiting for two minutes.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • **Exercise 6** • [Exercise 7](exercise-7.md)