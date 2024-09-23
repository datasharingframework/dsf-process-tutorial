[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • **Exercise 5** • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 5 - Exclusive Gateways
Different execution paths in a process based on the state of process variables can be achieved using Exclusive Gateways. In Exercise 5 we will examine how this can be implemented by modifying the `dsfdev_dicProcess`.

In order to solve this exercise, you should have solved Exercise 4 and read the topics on
[Exclusive Gateways](../learning/concepts/bpmn/gateways.md)
and [Conditions](../learning/concepts/bpmn/conditions.md).

Solutions to this exercise are found on the branch `solutions/exercise-5`.

## Exercise Tasks
1. Add an exclusive gateway to the `dsfdev_dicProcess` model and two outgoing sequence flows - the first starting the process `dsfdev_cosProcess`, the second stopping the process `dsfdev_dicProcess` without starting the process `dsfdev_cosProcess`.
2. Add condition expressions to each outgoing sequence flow which decides the path that will be taken based on a boolean value.
3. In the `DicTask` class, create a boolean variable which decides whether the `dsfdev_cosProcess` should be started based on the start Task's input parameter `tutorial-input`.
4. Add the boolean variable to the process execution variables, storing the decision. It needs to have the same name as the variable used in the condition expression from `2.`


## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-5
```

Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `dsfdev_dicProcess` and `dsfdev_cosProcess`es can be executed successfully, we need to deploy them into DSF instances and execute the `dsfdev_dicProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_cosProcess`. 

5. Start the `dsfdev_dicProcess` by posting a specific FHIR [Task](../learning/concepts/fhir/task.md) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](../learning/guides/starting-a-process-via-task-resources.md) again if you are unsure.

   Verify that the `dsfdev_dicProcess` was executed successfully by the `dic.dsf.test` DSF BPE server and possibly the `dsfdev_cosProcess` by the `cos.dsf.test` DSF BPE server, depending on whether decision of your algorithm based on the input parameter allowed to start the `dsfdev_dicProcess`.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • **Exercise 5** • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)