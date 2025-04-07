[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • **Exercise 2** • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 2 - Environment Variables and Input Parameters
BPMN processes might require additional information during execution, e.g. for configuration purposes. 
We will take a look at two possibilities on how to pass additional information to a BPMN process: Environment Variables and Input Parameters.   
The goal of this exercise is to enhance the `dsfdev_dicProcess` by trying them both. 
In both cases the information will be available in the `execute` method of your service class.

In order to solve this exercise, you should have solved the first exercise and read the topics on
[Environment Variables](../learning/concepts/dsf/environment-variables.md), 
[Task Input Parameters](../learning/concepts/fhir/task.md#task-input-parameters),
[Accessing Task Resources During Execution](../learning/guides/accessing-task-resources-during-execution.md),
[Placeholders](../learning/concepts/dsf/about-version-placeholders-and-urls.md#placeholders) and
[Read Access Tag](../learning/concepts/dsf/read-access-tag.md).

Solutions to this exercise are found on the branch `solutions/exercise-2`.


## Exercise Tasks
1. Add a new boolean variable to the `TutorialConfig` class. It will enable/disable logging and have its value injected from an environment variable. Add the annotation and specify the default value as `false`. You may freely choose a name for your environment variable here. Just make sure you follow the naming convention explained in [Environment Variables](../learning/concepts/dsf/environment-variables.md).
2. Modify the constructor of the `DicTask` class to use the newly created variable. Don't forget to change the `DicTask` bean in `TutorialConfig`. If you previously registered it through `ActivityPrototypeBeanCreator` you now have to register it as a separate Bean because `ActivityPrototypeBeanCreator` can only create beans through default constructors (scope has to be `prototype`).
   <details>
   <summary>Don't know how to register prototype beans?</summary>

   Check out [this guide](../learning/guides/registering-prototype-beans.md).
   </details>

3. Use the value of the environment variable in the `DicTask` class to decide whether the log message from exercise 1 should be printed.
4. Add the new environment variable to the `dic-bpe` service in `dev-setup/docker-compose.yml` and set the value to `"true"`.
5. Create a new [CodeSystem](../learning/concepts/fhir/codesystem.md) with url `http://dsf.dev/fhir/CodeSystem/tutorial` having a concept with code `tutorial-input` and name the file `tutorial.xml`. Don't forget to add the `read-access-tag`.
   <details>
   <summary>Don't know how to create a CodeSystem?</summary>

   Check out [this guide](../learning/guides/creating-codesystems-for-dsf-processes.md).
   </details>

   <details>
   <summary>Don't know where to put the CodeSystem?</summary>
   
   `tutorial-process/src/main/resources/fhir/CodeSystem`.
   </details>

6. Create a new [ValueSet](../learning/concepts/fhir/valueset.md) with url `http://dsf.dev/fhir/ValueSet/tutorial` that includes all concepts from the [CodeSystem](../learning/concepts/fhir/codesystem) and name the file `tutorial.xml`. Don't forget to add the `read-access-tag`.
   <details>
   <summary>Don't know how to create a ValueSet?</summary>

   Check out [this guide](../learning/guides/creating-valuesets-for-dsf-processes.md).
   </details>

   <details>
   <summary>Don't know where to put the ValueSet?</summary>

   `tutorial-process/src/main/resources/fhir/ValueSet`.
   </details>

7. Add a new input parameter of type `tutorial-input` with `Task.input.value[x]` as a `string` to the `task-start-dic-process.xml` [Task](../learning/concepts/fhir/task.md) profile.
   <details>
   <summary>Don't know how to add a new input parameter?</summary>

   Check out [this guide](../learning/guides/adding-task-input-parameters-to-task-profiles.md).
   </details>

8. `task-start-dic-process` and by extension the process `dsfdev_dicProcess` now require additional FHIR resources. Make sure the return value for `TutorialProcessPluginDefinition#getFhirResourcesByProcessId` also includes the new [CodeSystem](../learning/concepts/fhir/codesystem.md) and [ValueSet](../learning/concepts/fhir/valueset.md) resources for the `dsfdev_dicProcess`.
9. Read the new input parameter in the `DicTask` class from the start [Task](../learning/concepts/fhir/task.md) and add the value to the log message from exercise 1.
   <details>
   <summary>Don't know how to get the input parameter?</summary>
   
   The `TaskHelper` instance will prove useful here. Use it in conjunction with `variables` to get the right Task resource from the BPMN process execution.
   </details>
10. We just changed the elements a Task resource has to include. So you need to change `example-task.xml` for [cURL](../learning/guides/starting-a-process-via-task-resources.md#using-curl) or `Task/task-start-dic-process.xml`, if you want to use the web interface, to include the new input parameter. The actual value may be any arbitrary string.
   This also means that we need to change the plugin version, since a Task made according to the old StructureDefinition won't be valid for processes still expecting the old StructureDefinition. The new resource version shall be `2.1`.

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-2
```

Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `dsfdev_dicProcess` can be executed successfully, we need to deploy it into a DSF instance and execute the process. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`.

3. Start the `dsfdev_dicProcess` by posting an appropriate FHIR [Task](../learning/concepts/fhir/task.md) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](../learning/guides/starting-a-process-via-task-resources.md) again if you are unsure.

   Verify that the `dsfdev_dicProcess` was executed by the DSF BPE server. The BPE server should:
    * Print a message showing that the process was started.
    * If logging is enabled - print the log message and the value of the input parameter you added to the `DicTask`
      implementation.
    * Print a message showing that the process finished.
    
  Check that you can disable logging of your message by modifying the `docker-compose.yml` file and configuring your environment variable with the value `"false"` or removing the environment variable.  
  _Note: Changes to environment variable require recreating the docker container._
  
  Also check that modification to the [Task](../learning/concepts/fhir/task.md) input parameter specified in the `TutorialExampleStarter` class, have the appropriate effect on your log message.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • **Exercise 2** • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
