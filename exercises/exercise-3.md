[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 3 - DSF User Role Configuration

In this exercise you will learn how to control **who is allowed to start a process** in the DSF. You will configure authorization rules in an `ActivityDefinition` file and add a Keycloak-based user alongside the existing certificate-based access.

The file you will work in is `tutorial-process/src/main/resources/fhir/ActivityDefinition/dic-process.xml`.

<details>
<summary>Background reading (documentation links for this exercise)</summary>

- [Access Control](https://dsf.dev/operations/latest/fhir/access-control.html)
- [ActivityDefinitions](https://dsf.dev/process-development/api-v2/fhir/activitydefinition.html)
- [Requester and Recipient](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html)
- [Guide: Creating ActivityDefinitions](https://dsf.dev/process-development/api-v2/guides/creating-activity-definitions.html)
</details>

A [Keycloak](https://www.keycloak.org/) instance is already running as part of the dev setup. A user has been created for you in the `DIC` realm. The Keycloak admin console is accessible at https://keycloak:8443 (`username: admin`, `password: admin`). Your task is to allow this user to start the `dicProcess`. Optionally you can also add Keycloak users for the `COS` and `HRP` instances.

## Exercise Tasks

1. Change the `requester` element in the ActivityDefinition `tutorial-process/src/main/resources/fhir/ActivityDefinition/dic-process.xml` to allow all local clients with a practitioner role of `DSF_ADMIN` to request `dicProcess` messages. There is a documentation page to help you [understand the process authorization extension](https://dsf.dev/process-development/api-v2/dsf/understanding-the-process-authorization-extension.html).

   <details>
   <summary>Need a ready-made example?</summary>

   There is a list of examples for the `requester` element [here](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html).
   You can also check out the [guide on creating ActivityDefinitions](https://dsf.dev/process-development/api-v2/guides/creating-activity-definitions.html).
   </details>

2. We just made it so you will not be able to start the `dicProcess` using the client certificate used in earlier exercises.
   Add a **second** `<extension url="requester">` entry to the same authorization block in `dic-process.xml` which allows local clients from the `dic.dsf.test` organization to request `dicProcess` messages, in case you still want to use the client certificate to start the process.

    You need the `LOCAL_ORGANIZATION` code combined with the `extension-process-authorization-organization` nested extension pointing to `dic.dsf.test`.

   <details>
   <summary>Need a ready-made example?</summary>

   There is a list of examples for the `requester` element [here](https://dsf.dev/process-development/api-v2/dsf/requester-and-recipient.html).
   You can also check out the [guide on creating ActivityDefinitions](https://dsf.dev/process-development/api-v2/guides/creating-activity-definitions.html).
   </details>

3. Just like in [exercise 2](exercise-2.md), we just changed a FHIR resource in a way that breaks compatibility with older versions of the plugin. Therefore, we need to signal this change by incrementing the resource version to `1.2`.

   

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-3
```

Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `exampleorg_dicProcess` can be executed successfully, we need to deploy it into a DSF instance and execute the process. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.

1. Start the DSF FHIR server for the `dic.dsf.test` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully at https://dic/fhir.

2. Start the DSF BPE server for the `dic.dsf.test` organization in second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `exampleorg_dicProcess`.

3. Visit https://dic/fhir. First, use the client certificate to log into the DSF FHIR server and make sure you are 
   still able to start a `exampleorg_dicProcess` via the [web interface](https://dsf.dev/process-development/api-v2/guides/starting-a-process-via-task-resources.html#using-the-dsf-fhir-servers-web-interface).
4. Now try doing it again, but this time use Keycloak to log in. Your username and password are both `tutorial`. Also, you might have to clear your browser's
   SSL state because it keeps using the client certificate from before. Afterward, you can visit https://dic/fhir again but refuse to send a 
   client certificate when asked. This should forward you to the Keycloak login page.

If all went well, you should have been able to start the process via both the client certificate and the Keycloak user.
___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)