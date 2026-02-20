# DISCLAIMER
In its current state, the tutorial can only be done by checking out the version of the [DSF](https://github.com/datasharingframework/dsf) that is used in the tutorial and running `maven install` because some dependencies are not uploaded to public repositories.


# DSF Process Plugin Tutorial
This repository contains exercises to learn how to implement process plugins for the [Data Sharing Framework (DSF)](https://github.com/datasharingframework/dsf). The main documentation for the DSF can be found under [dsf.dev](https://dsf.dev). The tutorial is divided into several exercises that build on each other. For each exercise, a sample solution is provided in a separate branch. You should work on solving the exercises in your local main branch and checkout the solution branches if necessary.

The first version of this tutorial was executed at the [GMDS / TMF 2022](https://gmds-tmf-2022.de) conference. A recording of the opening remarks by H. Hund and R. Wettstein can be found on [YouTube](https://youtu.be/2AUPwQQQsPY). The slides of the opening remarks are available as a [PDF download](exercises/figures/2022-08-21_GMDS_2022_DSF_Process_Tutorial.pdf). Please note that the version presented was designed for DSF < 1.0.0 and that namespaces and other aspects have changed since then. The following tutorial is designed for the current version (above v2.0.x) of the DSF.

*Special thanks to [Bastian Schaffer](https://github.com/bastianschaffer) for his thorough feedback during development of the tutorial!*

## Prerequisites
In order to be able to solve the exercises described in this tutorial a software development environment with GIT, Java 17, Maven 3.8, Docker, Docker-Compose, a Java IDE like Eclipse or IntelliJ, Camunda Modeler as a BPMN Editor and a minimum of 16GB of RAM is needed. For more details see the [detailed prerequisites document](exercises/prerequisites.md).

## Exercise 0 - Setting Up The Project
Exercise 0 verifies that the project is set up correctly in order to start working on exercises. For more details see the [exercise 0 description](exercises/exercise-0.md).

## Exercise 1 - Simple Process
The first exercise focuses on setting up the testing environment used in this tutorial and shows how to implement and execute a simple BPMN process. For more details see the [exercise 1 description](exercises/exercise-1.md).

## Exercise 1.1 - Process Debugging
Exercise 1.1 looks at how to use the Java debugger of your IDE to remote debug the execution of a process plugin. For more details see the [exercise 1.1 description](exercises/exercise-1-1.md).

## Exercise 2 - Input Parameters
In order to configure processes that are packaged as process plugins, we will take a look at two possibilities on how to pass parameters into a process. For more details see the [exercise 2 description](exercises/exercise-2.md).

## Exercise 3 - DSF User Authentication and Authorization
This exercise looks at how to set authorization rules for DSF users and process plugins. For more details see the [exercise 3 description](exercises/exercise-3.md).

## Exercise 4 - Messaging
Communication between organizations is modeled using message flow events in BPMN processes. The third exercise shows you how a process at one organization can trigger a process at another organization. For more details see the [exercise 4 description](exercises/exercise-5.md).

## Exercise 5 - Exclusive Gateways
Different execution paths in a process based on the state of process variables can be achieved using exclusive gateways. In exercise 5 we will examine how this can be implemented. For more details see the [exercise 5 description](exercises/exercise-6.md).

## Exercise 6 - Event Based Gateways and Intermediate Events
Exercise 6 will look at message flows between three organizations as well as how to continue a waiting process, if no return message arrives. For more details see the [exercise 6 description](exercises/exercise-6.md).

## Exercise 7 - Exercise 7 - User Tasks and Task Output Parameters
Exercise 7 will teach you how to utilize User Tasks to provide user input to a running process and how to save data as Task Output Parameters. For more details see the [exercise 7 description](exercises/exercise-7.md).
