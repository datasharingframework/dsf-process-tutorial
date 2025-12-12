### Examples for Requester and Recipient Elements

Below you will find a set of examples for each Coding used by `requester` and `recipient` elements from
the [dsf-extension-process-authorization](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml). CodeSystems referenced in the examples can be found [here](https://github.com/datasharingframework/dsf/tree/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem).
Use this collection as a reference point when creating your own [ActivityDefinitions](../../concepts/fhir/activitydefinition.md).

#### Requester
The `requester` element uses one of the following Codings:
```xml
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all-practitioner|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization-practitioner|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role-practitioner|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-all|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-organization|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-parent-organization-role|2.0.0" />
```

##### Local All
```xml
<extension url="requester">
    <valueCoding>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ALL" />
    </valueCoding>
</extension>
```

##### Local All Practitioner
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-practitioner">
            <valueCoding>
                <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                <code value="DSF_ADMIN"/>   <!-- example, replace appropriately -->
            </valueCoding>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ALL_PRACTITIONER" />
    </valueCoding>
</extension>
```

##### Local Organization
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
            <valueIdentifier>
                <system value="http://dsf.dev/sid/organization-identifier"/>
                <value value="My_Organization"/>    <!-- example, replace appropriately -->
            </valueIdentifier>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ORGANIZATION" />
    </valueCoding>
</extension>
```

##### Local Organization Practitioner
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
            <extension url="organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>    <!-- example, replace appropriately -->
                </valueIdentifier>
            </extension>
            <extension url="practitioner-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                    <code value="DSF_ADMIN"/>   <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ORGANIZATION_PRACTITIONER" />
    </valueCoding>
</extension>
```

##### Local Parent Organization Role
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-parent-organization-role">
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Parent_Organization"/>     <!-- example, replace appropriately -->
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>     <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ROLE" />
    </valueCoding>
</extension>
```

##### Local Parent Organization Role Practitioner
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-parent-organization-role-practitioner">
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Parent_Organization"/>     <!-- example, replace appropriately -->
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>     <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
            <extension url="practitioner-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                    <code value="DSF_ADMIN"/>       <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ROLE_PRACTITIONER" />
    </valueCoding>
</extension>
```

##### Remote All
```xml
<extension url="requester">
    <valueCoding>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="REMOTE_ALL" />
    </valueCoding>
</extension>
```

##### Remote Organization
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
            <valueIdentifier>
                <system value="http://dsf.dev/sid/organization-identifier"/>
                <value value="My_Organization"/>    <!-- example, replace appropriately -->
            </valueIdentifier>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="REMOTE_ORGANIZATION" />
    </valueCoding>
</extension>
```

##### Remote Parent Organization Role
```xml
<extension url="requester">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-parent-organization-role">
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Parent_Organization"/>     <!-- example, replace appropriately -->
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>     <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="REMOTE_ROLE" />
    </valueCoding>
</extension>
```

#### Recipient
The `recipeint` element uses one of the following Codings:
```xml
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization|2.0.0" />
<profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role|2.0.0" />
```

##### Local All
```xml
<extension url="recipient">
    <valueCoding>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ALL" />
    </valueCoding>
</extension>
```

##### Local Organization
```xml
<extension url="recipient">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
            <valueIdentifier>
                <system value="http://dsf.dev/sid/organization-identifier"/>
                <value value="My_Organization"/>    <!-- example, replace appropriately -->
            </valueIdentifier>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ORGANIZATION" />
    </valueCoding>
</extension>
```

##### Local Parent Organization Role
```xml
<extension url="recipient">
    <valueCoding>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-parent-organization-role">
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Parent_Organization"/>     <!-- example, replace appropriately -->
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>     <!-- example, replace appropriately -->
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
        <code value="LOCAL_ROLE" />
    </valueCoding>
</extension>
```
