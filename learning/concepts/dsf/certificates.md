### Certificates

There is a number of certificates that need to be generated in order for DSF instances to communicate with each other securely.
You can find a comprehensive lists of certificates needed by the [DSF FHIR](https://dsf.dev/stable/maintain/fhir/configuration.html)
and [DSF BPE](https://dsf.dev/stable/maintain/bpe/configuration.html) servers on the DSF website.  
Certificates will be created by the `dsf-maven-plugin` by the time of the `package` phase in your process plugin build.
You can also invoke the generation of certificates separately by running `mvn dsf:generate-dev-setup-cert-files`.   
Since this tutorial comes with three preconfigured DSF instances, the only time you will need to interact with certificates
is when you want to make requests to the DSF FHIR server. Either for access to the web frontend under https://instance-host-name/fhir/,
or when [starting your process plugin](../../guides/starting-a-process-via-task-resources.md).  
In case of the web frontend, you will need to add the CA certificate and client certificate of the DSF instance you want to access to your browser.
Certificates can be found in `browser-certs`.

**Example:**  
You want to access the `dic` DSF FHIR server. You add the CA certificate located in `browser-certs/root-ca.crt` to your
browser's certificate store. You also add the client certificate for `dic` located in `browser-certs/dic/dic-client.p12`
to your browser's client certificates.

**Important: Passwords for .p12 files are always "password"**
