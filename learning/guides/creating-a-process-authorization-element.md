### Creating a Process Authorization Element

This guide aims to explain how to create a process authorization element for [ActivityDefinitions](../concepts/fhir/activitydefinition.md).
The extension defining the process authorization element can be found [here](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml).
Let us start by creating the [extension element](http://hl7.org/fhir/R4/extensibility.html#extension) with the correct URL. You can get the
value for the URL from the `Extension.url` element:
```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
 
</extension>
```
*Elements not relevant to the current component are hidden with ... to increase readability.*

The [differential](https://www.hl7.org/fhir/R4/profiling.html#snapshot) statement
starts by defining the [slicing](https://www.hl7.org/fhir/R4/profiling.html#snapshot) for the `Extension.extension` element:

```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
    <differential> 
        <element id="Extension">
            <path value="Extension" />
            <min value="1" />
        </element>
        <element id="Extension.extension">
            <path value="Extension.extension" />
            <slicing>
                <discriminator>
                    <type value="value" />
                    <path value="url" />
                </discriminator>
                <rules value="open" />
            </slicing>
        </element>
     ...
    </differential>
</StructureDefinition>
```

The above states that whenever this extension is used in a profile, the profile needs to include this extension at least once (`<min value="1" />`).
The [slicing](https://www.hl7.org/fhir/R4/profiling.html#snapshot) on `Extension.extension` tells us that elements of this [slicing](https://www.hl7.org/fhir/R4/profiling.html#snapshot)
are identified by the value of their URL (`<discriminator>`), which is always the case for extensions, and that other extensions can be added to the [slicing](https://www.hl7.org/fhir/R4/profiling.html#snapshot) (`<rules value="open" />`).
Since there is a [slicing](https://www.hl7.org/fhir/R4/profiling.html#snapshot) on `Extension.extension`, we are dealing with a nested extension.

After these initial element definitions come the elements relevant for your process plugin. The first one is the `message-name` slice:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
    <differential> 
     ...
        <element id="Extension.extension:message-name">
            <path value="Extension.extension" />
            <sliceName value="message-name" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:message-name.url">
            <path value="Extension.extension.url" />
            <fixedUri value="message-name" />
        </element>
        <element id="Extension.extension:message-name.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="string" />
            </type>
        </element>
     ...
    </differential>
</StructureDefinition>
```

This section tells us that we need to include exactly one extension element from the `message-name` slice.
The extension element will have a URL value of `message-name`. If you remember the `discriminator` configuration, this URL value identifies the element to belong to the `message-name` slice on `Extension.extension`.
Lastly, the extension element includes a `valueString` element. In case you are wondering how `value[x]` turned into `valueString`,
FHIR does not allow using `value[x]` as actual element. The value in `value[x]` is always strictly bound to some kind of type.
FHIR uses the `value[x].type.code` value to determine this type and replaces `[x]` with an uppercase version of `element.type.code`.  
This results in the following extension element we will add to our process authorization element:
```xml
<extension url="message-name">
    <valueString value="myMessage"/>
</extension>
```

For your use case, you have to replace `myMessage` with the `messageName` field injection value of the [BPMN message event](../concepts/bpmn/messaging.md) that is expecting this message.

<details>
<summary>This is how your element should look like so far</summary>

```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
    <extension url="message-name">
        <valueString value="myMessage"/>
    </extension>
</extension>
```
</details>

The next slice is called `task-profile`:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
    <differential> 
     ...
        <element id="Extension.extension:task-profile">
            <path value="Extension.extension" />
            <sliceName value="task-profile" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:task-profile.url">
            <path value="Extension.extension.url" />
            <fixedUri value="task-profile" />
        </element>
        <element id="Extension.extension:task-profile.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="canonical" />
            </type>
        </element>
     ...
    </differential>
</StructureDefinition>
```

This section has almost the same structure as `message-name`. The only difference is the value for `value[x].type.code`.
This means that instead of `valueString`, we will have to use a `valueCanonical` element for `task-profile.value[x]`.
Canonical values referring to [Task](../concepts/fhir/task.md) profiles in ActivityDefinitions have to conform
to the rules outlined by the documentation on [URLs](../concepts/dsf/about-version-placeholders-and-urls.md#urls).  
From the definition above, we will create the following extension element and add it to our process authorization element:
```xml
<extension url="task-profile">
    <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
</extension>
```

<details>
<summary>This is how your process authorization element should look like so far</summary>

```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization">
    <extension url="message-name">
        <valueString value="myMessage"/>
    </extension>
    <extension url="task-profile">
        <valueCanonical value="http://dsf.dev/fhir/StructureDefinition/my-task|#{version}"/>
    </extension>
</extension>
```
</details>

The next slice is `requester`:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
    <differential> 
     ...
        <element id="Extension.extension:requester">
            <path value="Extension.extension" />
            <sliceName value="requester" />
            <min value="1" />
        </element>
        <element id="Extension.extension:requester.url">
            <path value="Extension.extension.url" />
            <fixedUri value="requester" />
        </element>
        <element id="Extension.extension:requester.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Coding" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all-practitioner|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization-practitioner|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role-practitioner|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-all|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-organization|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-remote-parent-organization-role|1.0.0" />
            </type>
            <binding>
                <strength value="required" />
                <valueSet value="http://dsf.dev/fhir/ValueSet/process-authorization-requester|1.0.0" />
            </binding>
        </element>
     ...
    </differential>
</StructureDefinition>
```
Instead of a `string` or `canonical` type for `value[x]` we now have a `Coding` type. See the [FHIR documentation on Codings](https://www.hl7.org/fhir/R4/datatypes.html#Coding)
for more in-depth information. `Codings` are elements which contain, among other things, a `code` and the `system` the code belongs to. In the same way we transformed `value[x]` into `valueString` or `valueCanonical` before, we will also
have to turn `value[x]` into `valueCoding`. To use `Codings` in `valueCoding` elements, they are usually bound to the element through a [ValueSet](../concepts/fhir/valueset.md). This is the
responsibility of the `binding` element. You can also see that `value[x].type.profile` lists a number of profiles. They further constrain the `Coding` element to comply with one of these profiles. Depending on your use case, you have to pick one of the profiles.
Here is what they mean:
- `local-all`: All local requests will be allowed. Local requests are identified by matching the requester's certificate to a thumbprint which was internally marked by the DSF FHIR server as belonging to a local organization.
- `local-organization`: All local requests made from an organization with a specific `organization-identifier` will be allowed.
- `local-parent-organization-role`: All local requests made from an organization having a specific role inside a specific parent organization will be allowed.
- `remote` versions of the above rules work the same but the requester's certificate is instead required to match a thumbprint marked as a remote organization.
- `practitioner` suffixes all work the same. They include the same rules as their prefixes but now additionally require the requester to match a certain `practitioner-role`. A list of them
  can be found [here](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-practitioner-role-2.0.0.xml). This allows
  for more granularity when defining authorization rules within an organization and can be integrated into local user management via [OpenID Connect](https://dsf.dev/stable/maintain/fhir/access-control.html).

As you can see, there are no `practitioner` versions of `remote` authorization rules. From the perspective of the receiving DSF instance,
remote requests are always issued by an organization. They do not hold any information about the local user management of the requesting organization.  
You can also find examples of all Codings from above [here](../concepts/dsf/examples-for-requester-and-recipient-elements.md).

It is also good to keep in mind that you are allowed to add any number of `requester` elements into your [ActivityDefinition](../concepts/fhir/activitydefinition.md).  
Let us start out by adding a `requester` element like we did for previous elements:

```xml
<extension url="requester">
    <valueCoding>
        
    </valueCoding>
</extension>
```
**There is a [list of common requester element configurations](../concepts/dsf/examples-for-requester-and-recipient-elements.md)**


We now have to look at the elements that are defined in one of the profiles to fill in the remaining elements since they are not defined by the `requester` extension. For demonstration
purposes, we will choose the [dsf-coding-process-authorization-local-organization-practitioner](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-coding-process-authorization-local-organization-practitioner-2.0.0.xml) profile.
Since all elements listed in the [Coding definition](https://www.hl7.org/fhir/R4/datatypes.html#codesystem) are optional, we only have to look at the `differential` element from the profile we just selected:
<a id="coding-differential"></a>
```xml
<differential>
    <element id="Coding.extension">
        <path value="Coding.extension" />
        <slicing>
            <discriminator>
                <type value="value" />
                <path value="url" />
            </discriminator>
            <rules value="open" />
        </slicing>
    </element>
    <element id="Coding.extension:organization-practitioner">
        <path value="Coding.extension" />
        <sliceName value="organization-practitioner" />
        <min value="1" />
        <max value="1" />
        <type>
            <code value="Extension" />
            <profile value="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner|1.0.0" />
        </type>
    </element>
    <element id="Coding.system">
        <path value="Coding.system" />
        <min value="1" />
        <fixedUri value="http://dsf.dev/fhir/CodeSystem/process-authorization" />
    </element>
    <element id="Coding.code">
        <path value="Coding.code" />
        <min value="1" />
        <fixedCode value="LOCAL_ORGANIZATION_PRACTITIONER" />
    </element>
</differential>
```
It defines an extension called `organization-practitioner` which is identified through its url attribute. Again, the extension
is only referenced, its location is in a different file. You can find it [here](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-organization-practitioner-2.0.0.xml).
Let us look at its `differential` element in the extension file to see how we need to populate the extension:
```xml
<differential>
    <element id="Extension">
        <path value="Extension" />
        <min value="1" />
        <max value="1" />
    </element>
    <element id="Extension.extension">
        <path value="Extension.extension" />
        <slicing>  
            <discriminator>
                <type value="value" />
                <path value="url" />
            </discriminator>
            <rules value="open" />
        </slicing>
    </element>
    <element id="Extension.extension:organization">
        <path value="Extension.extension" />
        <sliceName value="organization" />
        <min value="1" />
        <max value="1" />
    </element>
    <element id="Extension.extension:organization.url">
        <path value="Extension.extension.url" />
        <fixedUri value="organization" />
    </element>
    <element id="Extension.extension:organization.value[x]">
        <path value="Extension.extension.value[x]" />
        <min value="1" />
        <type>
            <code value="Identifier" />
        </type>
    </element>
    <element id="Extension.extension:organization.value[x].system">
        <path value="Extension.extension.value[x].system" />
        <min value="1" />
        <fixedUri value="http://dsf.dev/sid/organization-identifier" />
    </element>
    <element id="Extension.extension:organization.value[x].value">
        <path value="Extension.extension.value[x].value" />
        <min value="1" />
    </element>
    <element id="Extension.extension:practitionerRole">
        <path value="Extension.extension" />
        <sliceName value="practitionerRole" />
        <min value="1" />
        <max value="1" />
    </element>
    <element id="Extension.extension:practitionerRole.url">
        <path value="Extension.extension.url" />
        <fixedUri value="practitioner-role" />
    </element>
    <element id="Extension.extension:practitionerRole.value[x]">
        <path value="Extension.extension.value[x]" />
        <min value="1" />
        <type>
            <code value="Coding" />
        </type>
    </element>
    <element id="Extension.extension:practitionerRole.value[x].system">
        <path value="Extension.extension.value[x].system" />
        <min value="1" />
    </element>
    <element id="Extension.extension:practitionerRole.value[x].code">
        <path value="Extension.extension.value[x].code" />
        <min value="1" />
    </element>
    <element id="Extension.url">
        <path value="Extension.url" />
        <fixedUri value="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner" />
    </element>
    <element id="Extension.value[x]">
        <path value="Extension.value[x]" />
        <max value="0" />
    </element>
</differential>
```

This extension does not reference any other files. This means we reached the "deepest" level. So now we can start working our way back up again from here, by translating this
definition into actual extension elements, then inserting it into the Coding we selected, translating the rest of the element
definitions from the Coding resource and adding everything to our [ActivityDefinition](../concepts/fhir/activitydefinition.md).

We will start with the `Extension.url` element, since the `Extension` element is the parent element for all slices on the `Extension.extension` elements:
```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">

</extension>
```

Next, we will add the `organization` slice:
```xml
<extension url="http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization-practitioner">
    <extension url="organization">
        <valueIdentifier>
            <system value="http://dsf.dev/sid/organization-identifier"/>
            <value value="My_Organization"/>
        </valueIdentifier>
    </extension>
</extension>
```
Finally, we will add the `practitionerRole` slice:

```xml
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
```

Notice that there is no `binding` element specified for `practitionerRole.value[x]`. This is intentional. In the example we used a code from the
[dsf-practitioner-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-practitioner-role-2.0.0.xml) CodeSystem.
This CodeSystem includes a standard set of codes which are often sufficient for DSF use cases. You can freely add other CodeSystems if you find these codes
do not apply for your use case. The code you set here can be used in the [DSF role config](https://dsf.dev/stable/maintain/fhir/access-control.html)
to allow certain users with this `practitioner-role` to send requests.

Working our way back up to the Coding we selected, we will now add the extension we just created as the `Coding.extension:organization-practitioner` element:
```xml
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
    </valueCoding>
</extension>
```
Now might be a good time to look at the [differential](#coding-differential) from the Coding again.  
Our next elements to be added are `Coding.system` and `Coding.code`:
```xml
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
```
Now we are finished with the `requester` extension and can add it to our process authorization element.

<details>
<summary>This is how your process authorization element should look like so far</summary>

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
</extension>
```
</details>

Now we are back to looking at the [dsf-extension-process-authorization](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml) again.
The last slice for this extension is `recipient`:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
    <differential> 
     ...
        <element id="Extension.extension:recipient">
            <path value="Extension.extension" />
            <sliceName value="recipient" />
            <min value="1" />
        </element>
        <element id="Extension.extension:recipient.url">
            <path value="Extension.extension.url" />
            <fixedUri value="recipient" />
        </element>
        <element id="Extension.extension:recipient.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Coding" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-all|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-organization|1.0.0" />
                <profile value="http://dsf.dev/fhir/StructureDefinition/coding-process-authorization-local-parent-organization-role|1.0.0" />
            </type>
            <binding>
                <strength value="required" />
                <valueSet value="http://dsf.dev/fhir/ValueSet/process-authorization-recipient|1.0.0" />
            </binding>
        </element>
     ...
    </differential>
</StructureDefinition>
```
**There is a [list of common recipient element configurations](../concepts/dsf/examples-for-requester-and-recipient-elements.md)**


The `recipient` will decide which DSF instance is allowed to process that message. That is the reason why you will not find any Codings for `remote` or `practitioner` here.
For `requester`, we already decided that we will only allow users with a certain role from our own (local) organization to send this message.
So now we will only allow the DSF instance run by that same local organization to process the message. The right Coding for this job is
the [coding-process-authorization-local-organization](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-coding-process-authorization-local-organization-2.0.0.xml).
The configuration of a local requester and local receiver is often used for the message that starts up the first BPMN process of the plugin.  
The process of adding the `recipient` slice is the exact same as it is for `requester`. You can follow the steps for the `requester` slice again
but just use a different Coding.

<details>
<summary>Using the Coding we just decided on, this is how your process authorization element should look like</summary>

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
</details>

The last element defined in the [process authorization extension](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-process-authorization-2.0.0.xml)
is `Extension.url`. But since we added this element at the very beginning of the working through the extension, we are finished with it here.
