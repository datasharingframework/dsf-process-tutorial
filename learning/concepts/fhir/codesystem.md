### CodeSystem

[CodeSystems](https://www.hl7.org/fhir/R4/codesystem.html) usually represent a set of concepts which
can be assigned to a code (think LOINC). If you want to use a Code in a resource, you will usually include them in a
[ValueSet](../../concepts/fhir/valueset.md).

Plugin development for the DSF requires the use of [CodeSystems](https://www.hl7.org/fhir/R4/codesystem.html) in two major ways:
1. Using existing [DSF CodeSystems](https://github.com/datasharingframework/dsf/tree/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem) in other FHIR resources like the [dsf-extension-process-authorization](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml).
2. Creating your own CodeSystem to add additional [Input Parameters](../../concepts/fhir/task.md#task-input-parameters) to your [Task](../../concepts/fhir/task.md) profiles.