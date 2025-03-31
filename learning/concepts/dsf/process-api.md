### DSF Process API Package

The [DSF Process API package](https://mvnrepository.com/artifact/dev.dsf/dsf-bpe-process-api-v1) consists of a set of utility classes designed to provide easy access to solutions for process plugin use cases.
This includes for example the `Variables` class, which provides access to the [BPMN process variables](../../concepts/dsf/bpmn-process-variables.md).

#### Process Plugin Api
When creating [Service Delegates](../../concepts/dsf/service-delegates.md) or [Message Delegates](../../concepts/dsf/message-delegates.md) you will
notice that you need to provide a constructor which expects a `ProcessPluginApi` object and forward it to the superclasses' constructor.
This API instance provides a variety of utility classes:
- `FhirClientProvider`**:** Provides access to a generic, configurable FHIR web client. Used for connections to FHIR servers that are not the DSF FHIR server.
- `DsfClientProvider`**:** Provides access to preconfigured FHIR web client to access DSF FHIR server including utility methods.
- `TaskHelper`**:** Provides utility methods to interact with Task resource. Namely, Input and Output Parameters.
- `FhirContext`**:** Provides access to the FHIR context.
- `EndpointProvider`**:** Provides utility methods to interact with Endpoint resources.
- `MailService`**:** Provides methods to use the DSF's e-mail functionality.
- `ObjectMapper`**:** Provides access to an ObjectMapper instance to perform e.g. JSON-serialization
- `OrganizationProvider`**:** Provides utility methods to interact with Organization resources.
- `OidcClientProvider`**:** Provides utility methods for OIDC functionality
- `ProcessAuthorizationHelper`**:** Provides utility methods to interact with process authorization in [ActivityDefinitions](../fhir/activitydefinition.md)
- `ProxyConfig`**:** Allows you to retrieve information about the DSF proxy
- `QuestionnaireResponseHelper`**:** Provides utility methods to interact with [QuestionnaireResponse](../fhir/questionnaire-and-questionnaireresponse.md) resources
- `ReadAccessHelper`**:** Provides utility methods to modify a resource's [read access tag](read-access-tag.md)