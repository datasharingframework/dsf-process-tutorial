[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • **Exercise 1** • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___
## Disclaimer
The concept of `Tasks` exists in both the FHIR and BPMN domains. For this tutorial `Task resource` always refers
to [FHIR Tasks](https://www.hl7.org/fhir/R4/task.html) and `Service Task` always means the BPMN concept.

# Troubleshooting Tip
Over the course of the exercises you should remember to take a look at the DSF FHIR server or DSF BPE server logs. 
You can use the logs provided by docker or the debug logs located in `dev-setup/{dsfInstance}/bpe/log` and `dev-setup/{dsfInstance}/fhir/log`.
The DSF FHIR server also has an audit log available in this directory.

# Tutorial Introduction
The tutorial project consists of three major parts:
1. A preconfigured installation of three DSF instances each part of their own organization. The setup can be found in the `dev-setup` directory and is using [Docker](https://www.docker.com/).
2. A `browser-certs` directory containing all [certificates](https://dsf.dev/explore/concepts/security.html#authentication) that are required during the tutorial.  
3. The tutorial process plugin and its resources under `tutorial-process/src`. Resources include FHIR resources and BPMN files.

The tutorial will sometimes instruct using certain names for things like file names or variables. This is done to more easily write the tests used to verify your solution.
If tests fail, make sure everything is named as expected by the test. Appropriate Maven profiles are activated in Maven commands for each exercise in its [Solution Verification](#solution-verification)
section. Solutions show ONE way that definitely works and is usually considered best practice. Feel free to turn off verification and experiment!

FHIR resources used in the DSF are formatted as XML. You can find them in the `tutorial-process/src/main/resources/fhir` directory.
When creating your own FHIR resources for DSF process plugins you also want to put them in a fitting subdirectory of `tutorial-process/src/main/resources/fhir`.

# Exercise 1 - Simple Process
In this exercise you will add functionality to a service task in the already existing process called `dsfdev_dicProcess` and learn how to start
processes in the DSF. The BPMN model for the `dsfdev_dicProcess` is located in `tutorial-process/src/main/resources/bpe`. 

Documentation topics related to this exercise are [FHIR Task](https://dsf.dev/process-development/api-v2/fhir/task.html),
[The Process Plugin Definition](https://dsf.dev/process-development/api-v2/dsf/process-plugin-definition.html),
[Spring Integration](https://dsf.dev/process-development/api-v2/dsf/spring-framework-integration.html),
[Activities](https://dsf.dev/process-development/api-v2/dsf/activities.html),
[BPMN Process Execution](https://dsf.dev/process-development/api-v2/dsf/bpmn-process-execution.html),
[BPMN Process Variables](https://dsf.dev/process-development/api-v2/dsf/bpmn-process-variables.html),
[Accessing BPMN Process Variables](https://dsf.dev/process-development/api-v2/guides/accessing-bpmn-process-variables.html),
[Versions, Placeholders and URLs](https://dsf.dev/process-development/api-v2/dsf/versions-placeholders-urls.html)
and [Starting a Process via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html).


Solutions to this exercise are found on the branch `solutions/exercise-1`.

## Exercise Tasks
1. Set the `DicTask` class as the service implementation of the appropriate service task within the `dic-process.bpmn` process model.
2. Register the `DicTask` class as a prototype bean in the `TutorialConfig` class.
    <details>
    <summary>Don't know how to register prototype beans?</summary>
    
    Take a look at the documentation on [Spring Integration](https://dsf.dev/process-development/api-v2/dsf/spring-framework-integration.html).
    </details>

3. Add a log message to the `DicTask#execute` method that logs the recipient organization identifier from the start [FHIR Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource.

    <details>
        <summary>Don't know where to get a logger?</summary>
    
    This project uses slf4j. So use `LoggerFactory` to get yourself a logger instance.
    </details>
    
    <details>
        <summary>Can't find a way to get the start task?</summary>
    
    The `execute` method provides a `Variables` instance. Try it through this one.
    </details>
    
    <details>
        <summary>Don't know where to look for the identifier?</summary>
    
    Take a look at the official [FHIR Task](https://hl7.org/fhir/R4/task.html) resource, find elements that have a recipient and maneuver your way to those elements using the right getters. Then test which of them has the correct value.
    </details>

4. In order to start your process you need to either create a regular [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource
    or a [Draft Task Resource](https://dsf.dev/process-development/api-v2/dsf/draft-task-resources.html). Based on whether you would like
    to use cURL or the DSF FHIR server's web interface for starting processes you can do one of the following
    assignments (although we invite you to do both):
   * Create a [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource in `tutorial-process/src/main/resources/fhir/example-task.xml` based on the [Task](https://dsf.dev/process-development/api-v2/fhir/task.html)
     profile `tutorial-process/src/main/resources/fhir/StructureDefinition/task-start-dic-process.xml`.  
     You will need it to start your process via cURL.

        <details>
        <summary>Don't where to get values for organization identifiers?</summary>

        Take a look at the topic on [organization identifiers](https://dsf.dev/process-development/api-v2/dsf/organization-identifiers.html).
        </details>   

        <details>
        <summary>Don't know how to create Task resources?</summary>

        Take a look at the guide for [creating Task resources based on a definition](https://dsf.dev/process-development/api-v2/guides/creating-task-resources-based-on-a-definition.html)
        </details>
   * Create a [Draft Task Resource](https://dsf.dev/process-development/api-v2/dsf/draft-task-resources.html). You will need to be able
    to create [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resources as a prerequisite. If you don't know how to do this, 
    we recommend checking out the cURL method first and revisiting this assignment after that.

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:
```
mvn clean install -Pexercise-1
```
Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `dsfdev_dicProcess` can be executed successfully, we need to deploy it into a DSF instance and execute the process. The maven `install` build is configured to create a process jar file with all necessary resources and to copy the jar to the appropriate locations of the docker dev setup.

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
	```
	docker-compose up dic-fhir
	```
	Verify the DSF FHIR server started successfully at https://dic/fhir. 
	The DSF FHIR server uses a server certificate that was generated during the first maven install build. 
    To authenticate yourself to the server you can use the client certificate located at `.../dsf-process-tutorial/browser-certs/dic/dic-client.p12` (Password: `password`). 
    Add the certificate and the generated Root CA located at `.../dsf-process-tutorial/browser-certs/root-ca.crt` to your browser certificate store.
	
	**Caution:** __If you add the generated Root CA to your browsers certificate store as a trusted Root CA, make sure you are 
    the only one with access to the private key at `.../dsf-process-tutorial/cert/DSF_Dev_Root_CA.key`.__

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console in the `dev-setup` directory:
	```
	docker-compose up dic-bpe
	```
	Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`. 
    The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new ActivityDefinition resource. Go to `https://dic/fhir/ActivityDefinition` to check if the expected resource was created by the BPE while deploying the process. The returned FHIR Bundle should contain a single ActivityDefinition. Also, go to `https://dic/fhir/StructureDefinition?url=http://dsf.dev/fhir/StructureDefinition/task-start-dic-process` to check if the expected [FHIR Task](https://dsf.dev/process-development/api-v2/fhir/task.html) profile was created.

3. Start the `dsfdev_dicProcess` by posting an appropriate [FHIR Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource to the DSF FHIR server using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html) again if you are unsure.  
	
    Verify that the  [FHIR Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource could be created at the DSF FHIR server. Either look at your docker container log for the DIC FHIR server or find your [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resource in the list of all [Task](https://dsf.dev/process-development/api-v2/fhir/task.html) resources under https://dic/fhir/Task/. 
	
    Verify that the `dsfdev_dicProcess` was executed by the DSF BPE server. The BPE server should print a message showing that the process was started, print the log message you added to the `DicTask` class and end with a message showing that the process finished.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • **Exercise 1** • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
