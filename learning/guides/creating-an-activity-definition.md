### Creating an ActivityDefinition

This guide will teach you how to create an ActivityDefinition based on the [dsf-activity-definition](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-activity-definition-1.0.0.xml) profile for your process plugin.
It is divided into steps for each of the main components of ActivityDefinitions:
1. Read Access Tag
2. Extension: process authorization
3. BPE Managed Elements
4. Regular Elements

*Regular elements* are all elements not part of the first 3 main components.

#### 1. Read Access Tag
Let us start out with an empty [ActivityDefinition](../concepts/fhir/activitydefinition.md):
```xml
<ActivityDefinition xmlns="http://hl7.org/fhir">
    
</ActivityDefinition>
```

The first element in DSF FHIR resources is always the [Read Access Tag](../concepts/dsf/read-access-tag.md). It describes who is
allowed to read this resource through the DSF FHIR server's REST API. You can learn more complex configurations of the
[Read Access Tag](../concepts/dsf/read-access-tag.md) in [this guide](../concepts/dsf/read-access-tag.md). In this case, we will allow read access to everyone:

```xml
<ActivityDefinition xmlns="http://hl7.org/fhir">
    <meta>
        <tag>
            <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
            <code value="ALL" />
        </tag>
    </meta> 
</ActivityDefinition>
```

#### 2. Extension: Process Authorization
This part of your ActivityDefinition will tell the DSF who is allowed to request and receive messages ([Task](../concepts/fhir/task.md) resources)
for your BPMN process. If your plugin contains more than one BPMN process, you will have to create one [ActivityDefinition](../concepts/fhir/activitydefinition.md)
for each BPMN process. It is important to note that you need to include authorization rules for **ALL** messages received in your BPMN process.
This includes the message starting your BPMN process initially.   

For this example we will add process authorization for the message `myMessage`, which is sent via `my-task`, and allow it to be requested 
by a user with role `DSF_ADMIN` which belongs to a local organization with identifier `myOrganization`:
```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
    <extension url="message-name">
        <valueString value="myMessage"/>
    </extension>
    <extension url="task-profile">
        <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
    </extension>
    <extension url="requester">
        <valueCoding>
            <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
                <extension url="organization">
                    <valueIdentifier>
                        <system value="http://dsf.dev/sid/organization-identifier"/>
                        <value value="My_Organization"/>
                    </valueIdentifier>
                </extension>
                <extension url="practitioner-role">
                    <valueCoding>
                        <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                        <code value="DSF_ADMIN"/>
                    </valueCoding>
                </extension>
            </extension>
            <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
            <code value="LOCAL_ORGANIZATION_PRACTITIONER"/>
        </valueCoding>
    </extension>
    <extension url="recipient">
        <valueCoding>
            <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
            <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
            <code value="LOCAL_ORGANIZATION"/>
        </valueCoding>
    </extension>
</extension>
```
You should put this element beneath the [Read Access Tag](#1-read-access-tag). 
There is a step-by-step guide on [how to create process authorization elements](creating-a-process-authorization-element.md) if you want to learn how this is done in detail.
The challenging part is usually getting the `requester` and `recipient` elements right. For this reason, 
we also provide a [list](../concepts/dsf/examples-for-requester-and-recipient-elements.md) of example `requester` and `recipient` elements where you can replace values as needed.

<details>
<summary>Your ActivityDefinition should now look like this</summary>

```xml
<ActivityDefinition xmlns="http://hl7.org/fhir">
    <meta>
        <tag>
            <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
            <code value="ALL" />
        </tag>
    </meta>
    <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
        <extension url="message-name">
            <valueString value="myMessage"/>
        </extension>
        <extension url="task-profile">
            <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
        </extension>
        <extension url="requester">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
                    <extension url="organization">
                        <valueIdentifier>
                            <system value="http://dsf.dev/sid/organization-identifier"/>
                            <value value="My_Organization"/>
                        </valueIdentifier>
                    </extension>
                    <extension url="practitioner-role">
                        <valueCoding>
                            <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                            <code value="DSF_ADMIN"/>
                        </valueCoding>
                    </extension>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION_PRACTITIONER"/>
            </valueCoding>
        </extension>
        <extension url="recipient">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
                    <valueIdentifier>
                        <system value="http://dsf.dev/sid/organization-identifier"/>
                        <value value="My_Organization"/>
                    </valueIdentifier>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION"/>
            </valueCoding>
        </extension>
    </extension>
</ActivityDefinition>
```
</details>

#### 3. BPE Managed Elements

Some elements of [ActivityDefinitions](../concepts/fhir/activitydefinition.md) are managed by the DSF BPE and replaced with certain values
at appropriate times.

The following elements are managed by the DSF BPE:
- `ActivityDefinition.version` should use the [placeholder](../concepts/dsf/about-version-placeholders-and-urls.md#placeholders) `#{version}`
- `ActivityDefinition.date` is not required, but should you decide to include it, use the [placeholder](../concepts/dsf/about-version-placeholders-and-urls.md#placeholders) `#{date}`
- `ActivityDefinition.status` must have a value of `unknown`

<details>
<summary>Your ActivityDefinition should now look like this</summary>

```xml
<ActivityDefinition xmlns="http://hl7.org/fhir">
    <meta>
        <tag>
            <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
            <code value="ALL" />
        </tag>
    </meta>
    <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
        <extension url="message-name">
            <valueString value="myMessage"/>
        </extension>
        <extension url="task-profile">
            <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
        </extension>
        <extension url="requester">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
                    <extension url="organization">
                        <valueIdentifier>
                            <system value="http://dsf.dev/sid/organization-identifier"/>
                            <value value="My_Organization"/>
                        </valueIdentifier>
                    </extension>
                    <extension url="practitioner-role">
                        <valueCoding>
                            <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                            <code value="DSF_ADMIN"/>
                        </valueCoding>
                    </extension>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION_PRACTITIONER"/>
            </valueCoding>
        </extension>
        <extension url="recipient">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
                    <valueIdentifier>
                        <system value="http://dsf.dev/sid/organization-identifier"/>
                        <value value="My_Organization"/>
                    </valueIdentifier>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION"/>
            </valueCoding>
        </extension>
    </extension>
    <!-- version managed by bpe -->
    <version value="#{version}"/>
    <!-- date managed by bpe -->
    <date value="#{date}"/>
    <!-- status managed by bpe -->
    <status value="unknown"/>
</ActivityDefinition>
```
</details>

#### 4. Regular Elements

The only required elements in this set are `ActivityDefinition.url` and `ActivityDefinition.kind`.
Check out the documentation on [URLs](../concepts/dsf/about-version-placeholders-and-urls.md#urls) on how to choose the correct value for `ActivityDefinition.url`. `ActivityDefinition.kind`
must have the value `Task`.
All other elements can technically be omitted. Still, we recommend you include the following elements:
- `AcitivityDefinition.name`
- `AcitivityDefinition.title`
- `AcitivityDefinition.subtitle`
- `AcitivityDefinition.experimental`
- `AcitivityDefinition.publisher`
- `AcitivityDefinition.contact`
- `AcitivityDefinition.description`

<details>
<summary>Your finished ActivityDefinition should now look something like this</summary>

```xml
<ActivityDefinition xmlns="http://hl7.org/fhir">
    <meta>
        <tag>
            <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
            <code value="ALL" />
        </tag>
    </meta>
    <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
        <extension url="message-name">
            <valueString value="myMessage"/>
        </extension>
        <extension url="task-profile">
            <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
        </extension>
        <extension url="requester">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
                    <extension url="organization">
                        <valueIdentifier>
                            <system value="http://dsf.dev/sid/organization-identifier"/>
                            <value value="My_Organization"/>
                        </valueIdentifier>
                    </extension>
                    <extension url="practitioner-role">
                        <valueCoding>
                            <system value="http://dsf.dev/fhir/CodeSystem/practitioner-role"/>
                            <code value="DSF_ADMIN"/>
                        </valueCoding>
                    </extension>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION_PRACTITIONER"/>
            </valueCoding>
        </extension>
        <extension url="recipient">
            <valueCoding>
                <extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization">
                    <valueIdentifier>
                        <system value="http://dsf.dev/sid/organization-identifier"/>
                        <value value="My_Organization"/>
                    </valueIdentifier>
                </extension>
                <system value="http://dsf.dev/fhir/CodeSystem/process-authorization"/>
                <code value="LOCAL_ORGANIZATION"/>
            </valueCoding>
        </extension>
    </extension>
    <!-- version managed by bpe -->
    <version value="#{version}"/>
    <!-- date managed by bpe -->
    <date value="#{date}"/>
    <!-- status managed by bpe -->
    <status value="unknown"/>
    <url value="http://dsf.dev/bpe/Process/myProcess"/>
    <kind value="Task"/>
    <name value="My Process"/>
    <title value="My Title For My Process"/>
    <subtitle value="Information Processing Process"/>
    <experimental value="false"/>
    <publisher value="DSF"/>
    <contact>
        <name value="DSF"/>
        <telecom>
            <system value="email"/>
            <value value="noreply@dsf.dev"/>
        </telecom>
    </contact>
    <description value="My Process processes information"/>
</ActivityDefinition>
```
</details>