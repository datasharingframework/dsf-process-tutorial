### Read Access Tag

Axiomatically, nobody is allowed to write FHIR resources (except [Task](../../concepts/fhir/task.md)) to the DSF FHIR server
unless it is your own organization. By default, the same applies to reading FHIR resources
(again except [Task](../../concepts/fhir/task.md)). But since the DSF is often used to offer medical data in form of
FHIR resources, you will find yourself wanting other organizations to be allowed to read the resources you are offering.
The `Resource.meta.tag` element is used define access rules for all FHIR resources in the DSF, with the
exception of [Task](../../concepts/fhir/task.md) resources. We will explain the reason for this exception shortly.
For example, allowing read access for all organizations, you would use the following `system` and `code` in your FHIR resource:

```xml
<meta>
   <tag>
      <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
      <code value="ALL" />
   </tag>
</meta>
```
You can find all codes for the Read Access Tag in its [CodeSystem](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-read-access-tag-2.0.0.xml).

The read access rules for [Task](../../concepts/fhir/task.md) resources are defined through the `requester` and `recipient` elements of the  [dsf-extension-process-authorization](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml) in your plugin's
[ActivityDefinitions](../../concepts/fhir/activitydefinition.md). Therefore, no `read-access-tag` is needed.

It is also possible to restrict read access of FHIR resources to organizations with
a specific role in a parent organization or a specific identifier.
If you want to find out more, you may look at the [guide on configuring the Read Access Tag](../../guides/configuring-the-read-access-tag.md).
