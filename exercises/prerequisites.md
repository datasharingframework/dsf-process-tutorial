**Prerequisites** • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
___

# Prerequisites
In order to be able to solve the exercises described in this tutorial a software development environment with GIT, Java 17, Maven 3.8, Docker, docker-compose, a Java IDE like Eclipse or IntelliJ, a BPMN Editor like the Camunda Modeler and a minimum 16GB of RAM is needed.
Make sure you read through the introductory documentation pages located [here](https://dsf.dev/intro/).
You also want to have some amount of experience in [HL7 FHIR R4](https://www.hl7.org/fhir/R4/).

## GIT
[git](https://git-scm.com) is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency.

- An installation guide for Linux, Mac and Windows can be found here: [installation guide](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
- The most basic git CLI commands are described here: [commands](https://git-scm.com/book/en/v2/Git-Basics-Getting-a-Git-Repository)

## Java 17
Process plugins for the DSF are written using the [Java](https://www.java.com) programming language in version 17. Various open source releases of the Java Developer Kit (JDK) 17 exist, you are free in your choice.

## Maven 3.8
When implementing DSF processes, we use Maven 3.8 to manage the software project's build, reporting and documentation workflow.

- An installation guide for Maven 3.8 can be found here: [installation guide](https://maven.apache.org/install.html)
- The most important maven commands are described here: [commands](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

You can also find all DSF Maven artifacts [here](https://mvnrepository.com/artifact/dev.dsf).

## Docker and docker-compose
To be able to test the implemented processes, we use a dev-setup based on Docker and docker-compose. This allows us to simulate multiple organizations with different roles and run the processes across "organizational boundaries".

- An installation guide for Docker and docker-compose can be found here: [installation guide](https://docs.docker.com/get-docker/)
- The most important Docker commands are described here: [Docker commands](https://docs.docker.com/engine/reference/run/)
- An overview of docker-compose commands are described here: [docker-compose commands](https://docs.docker.com/compose/reference/)

### Host entries for dev-setup
The following entries are required in the `hosts` file of your computer so that the FHIR servers of the simulated organizations can be accessed in your web browser. On Linux and Mac this file is located at `/etc/hosts`. On Windows you can find it at `C:\Windows\System32\drivers\etc\hosts`.

```
127.0.0.1	dic
127.0.0.1	cos
127.0.0.1	hrp
127.0.0.1   keycloak
```

## Java IDE
For the development of the processes we recommend the use of an IDE, e.g. Eclipse or IntelliJ:

- An installation guide for Eclipse can be found here: [Eclipse installation guide](https://wiki.eclipse.org/Eclipse/Installation)
- An installation guide for IntelliJ can be found here: [IntelliJ installation guide](https://www.jetbrains.com/help/idea/installation-guide.html)

## BPMN Editor
To simplify modeling of BPMN processes, we recommend a graphical editor, e.g. the Camunda Modeler:

- An installation guide for the Camunda Modeler can be found here: [installation guide](https://camunda.com/download/modeler/)  
     - **CAVE:** When creating models with Camunda Modeler, make sure you create a **Camunda 7 model**, not a Camunda 8 model. The DSF
relies on Camunda 7 which is incompatible with Camunda 8.

## Hardware
The minimum hardware requirements to run all simulated organizations as part of the Docker dev-setup is 16 GB of RAM.

___
**Prerequisites** • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
