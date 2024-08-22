[Prerequisites](prerequisites.md) • **Exercise 0** • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
___
# Exercise 0 - Setting Up The Project
Before you start working on any exercises, you should verify that you meet the requirements outlined in [prerequisites](prerequisites.md) by successfully building and deploying a plugin.  
For this, you should follow these steps:  
1. Checkout the branch `solutions/exercise-1`
2. Build the project using maven `package`. Make sure you either check the profile for exercise 1 or no profile at all
3. Start the scenario from the `dev-setup` folder:  
   ```shell
      docker-compose up
   ```
4. Add the CA certificate from `test-data-generator/cert/ca/testca_certificate.pem` to your browser's certificate store
5. Add the client certificate from `test-data-generator/cert/Webbrowser_Test_User/Webbrowser_Test_User_certificate.p12` to your browser's certificate store
6. Visit https://dic/fhir. Your browser should prompt you to supply a client certificate. Select the `Webbrowser Test User` certificate. If it doesn't, you need to research how to configure your browser to send the client certificate. If prompted for a password use "password".
7. You should now be logged into the DSF FHIR server frontend
8. Repeat step 6 for https://cos/fhir and https://hrp/fhir
9. Visit https://dic/fhir/Task?_sort=_profile,identifier&status=draft
10. There should be one resource listed. If it is, your setup is complete and working correctly
11. Delete the `Webbrowser_Test_User` certificate from your browser's certificate store
12. You may choose to either keep or delete the CA certificate. A later exercise will require this certificate to be present again
13. Tear down the test scenario:
   ```shell
      docker-compose down
   ```
14. Checkout the branch `main`
15. You may proceed to [exercise 1](exercise-1.md)
___
[Prerequisites](prerequisites.md) • **Exercise 0** • [Exercise 1](exercise-1.md) • [Exercise 1.1](exercise-1-1.md) • [Exercise 2](exercise-2.md) • [Exercise 3](exercise-3.md) • [Exercise 4](exercise-4.md) • [Exercise 5](exercise-5.md) • [Exercise 6](exercise-6.md)
