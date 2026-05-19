[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • **Exercise 4** • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 4 - Messaging
In this exercise you will make two organizations talk to each other: `dic.dsf.test` will automatically trigger a process at `cos.dsf.test` by sending a FHIR Task resource across organizations. You will configure both BPMN files and the required FHIR resources for this to work.

Solutions to this exercise are found on the branch `solutions/exercise-4`.

<details>
<summary>Background reading (documentation links for this exercise)</summary>

- [Messaging](https://dsf.dev/process-development/api-v2/dsf/messaging.html)
- [Message Activities](https://dsf.dev/process-development/api-v2/dsf/message-activities.html)
- [Version Pattern](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html#version-pattern)
- [URLs](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html#urls)
- [Target and Targets](https://dsf.dev/process-development/api-v2/dsf/target-and-targets.html)
</details>

## Exercise Tasks

1. Replace the [End Event](https://docs.camunda.org/manual/7.17/reference/bpmn20/events/none-events/#none-end-event) of the `exampleorg_dicProcess` in `dic-process.bpmn` with a [Message End Event](https://dsf.dev/process-development/api-v2/dsf/messaging.html#message-end-event). Give the event a name and an ID, then configure it as follows:

    **In Camunda Modeler:**
    - Click the End Event circle → change its type to **Message End Event** (envelope icon)
    - In the properties panel, set **Implementation** to **Java Class** and enter: `org.tutorial.process.tutorial.message.HelloCosMessage`
    - Switch to the **Field Injections** tab and add three entries:

    | Field name | Type | Value |
    |---|---|---|
    | `profile` | String | `http://example.org/fhir/StructureDefinition/task-hello-cos|#{version}` |
    | `messageName` | String | `helloCos` |
    | `instantiatesCanonical` | String | *(see hint below)* |

    **What do these three field injections mean?**

    | Field | Purpose                                                                                                                                                                       |
    |---|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    | `profile` | The StructureDefinition URL that the FHIR Task sent to the target organization must conform to. This links the outgoing message to a specific Task profile.                   |
    | `messageName` | The BPMN message name that identifies which Message Start Event at the target process should be triggered. Must match exactly the message name you set in `cos-process.bpmn`. |
    | `instantiatesCanonical` | The process URI + version of the *target* process to be started.                                                                                                              |

    <details>
    <summary>Can't figure out the instantiatesCanonical value?</summary>

    The process definition key of the COS process is `cosProcess`. Follow the [URL pattern](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html#urls) to create the correct value.
    </details>
2. Open `cos-process.bpmn` and configure the **Message Start Event** message name to match the `messageName` value from step 1 (`helloCos`).

3. Create a new StructureDefinition Task profile for the `helloCos` message. Save it as a resource under `fhir/StructureDefinition/task-hello-cos.xml`.

    <details>
    <summary>Don't know how to get started?</summary>

    Base this Task profile on `fhir/StructureDefinition/task-start-dic-process.xml`. Key differences: the `instantiatesCanonical` must point to the COS process URI, and the `message-name` input slice value must be `helloCos`. The `correlation-key` slice should be allowed (`max` not `0`) since inter-organization messages need a correlation key. Remove the `tutorial-input` slice if it was added in exercise 2.
    </details>

4. Create a new ActivityDefinition for the `exampleorg_cosProcess`. Save it as `fhir/ActivityDefinition/cos-process.xml`. Configure the authorization extension:
    - **requester**: `dic.dsf.test` (remote organization) → use code `REMOTE_ORGANIZATION` with a nested `extension-process-authorization-organization` pointing to `dic.dsf.test`
    - **recipient**: `cos.dsf.test` (local organization) → use code `LOCAL_ORGANIZATION` with a nested `extension-process-authorization-organization` pointing to `cos.dsf.test`

    <details>
    <summary>Don't know how to get started?</summary>

    Base this ActivityDefinition on `fhir/ActivityDefinition/dic-process.xml` and adapt `url`, `name`, `title`, `message-name`, `task-profile`, `requester` and `recipient`. Refer back to the [documentation on the process authorization extension](https://dsf.dev/process-development/api-v2/dsf/understanding-the-process-authorization-extension.html) for the XML patterns.
    Or take a look at the [guide on creating ActivityDefinitions](https://dsf.dev/process-development/api-v2/guides/creating-activity-definitions.html).
    </details>

5. Add the `exampleorg_cosProcess` and its resources to the `TutorialProcessPluginDefinition` class (`TutorialProcessPluginDefinition.java`). Add a new entry to the Map returned by `getFhirResourcesByProcessId()` using the full process name of the `cosProcess` as the key and a list containing the new ActivityDefinition and StructureDefinition files as the value. Also add `bpe/cos-process.bpmn` to `getProcessModels()`.

6. Modify the `DicTask` service class to set the `target` process variable for the `cos.dsf.test` organization.

    The `target` variable tells the DSF's Message End Event where to send the outgoing FHIR Task. Call `variables.createTarget(...)` with three parameters:

    | Parameter | What it identifies                                                                                                                                            | Value for this exercise |
    |---|---------------------------------------------------------------------------------------------------------------------------------------------------------------|---|
    | Organization identifier | The DSF organization that should receive the message. Can be found in the allow list (e.g for the cos organization in `dev-setup/cos/fhir/conf/bundle.xml`). | `"cos.dsf.test"` |
    | Endpoint identifier | The DSF endpoint name registered at that organization. Can be found in the allow list (e.g for the cos organization in `dev-setup/cos/fhir/conf/bundle.xml`). | `"cos.dsf.test_Endpoint"` |
    | FHIR base URL | The FHIR server URL of the target DSF instance                                                                                                                | `"https://cos/fhir"` |

    ```java
    Target target = variables.createTarget(
        "cos.dsf.test",
        "cos.dsf.test_Endpoint",
        "https://cos/fhir"
    );
    variables.setTarget(target);
    ```

7. Configure the `HelloCosMessage` class as a Spring prototype bean in the `TutorialConfig` class, the same way you registered `DicTask`.

8. Again, we introduced changes that break compatibility. Older plugin versions at the COS instance won't be able to handle the Task resource type we added earlier. Increment your resource version to `1.3`.

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-4
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `exampleorg_dicProcess` and `exampleorg_cosProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `exampleorg_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Don't forget that you will have to add the client certificate for the `COS` instance to your browser the same way you added it for the `DIC` instance
in [exercise 1](exercise-1.md) or use the Keycloak user `Tyler Tester` with username `test` and password `test`. Otherwise, you won't be able to access [https://cos/fhir](https://cos/fhir). You can find the client certificate
in `.../dsf-process-tutorial/browser-certs/cos/cos-client.p12` (password: password).

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in another console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_dicProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in another console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_cosProcess`. The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resource. Go to https://cos/fhir/ActivityDefinition to check if the expected resource was created by the BPE while deploying the process. The returned FHIR [Bundle](http://hl7.org/fhir/R4/bundle.html) should contain two [ActivityDefinition](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html) resources. Also, go to https://cos/fhir/StructureDefinition?url=http://example.org/fhir/StructureDefinition/task-hello-cos to check if the expected [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) profile was created.

5. Start the `exampleorg_dicProcess` by posting a specific FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure.

   Verify that the FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server and the `exampleorg_dicProcess` was executed by the DSF BPE server of the `dic.dsf.test` organization. The DSF BPE server of the `dic.dsf.test` organization should print a message showing that a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to start the `exampleorg_cosProcess` was sent to the `cos.dsf.test` organization.  
   Verify that a FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server of the `cos.dsf.test` organization and the `exampleorg_cosProcess` was then executed by the DSF BPE server of the `cos.dsf.test` organization.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • **Exercise 4** • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)