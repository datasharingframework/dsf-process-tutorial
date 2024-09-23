[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • **Exercise 1** • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
___
## Disclaimer
The concept of `Tasks` exists in both the FHIR and BPMN domains. For this tutorial `Task resource` always refers
to [FHIR Tasks](https://www.hl7.org/fhir/R4/task.html) and `Service Task` always means the BPMN concept.

# Troubleshooting Tip
Over the course of the exercises you will mostly likely want to take a look at the DSF FHIR server or DSF BPE server logs. 
You can use the logs provided by docker or the debug logs located in `dev-setup/{dsfInstance}/bpe/log` and `dev-setup/{dsfInstance}/fhir/log`.
The DSF FHIR server also has an audit log available in this directory.

# Exercise 1 - Simple Process
The first exercise focuses on setting up the development environment used in this tutorial and shows how to implement and execute a simple
BPMN process. But first, let's make ourselves familiar with the project structure.  
The tutorial project consists of three parts:
1. A `test-data-generator` project used to generate X.509 certificates and FHIR resources
   during the maven build of the project. The certificates and FHIR resources are needed to start DSF instances which simulate
   installations at three different organizations.
2. The DSF instances are created using [Docker](https://www.docker.com/) and configured using
   a `docker-compose.yml` file in the `dev-setup` folder. The docker-compose dev setup uses a single PostgreSQL database server,
   a single nginx reverse proxy, a keycloak instance as well as three separate DSF FHIR server instances and 3 separate DSF BPE server instances.
3. The `tutorial-process` project contains all resources (FHIR resources, BPMN models and Java code) for the actual
   DSF process plugin. Java code for the `tutorial-process` project is located at `src/main/java`, FHIR resources and
   BPMN process models at `src/main/resources` as well as prepared JUnit tests to verify your solution at `src/test/java`.

FHIR resources used in the DSF are formatted as XML. You can find them in the `tutorial-process/src/main/resources/fhir` directory.
When creating your own FHIR resources for DSF process plugins you also want to put them in a fitting subdirectory of `tutorial-process/src/main/resources/fhir`.

We recommend you take a quick glance at all the topics in the `learning` directory to get a 
feel for the scope of this tutorial.  
In order to solve this exercise, you need to have read the topics on [FHIR](../learning/concepts/fhir/info.md), 
[FHIR Task](../learning/concepts/fhir/task.md), 
[The Process Plugin Definition](../learning/concepts/dsf/the-process-plugin-definition.md), 
[Spring Integration](../learning/concepts/dsf/spring-integration.md), [Service Tasks](../learning/concepts/bpmn/service-tasks.md), 
[Service Delegates](../learning/concepts/dsf/service-delegates.md),
[BPMN Process Execution](../learning/concepts/dsf/bpmn-process-execution.md), 
[BPMN Process Variables](../learning/concepts/dsf/bpmn-process-variables.md), 
[Accessing BPMN Process Variables](../learning/guides/accessing-bpmn-process-variables.md),
[About Version, Placeholders and URLs](../learning/concepts/dsf/about-version-placeholders-and-urls.md)
and [Starting a Process via Task Resources](../learning/guides/starting-a-process-via-task-resources.md).

Solutions to this exercise are found on the branch `solutions/exercise-1`.

## Exercise Tasks
1. Set the `DicTask` class as the service implementation of the appropriate service task within the `dic-process.bpmn` process model.
2. Register the `DicTask` class as a prototype bean in the `TutorialConfig` class.
3. Add a log message to the `DicTask#doExecute` method that logs the recipient organization identifier from the start [FHIR Task](../learning/concepts/fhir/task.md) resource.

    <details>
        <summary>Don't know where to get a logger?</summary>
    
    This project uses slf4j. So use `LoggerFactory` to get yourself a logger instance.
    </details>
    
    <details>
        <summary>Can't find a way to get the start task?</summary>
    
    The `doExecute` method provides a `Variables` instance. Try it through this one.
    </details>
    
    <details>
        <summary>Don't know where to look for the identifier?</summary>
    
    Take a look at the official [FHIR Task](../learning/concepts/fhir/task.md) resource, find elements that have a recipient and manoeuvre your way to those elements using the right getters. Then test which of them has the correct value.
    </details>


4. In order to start your process you need to either create a regular [Task](../learning/concepts/fhir/task.md) resource
    or a [Draft Task Resource](../learning/concepts/dsf/draft-task-resources.md). Based on whether you would like
    to use cURL or the DSF FHIR server's web interface for starting processes you can do one of the following
    assignments (although we invite you to do both):
   * Create a [Task](../learning/concepts/fhir/task.md) resource in `tutorial-process/src/main/resources/fhir/example-task.xml` based on the [Task](../learning/concepts/fhir/task.md)
     profile `tutorial-process/src/main/resources/fhir/StructureDefinition/task-start-dic-process.xml`.  
     You will need it to start your process via cURL.

        <details>
        <summary>Don't where to get values for organization identifiers?</summary>

        Take a look at the topic on [organization identifiers](../learning/concepts/dsf/organization-identifiers.md).
        </details>   

        <details>
        <summary>Don't know how to create Task resources?</summary>

        Take a look at [this guide](../learning/guides/creating-task-resources-based-on-a-definition.md).
        </details>
   * Create a [Draft Task Resource](../learning/concepts/dsf/draft-task-resources.md). You will need to be able
    to create [Task](../learning/concepts/fhir/task.md) resources as a prerequisite. If you don't know how to do this, 
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
    To authenticate yourself to the server you can use the client certificate located at `.../dsf-process-tutorial/test-data-generator/cert/dic-client/dic-client_certificate.p12` (Password: `password`). 
    Add the certificate and the generated Root CA located at `.../dsf-process-tutorial/test-data-generator/cert/ca/testca_certificate.pem` to your browser certificate store.
	
	**Caution:** __If you add the generated Root CA to your browsers certificate store as a trusted Root CA, make sure you are 
    the only one with access to the private key at `.../dsf-process-tutorial/test-data-generator/cert/ca/testca_private-key.pem`.__

2. Start the DSF BPE server for the `dic.dsf.test` organization in a second console at location `.../dsf-process-tutorial/dev-setup`:
	```
	docker-compose up dic-bpe
	```
	Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`. 
    The DSF BPE server should print a message that the process was deployed. The DSF FHIR server should now have a new ActivityDefinition resource. Go to `https://dic/fhir/ActivityDefinition` to check if the expected resource was created by the BPE while deploying the process. The returned FHIR Bundle should contain a single ActivityDefinition. Also, go to `https://dic/fhir/StructureDefinition?url=http://dsf.dev/fhir/StructureDefinition/task-start-dic-process` to check if the expected [FHIR Task](../learning/concepts/fhir/task.md) profile was created.

3. Start the `dsfdev_dicProcess` by posting an appropriate [FHIR Task](../learning/concepts/fhir/task.md) resource to the DSF FHIR server using either cURL or the DSF FHIR server's web interface. Check out [Starting A Process Via Task Resources](../learning/guides/starting-a-process-via-task-resources.md) again if you are unsure.  
	
    Verify that the  [FHIR Task](../learning/concepts/fhir/task.md) resource could be created at the DSF FHIR server. Either look at your docker container log for the DIC FHIR server or find your [Task](../learning/concepts/fhir/task) resource in the list of all [Task](../learning/concepts/fhir/task) resources under https://dic/fhir/Task/. 
	
    Verify that the `dsfdev_dicProcess` was executed by the DSF BPE server. The BPE server should print a message showing that the process was started, print the log message you added to the `DicTask` class and end with a message showing that the process finished.

___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • **Exercise 1** • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
