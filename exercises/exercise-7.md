[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • **Exercise 7**
___

# Exercise 7 - User Tasks and Task Output Parameters

This exercise introduces a new scenario which will serve as an example on how [User Tasks](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html), resource download and [Task Output Parameters](https://dsf.dev/process-development/api-v2/fhir/task.html#task-output-parameters)
may be utilized. The scenario is a voting process where one DSF instances of the tutorial setup will send a binary question (yes/no) to the other instances and itself.
The question can be set when starting the voting process. The question can will then be answerable through a [QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) resource on the instance's DSF FHIR server.
The answer then gets sent back to the instance which initiated the voting process. This exercise will focus on [User Tasks](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html) and [Task Output Parameters](https://dsf.dev/process-development/api-v2/fhir/task.html#task-output-parameters).
The scenario comes with a skeleton including two BPMN models. One for orchestrating the voting process called `tutorialorg_votingProcess` found in `voting-process.bpmn` and the subprocess which handles the vote itself found in `vote.bpmn`. 
It also includes most of the Java implementation for both processes and the required FHIR resources. Your task will be to fill in the parts concerning the [User Task](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html)
and [Task Output Parameters](https://dsf.dev/process-development/api-v2/fhir/task.html#task-output-parameters).

In order to solve this exercise, you should have solved exercise 6 and read the topics on
[User Tasks](https://dsf.dev/process-development/api-v2/guides/user-tasks-in-the-dsf.html#questionnaire-template), [Questionnaire and QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html)
and [adding Task Output Parameters](https://dsf.dev/process-development/api-v2/guides/adding-task-parameters-to-task-profiles.html).

Solutions to this exercise are found on the branch `solutions/exercise-7`. The skeleton can be found on the branch `skeleton/exercise-7`.

## Exercise Tasks
1. The StructureDefinition `task-start-voting-process.xml` describes the Task resource which starts the voting process. It already has an input parameter called `binary-question` which stores 
   the question you want to pose to all DSF instances in the network. When the entire voting process is finished, we would like to see all the voting results of the other instances listed as 
   output parameters in this resource.  
   Add a [Task Output Parameter](https://dsf.dev/process-development/api-v2/fhir/task.html#task-output-parameters) called `voting-result`:
   * This parameter stores the voting result of one instance as either `yes`, `no` or `timeout`. This can be achieved by using a `Coding` as the type for `Task.output.value[x]`. 
     The codings for this are already provided by the CodeSystem `voting-process` located in `tutorial-process/src/main/resources/fhir/CodeSystem/voting-process.xml` and included in a ValueSet 
     called `voting-results` located in `tutorial-process/src/main/resources/fhir/ValueSet/voting-results.xml`.
     <details>
     <summary>Don't know how to include the coding?</summary>

     Define the `Task.output.value[x]` element for your Output Parameter slice with a type of `Coding`.
       <details>
       <summary>XML Example</summary>

       ``` xml
            <element id="Task.output:example-output.value[x]">
               <path value="Task.output.value[x]"/>
               <max value="1"/>
               <type>
                   <code value="Coding"/>
               </type>
            </element>
       ```
       </details>

     Define the CodeSystem of the Coding using the URL of the `voting-process` CodeSystem.
       <details>
       <summary>XML Example</summary>

       ``` xml
            <element id="Task.output:example-output.value[x].system">
               <path value="Task.output.value[x]"/>
               <min value="1"/>
               <max value="1"/>
               <fixedUri value="http://tutorial.org/fhir/CodeSystem/voting-process"/>
            </element>
       ```
       </details>
     
     Define the values that are allowed for the Coding by binding it to the `voting-results` ValueSet using a binding strength of `required`.
       <details>
       <summary>XML Example</summary>

       ``` xml
            <element id="Task.output:example-output.value[x].code">
               <path value="Task.output.value[x].code"/>
               <min value="1"/>
               <max value="1"/>
               <binding>
                   <strength value="required"/>
                   <valueSet value="http://tutorial.org/fhir/ValueSet/voting-results"/>
               </binding>
            </element>
       ```
       </details>
    </details>
   
   * It can appear any number of times. Define the cardinality as `0..*`
   * It needs to include information on which instance did the voting. Use the extension `extension-voting-result` found in `tutorial-process/src/main/resources/fhir/StructureDefinition/extension-voting-result.xml`
       <details>
       <summary>Don't know how to include the extension?</summary>
       
       Create a slice for `Task.output:example-output.extension`. Add `Extension` as the value for `Task.output:example-output.extension.type.code` 
       and add the URL of the extension as the value for `Task.output:example-output.extension.type.profile`.
       <details>
       <summary>XML Example</summary>
      
       ``` xml
       <element id="Task.output:example-output.extension:my-extension">
          <path value="Task.output.extension"/>
          <sliceName value="example-output"/>
          <min value="1"/>
          <max value="1"/>
          <type>
             <code value="Extension"/>
             <profile value="http://tutorial.org/fhir/StructureDefinition/my-extension"/>
          </type>
       </element>
       ```
       </details>
       </details>
2. In the next steps, we will create the part of the process where a user has to interact with a [QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) 
   in the DSF FHIR server web UI to answer the question defined in the input parameter of `task-start-voting-process.xml`. 
   Create a [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) in `tutorial-process/src/main/resources/fhir/Questionnaire/user-vote.xml` called `user-vote`. 
   Don't forget to register it in the Process Plugin Definition for the `vote` process.
   <details>
   <summary>Don't know how the Questionnaire should look like?</summary>
    
   Check out the [template](https://dsf.dev/process-development/api-v2/guides/user-tasks-in-the-dsf.html#questionnaire-template) again. Don't forget changing the URL.
   </details>
    
    * Add an item with linkId `binary-question` and type `display`.
    * Add an item with linkId `vote` and type `boolean`.
3. We now have a [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) resource that can be referenced in the BPMN model's [User Tasks](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html).
   If referenced, the DSF will take this [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) as a template to create the [QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html)
   that can be answered by a user in the DSF FHIR server web UI.  
   Add a [User Task](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html) to `vote.bpmn` located in `tutorial-process/src/main/resources/bpe/vote.bpmn`:
   * The [User Task](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html) should be inserted between the [Exclusive Gateway](https://dsf.dev/process-development/api-v2/bpmn/gateways.html) and the `Save User Vote` [Service Task](https://dsf.dev/process-development/api-v2/bpmn/service-tasks.html).
     The connection from the [Exclusive Gateway](https://dsf.dev/process-development/api-v2/bpmn/gateways.html) requires a [Condition](https://dsf.dev/process-development/api-v2/bpmn/conditions.html) element with type `Expression` and `Condition Expression` with value `${userVote}`.
   * The [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) resource is referenced by providing a `Form key` attribute with the value of the [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) URL you created in the previous step appended by the version placeholder `|#{version}`. This option is found under `Forms` and type `Embedded or External Task Forms`.
4. The [QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) that is automatically created will copy its items from the template [Questionnaire](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html).
   This means we need a way to set the `item.text` element of the `binary-question` item you created in the previous step, dynamically. This mechanism is provided by [Task Listeners](https://dsf.dev/process-development/api-v2/dsf/activities.html#usertasklistener).
   Create a [User Task Listener](https://dsf.dev/process-development/api-v2/dsf/activities.html#usertasklistener) in `tutorial-process/src/main/java/dev/dsf/process/tutorial/listener` for the [User Task](https://dsf.dev/process-development/api-v2/bpmn/user-tasks.html) you added in the previous step:
    * The new Java class needs to inherit from `DefaultUserTaskListener`
    * Override `beforeQuestionnaireResponseCreate` and set the text of the [QuestionnaireResponse](https://dsf.dev/process-development/api-v2/fhir/questionnaire-and-questionnaireresponse.html) item with linkId `binary-question` to the value of the 
      Start Task's input parameter with name `binary-question`

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-7
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `tutorialorg_votingProcess` can be executed successfully, we need to deploy them into DSF instances and execute the `tutorialorg_votingProcess`. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.
Again, you may decide to authenticate and start the process via the certificate or the Keycloak user `Tyler Tester` with username `test` and password `test`. You can find the client certificate
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
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_votingProcess`.

3. Start the DSF FHIR server for the `cos.dsf.test` organization in a third console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-fhir
   ```
   Verify the DSF FHIR server started successfully at https://cos/fhir.

4. Start the DSF BPE server for the `cos.dsf.test` organization in a fourth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up cos-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_votingProcess`.


5. Start the DSF FHIR server for the `hrp.dsf.test` organization in a fifth at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-fhir
   ```
   Verify the DSF FHIR server started successfully at https://hrp/fhir.

6. Start the DSF BPE server for the `hrp.dsf.test` organization in a sixth console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up hrp-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `tutorialorg_votingProcess`.

7. Start the `tutorialorg_votingProcess` by posting a specific FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server of the `dic.dsf.test` organization using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure. Make sure to populate the Input Parameters.

   Verify that the FHIR [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource was created at the DSF FHIR server and the `tutorialorg_votingProcess` was executed. To do this, navigate to https://dic/fhir/QuestionnaireResponse?_sort=-_lastUpdated&status=in-progress. There should be a QuestionnaireResponse resource with status `in-progress` based on a Questionnaire resource with URL `http://tutorial.org/fhir/Questionnaire/user-vote`. 
   Click on the QuestionnaireResponse, answer the question and press `Submit`. Navigate to https://dic/fhir/Task?_sort=-_lastUpdated and find the latest Task resource with message-name `startVotingProcess`. It should have a status of `completed`. Clicking on the Task resource redirects to the detailed Task view and three Output Parameters should now be present. Each one describes the voting result of
   an Organization that responded in the `vote` process. The responses from `cos.dsf.test` and `hrp.dsf.test` have a randomly generated response, but they should not have a value of `timeout`. Depending on whether you completed the QuestionnaireResponse in time, the output for `dic.dsf.text` should show your answer or `timeout`.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • **Exercise 7**