[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
___

# Exercise 3 - DSF User Role Configuration

In [Exercise 1](exercise-1.md), you added a client certificate to your browser in order to be allowed to access the DIC FHIR
server. In later exercises we will also use some of the other DSF installations like the cos.dsf.test or hrp.dsf.test. You could add a 
client certificate to your browser for each one, or you could configure yourself a specific DSF user
with access to all DSF installations.  
This is part of the DSF's access control using the role configuration mechanism. It allows you to specify
exact rules for accessing the FHIR REST API and starting processes for certain users. Either by providing
thumbprints of their client certificates or by using [OpenID Connect](https://openid.net/developers/how-connect-works/).  
For this exercise, we will include [OpenID Connect](https://openid.net/developers/how-connect-works/) in the configuration through a [Keycloak](https://www.keycloak.org/) instance. We have already created a user for you in the `DIC` realm who has sufficient
access to the FHIR REST API and who is allowed to start our `dicProcess`.  
The administration console for Keycloak is accessible under https://keycloak:8443.
Credentials for administrator access are `username: admin` and `password: admin`.  
Your task will be to take this user and explicitly allow them to start the `dicProcess`. Optionally, you can also add
Keycloak users for the `COS` and `HRP` instances.

In order to solve this exercise, you need to have read the documentation on [Access Control](https://dsf.dev/stable/maintain/fhir/access-control.html) 
and [ActivityDefinitions](../learning/concepts/fhir/activitydefinition.md).

## Exercise Tasks

1. Add a new role to the `DEV_DSF_FHIR_SERVER_ROLECONFIG` for the DIC FHIR server instance in [docker-compose.yml](../dev-setup/docker-compose.yml). It should match any user with `token-role` equal to
   `tutorial` and have `dsf-roles` `CREATE`, `READ`, `UPDATE`, `DELETE`, `SEARCH` and `HISTORY`. Finally, the role should also have the practitioner role `DSF_ADMIN`.
2. Change the `requester` element in the ActivityDefinition `dic-process.xml` to allow all local clients with a practitioner role of `DSF_ADMIN` to request `dicProcess` messages.
   <details>
   <summary>Don't know how to change the ActivityDefinition?</summary>

   There is a list of examples for the `requester` element [here](../learning/concepts/dsf/examples-for-requester-and-recipient-elements.md).
   You can also check out the [guide on creating ActivityDefinitions](../learning/guides/creating-an-activity-definition.md).
   </details>

3. We just made it so you will not be able to start the `dicProcess` using the client certificate utilized in earlier exercises.
   Add another `requester` to the ActivityDefinition `dic-process.xml` which allows local clients from the `dic.dsf.test` organization to request `dicProcess` messages,
   in case you still want to use the client certificate to start the process.
   <details>
   <summary>Don't know how to change the ActivityDefinition?</summary>

   There is a list of examples for the `requester` element [here](../learning/concepts/dsf/examples-for-requester-and-recipient-elements).
   You can also check out the [guide on creating ActivityDefinitions](../learning/guides/creating-an-activity-definition.md).
   </details>

4. Just like in [exercise 2](exercise-2.md), we just changed a FHIR resource in a way that breaks compatibility with older versions of the plugin. Therefore, we need to signal this change by incrementing the resource version to `1.2`.

   

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-3
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

3. Visit https://dic/fhir. First, use the client certificate to log into the DSF FHIR server and make sure you are 
   still able to start a `dsfdev_dicProcess` via the [web interface](../learning/guides/starting-a-process-via-task-resources.md#using-the-dsf-fhir-servers-web-interface).
4. Now try doing it again, but this time use Keycloak to log in. Your username and password are both `tutorial`. Also, you might have to clear your browser's
   SSL state because it keeps using the client certificate from before. Afterward, you can visit https://dic/fhir again but refuse to send a 
   client certificate when asked. This should forward you to the Keycloak login page.

If all went well, you should have been able to start the process via both the client certificate and your Keycloak user.
___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md) • [Exercise 7](exercise-7.md)
