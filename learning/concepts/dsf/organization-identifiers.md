### Organization Identifiers
DSF FHIR server instances always have something called an `organization identifer`. It uniquely identifies
the organization the DSF FHIR server instance belongs to for its [Allow-List mechanism](https://dsf.dev/intro/info/allowList.html). 
It is configured as an [environment variable](https://dsf.dev/stable/maintain/fhir/configuration.html#dev-dsf-fhir-server-organization-identifier-value).  
You can make a GET request to `https://domain/fhir/Organization` to get a list of all organizations for the DSF FHIR server
instance running under `domain`. The results will also include the `organization identifier` of each organization.  

#### Organization Identifiers in Task Resources
[Task](../../concepts/fhir/task.md) resources require you to reference an organization via its identifier as 
the `Task.requester` and `Task.restriction.recipient` elements. The exact values for these elements 
depend on the [ActivityDefinition](../../concepts/fhir/activitydefinition.md) the [Task](../../concepts/fhir/task.md) resource
should conform to.  
As a general rule, you will want to put the identifier of your own organization as the `Task.requester` and `Task.restriction.recipient` elements
for [Task](../../concepts/fhir/task.md) resources which initially start processes. All other cases depend on the context of
the message being sent during process execution. For example, sending a message to another organization would require that organization's identifier to be in the `Task.restriction.recipient` element. 