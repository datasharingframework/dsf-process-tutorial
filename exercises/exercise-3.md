[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
___

# Exercise 3 - DSF User Role Configuration

In [Exercise 1](exercise-1.md), you added a client certificate to your browser in order to be allowed to access the DIC FHIR
server. In later exercises we will also use some of the other DSF installations like the Test_COS or Test_HRP. You could add a 
client certificate to your browser for each one, or you could configure yourself a specific DSF user
with access to all DSF installations.  
This is part of the DSF's access control using the role configuration mechanism. It allows you to specify
exact rules for accessing the FHIR REST API and starting processes for certain users. Either by providing
thumbprints of their client certificates or by using [OpenID Connect](https://openid.net/developers/how-connect-works/).  
For this exercise, we will use OpenID Connect claims to create ourselves a user with sufficient
access to the FHIR REST API and who is allowed to start our `dicProcess`.  
The tutorial project provides a Keycloak instance for this purpose with the administration console accessible under https://keycloak:8443.
Credentials for administrator access are `username: admin` and `password: admin`.

In order to solve this exercise, you need to have read the documentation on [Access Control](https://dsf.dev/stable/maintain/fhir/access-control.html) 
and [ActivityDefinitions](../learning/concepts/fhir/activitydefinition).

## Exercise Tasks

1. In the Keycloak administrator console, create a new realm role called `tutorial` in the `cos`, `dic` and `hrp` realms.
   <details>
   <summary>Don't know how to access realms?</summary>
   
   Use the dropdown in the top left corner:  
   ![Keycloak realm dropdown](figures/keycloak_realm_dropdown.png)
   </details>
   
2. In the Keycloak administrator console, create a new user in the `cos`, `dic` and `hrp` realms with the new `tutorial` role. 
   This will be your credentials to access all DSF FHIR server instances. Make sure you set a **non-temporary** password in the `Credentials` tab.
3. Add a new role to the `DEV_DSF_FHIR_SERVER_ROLECONFIG` for all FHIR server instances in [docker-compose.yml](../dev-setup/docker-compose.yml). It should match any user with `token-role` equal to
   `tutorial` and have `dsf-roles` `CREATE`, `READ`, `UPDATE`, `DELETE`, `SEARCH` and `HISTORY`. Finally, the role should also have the practitioner role `DSF_ADMIN`.
4. Change the `requester` element in the ActivityDefinition `dic-process.xml` to allow all local clients with a practitioner role of `DSF_ADMIN` to request `dicProcess` messages.
   <details>
   <summary>Don't know how to change the ActivityDefinition?</summary>

   There is a list of examples for the `requester` element [here](../learning/concepts/dsf/examples-for-requester-and-recipient-elements).
   You can also check out the [guide on creating ActivityDefinitions](../learning/guides/creating-an-activity-definition.md).
   </details>

5. We just made it so you will not be able to start the `dicProcess` using the client certificate utilized in earlier exercises.
   Add another `requester` to the ActivityDefinition `dic-process.xml` which allows local clients from the `Test_DIC` organization to request `dicProcess` messages,
   in case you still want to use the client certificate to start the process.
   <details>
   <summary>Don't know how to change the ActivityDefinition?</summary>

   There is a list of examples for the `requester` element [here](../learning/concepts/dsf/examples-for-requester-and-recipient-elements).
   You can also check out the [guide on creating ActivityDefinitions](../learning/guides/creating-an-activity-definition.md).
   </details>

   

## Solution Verification
### Maven Build and Automated Tests
Execute a maven build of the `dsf-process-tutorial` parent module via:

```
mvn clean install -Pexercise-3
```

Verify that the build was successful and no test failures occurred.

### Process Execution and Manual Tests
To verify the `dsfdev_dicProcess` can be executed successfully, we need to deploy it into a DSF instance and execute the process. The maven `install` build is configured to create a process jar file with all necessary resources and copy the jar to the appropriate locations of the docker dev setup.

1. Start the DSF FHIR server for the `Test_DIC` organization in a console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-fhir
   ```
   Verify the DSF FHIR server started successfully.

2. Start the DSF BPE server for the `Test_DIC` organization in second console at location `.../dsf-process-tutorial/dev-setup`:
   ```
   docker-compose up dic-bpe
   ```
   Verify the DSF BPE server started successfully and deployed the `dsfdev_dicProcess`.

3. Visit https://dic/fhir. First, use the client certificate to log into the DSF FHIR server and make sure you are 
   still able to start a `dsfdev_dicProcess` via the [web interface](../learning/guides/starting-a-process-via-task-resources.md#using-the-dsf-fhir-servers-web-interface).
4. Now try doing it again, but this time use the user you created earlier. For this, you might have to clear your browser's
   SSL state because it keeps using the client certificate from before. Afterward, you can visit https://dic/fhir again but refuse to send a 
   client certificate when asked. This should forward you to the Keycloak login page.

If all went well, you should have been able to start the process via both the client certificate and your Keycloak user.
___
[Prerequisites](prerequisites.md) • [Exercise 0](exercise-0.md) • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • **Exercise 3** • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)