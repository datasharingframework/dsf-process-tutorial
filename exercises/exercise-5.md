[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • **Exercise 5** • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 5 - Exclusive Gateways
In this exercise you will add a decision point to the `exampleorg_dicProcess`: based on the value of the `tutorial-input` parameter sent with the start Task, the process will either trigger `exampleorg_cosProcess` or stop right there.

The file you will primarily work in is `dic-process.bpmn` and `DicTask.java`.

Solutions to this exercise are found on the branch `solutions/exercise-5`.

<details>
<summary>Background reading (documentation links for this exercise)</summary>

- [Exclusive Gateways](https://dsf.dev/process-development/api-v2/bpmn/gateways.html)
- [Conditions](https://dsf.dev/process-development/api-v2/bpmn/conditions.html)
</details>

## Exercise Tasks

1. Open `tutorial-process/src/main/resources/bpe/dic-process.bpmn` in **Camunda Modeler** and add an **Exclusive Gateway** between the `DicTask` service task and the current end/message-end event.

    Connect two outgoing sequence flows from the gateway:
    - **Path A**: leads to the Message End Event that sends `helloCos` (from Exercise 4) → starts `exampleorg_cosProcess`
    - **Path B**: leads to a plain End Event → stops `exampleorg_dicProcess` without contacting COS

    <details>
    <summary>Camunda Modeler instructions</summary>

    - Click the `DicTask` service task → select **Append Gateway**
    - Draw a sequence flow from the gateway to the existing Message End Event (Path A)
    - Draw another sequence flow from the gateway to a new plain End Event (Path B)
    </details>
    
2. Add a **condition expression** to each outgoing sequence flow so the BPE knows which path to take at runtime.

    The BPE uses **JUEL** (Java Unified Expression Language) for conditions. Condition expressions read process variables by name. For a boolean variable called `sendToCos`, the expressions would be:

    | Sequence flow | Condition expression | Meaning |
    |---|---|---|
    | Path A (→ COS) | `${sendToCos}` | Take this path when `sendToCos` is `true` |
    | Path B (→ End) | `${!sendToCos}` | Take this path when `sendToCos` is `false` |

    You can freely choose the variable name — just make sure it matches exactly between the condition expressions here and the process variable you set in step 4.

    <details>
    <summary>Camunda Modeler instructions</summary>

    - Click a sequence flow arrow → in the properties panel on the right, find **Condition Type** → select **Expression**
    - Enter the expression (e.g. `${sendToCos}`) in the **Expression** field
    </details>
    
3. In the `DicTask` class, read the `tutorial-input` string from the start Task and derive a boolean decision from it.

    **What:** Evaluate whether the input value signals that the COS process should be started.  
    **Why:** The gateway condition reads a process variable — that variable must be set by your Java code before the gateway is reached. This is done in the next step.

    <details>
    <summary>Not sure how to read the tutorial-input parameter?</summary>

    Use the `TaskHelper` together with `variables` as you did in Exercise 2. The input type code is `tutorial-input` from CodeSystem `http://example.org/fhir/CodeSystem/tutorial`. Once you have the string value, decide on a convention — for example, `"true"` means start COS, anything else means stop.
    </details>

4. Store the boolean result as a **named process variable** using `variables.setBoolean(...)`. The variable name must be **identical** to the name used in the condition expressions in step 2.


## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-5
```

Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `exampleorg_dicProcess` and `exampleorg_cosProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `exampleorg_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.

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

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_cosProcess`. 

5. Start the `exampleorg_dicProcess` by posting a specific FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure.

   Verify that the `exampleorg_dicProcess` was executed successfully by the `dic.dsf.test` DSF BPE server and possibly the `exampleorg_cosProcess` by the `cos.dsf.test` DSF BPE server, depending on whether decision of your algorithm based on the input parameter allowed starting the `exampleorg_cosProcess`.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • **Exercise 5** • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)