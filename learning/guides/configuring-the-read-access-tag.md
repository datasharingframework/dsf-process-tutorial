### Configuring the Read Access Tag

To start off, you want to take a look at the [CodeSystem](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-read-access-tag-2.0.0.xml) defined for the [Read Access Tag](../concepts/dsf/read-access-tag.md)
and choose one of the codes from it:
```xml
<CodeSystem xmlns="http://hl7.org/fhir">
    ...
    <url value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
	...
	<concept>
		<code value="LOCAL"/>
		<display value="Local"/>
		<definition value="Read access for local users"/>
	</concept>
	<concept>
		<code value="ORGANIZATION"/>
		<display value="Organization"/>
		<definition value="Read access for organization specified via extension http://dsf.dev/fhir/StructureDefinition/extension-read-access-organization"/>
	</concept>
	<concept>
		<code value="ROLE"/>
		<display value="Role"/>
		<definition value="Read access for member organizations with role in consortium (parent organization) specified via extension http://dsf.dev/fhir/StructureDefinition/extension-read-access-consortium-role"/>
	</concept>
	<concept>
		<code value="ALL"/>
		<display value="All"/>
		<definition value="Read access for remote and local users"/>
	</concept>
</CodeSystem> 
```

The codes `LOCAL` and `ALL` are trivial. Their [Read Access Tag](../concepts/dsf/read-access-tag.md) would look like this:
```xml
<meta>
    <tag>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ALL"/> <!-- or value="LOCAL" respectively-->
    </tag>
</meta>
```

Let us try to configure a Read Access Tag whose code uses an extension. We will choose `ROLE` for this example. We start out the same way as before:
```xml
<meta>
    <tag>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

The `definition` element of the `ROLE` code references an extension called [dsf-extension-read-access-parent-organization-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-extension-read-access-parent-organization-role-2.0.0.xml).

The most important part of it is the `differential` statement. It uses [element definitions](https://www.hl7.org/fhir/R4/elementdefinition.html) to describe how we need to implement the extension:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
    ...
    <differential>
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
        <element id="Extension.extension:parentOrganization">
            <path value="Extension.extension" />
            <sliceName value="parentOrganization" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:parentOrganization.url">
            <path value="Extension.extension.url" />
            <fixedUri value="parent-organization" />
        </element>
        <element id="Extension.extension:parentOrganization.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Identifier" />
            </type>
        </element>
        <element id="Extension.extension:parentOrganization.value[x].system">
            <path value="Extension.extension.value[x].system" />
            <min value="1" />
            <fixedUri value="http://dsf.dev/sid/organization-identifier" />
        </element>
        <element id="Extension.extension:parentOrganization.value[x].value">
            <path value="Extension.extension.value[x].value" />
            <min value="1" />
        </element>
        <element id="Extension.extension:organizationRole">
            <path value="Extension.extension" />
            <sliceName value="organizationRole" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:organizationRole.url">
            <path value="Extension.extension.url" />
            <fixedUri value="organization-role" />
        </element>
        <element id="Extension.extension:organizationRole.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Coding" />
            </type>
        </element>
        <element id="Extension.extension:organizationRole.value[x].system">
            <path value="Extension.extension.value[x].system" />
            <min value="1" />
        </element>
        <element id="Extension.extension:organizationRole.value[x].code">
            <path value="Extension.extension.value[x].code" />
            <min value="1" />
        </element>
        <element id="Extension.url">
            <path value="Extension.url" />
            <fixedUri value="http://dsf.dev/fhir/StructureDefinition/extension-read-access-parent-organization-role" />
        </element>
        <element id="Extension.value[x]">
            <path value="Extension.value[x]" />
            <max value="0" />
        </element>
    </differential>
</StructureDefinition>
```

All extensions for the [Read Access Tag](../concepts/dsf/read-access-tag.md) CodeSystem are defined on the `meta.tag.extension` element through
the extension's `context` element:
```xml
<context>
    <type value="element" />
    <expression value="Coding" />   <!-- meta.tag is of type Coding-->
</context>
```

That is why the first element we are adding to `meta.tag` is an `extension` element:
```xml
<meta>
    <tag>
        <extenion>
         
        </extenion>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

We will now go through the `differential` statement one element at a time, starting at the top:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
    ...
    <differential>
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

It defines a [slicing](https://www.hl7.org/fhir/R4/profiling.html#slicing) for the `Extension.extension` element, meaning we are dealing
with a nested extension. The `discriminator` element tells us that slices will be identified by the value of their `url` attribute.
A `rules` element with value `open` means other types of slices may be added later on e.g. when creating a profile. We do not have to
add any elements from here to the `meta.tag.extension` element.  
Next up is the first slice called `parentOrganization`:

```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
    ...
    <differential>
        ...
        <element id="Extension.extension:parentOrganization">
            <path value="Extension.extension" />
            <sliceName value="parentOrganization" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:parentOrganization.url">
            <path value="Extension.extension.url" />
            <fixedUri value="parent-organization" />
        </element>
        <element id="Extension.extension:parentOrganization.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Identifier" />
            </type>
        </element>
        <element id="Extension.extension:parentOrganization.value[x].system">
            <path value="Extension.extension.value[x].system" />
            <min value="1" />
            <fixedUri value="http://dsf.dev/sid/organization-identifier" />
        </element>
        <element id="Extension.extension:parentOrganization.value[x].value">
            <path value="Extension.extension.value[x].value" />
            <min value="1" />
        </element>
        ...
    </differential>
</StructureDefinition>
```

The first element defines a slice called `parentOrganization` on the `Extension.extension` element with cardinality `1..1`.
The second element defines the url attribute of the `parentOrganization` slice to be fixed to the value `parent-organization`.
With this information we can add the next element to `meta.tag`. Since it is defined on `Extension.extension` we will add it to
`meta.tag.extension.extension` like this:
```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
             
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

After that, it defines `parentOrganization.value[x]` to occur at least once and have a type of `Identifier`. To turn this into an
element to add to `meta.tag.extension.extension` we have to replace `[x]` with our code in `value[x].type`, which in this case is `Identifier`. It is important
to note, that should the value in the code element be lowercase, you will have make it uppercase before replacement. In our case this means we will have a
`meta.tag.extension.extension.valueIdentifier` element:
```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
                <valueIdentifier>
                    
                </valueIdentifier>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

The last two elements define a `system` element with a fixed value and `value` element we can fill in on our own, since it does not have any constraints applied. Notice that
the element definition still uses `value[x].system` and `value[x].value`. The replacement mentioned earlier does not happen in
the element definition, but since `value[x]` is defined to have the type `Identifier` it is inferred that we mean to reference `Identifier.system`
and `Identifier.value`.  
We will choose an arbitrary `Idenfier` value, but you should be using an actual organization identifier depending on who you want to allow read access to the resource.

```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

Next is the slice is called `organizationRole`:
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
    ...
    <differential>
        ...
        <element id="Extension.extension:organizationRole">
            <path value="Extension.extension" />
            <sliceName value="organizationRole" />
            <min value="1" />
            <max value="1" />
        </element>
        <element id="Extension.extension:organizationRole.url">
            <path value="Extension.extension.url" />
            <fixedUri value="organization-role" />
        </element>
        <element id="Extension.extension:organizationRole.value[x]">
            <path value="Extension.extension.value[x]" />
            <min value="1" />
            <type>
                <code value="Coding" />
            </type>
        </element>
        <element id="Extension.extension:organizationRole.value[x].system">
            <path value="Extension.extension.value[x].system" />
            <min value="1" />
        </element>
        <element id="Extension.extension:organizationRole.value[x].code">
            <path value="Extension.extension.value[x].code" />
            <min value="1" />
        </element>
        ...
    </differential>
</StructureDefinition>
```

Like with `parentOrganization`, we will add an extension element to `meta.tag.extension` with the fixed url value defined above:
```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
             
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

Instead of `Identifier`, the `value[x]` element is now defined as a `Coding` type. This means we will add a `valueCoding` element to the extension:
```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                 
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

A `Coding` has to belong to some [CodeSystem](../concepts/fhir/codesystem.md). The DSF has a CodeSystem called [dsf-organization-role](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/CodeSystem/dsf-organization-role-2.0.0.xml).
Before creating your own CodeSystem, it is worth taking a look at it to see if an appropriate role already exists for your organization.
For demonstration purposes, we will be using the `DIC` role:
```xml
<meta>
    <tag>
        <extension>
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

Now we only have two elements left in the `differential` statement:

```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
    ...
    <differential>
        ...
        <element id="Extension.url">
            <path value="Extension.url" />
            <fixedUri value="http://dsf.dev/fhir/StructureDefinition/extension-read-access-parent-organization-role" />
        </element>
        <element id="Extension.value[x]">
            <path value="Extension.value[x]" />
            <max value="0" />
        </element>
    </differential>
</StructureDefinition>
```

The `Extension.url` element tells us to add a url attribute to `meta.tag.extension`. The last element makes it so we must not
add a `meta.tag.extension.value[x]` element. This leaves us with this final Read Access Tag:

```xml
<meta>
    <tag>
        <extension url="http://dsf.dev/fhir/StructureDefinition/extension-read-access-parent-organization-role">
            <extension url="parent-organization">
                <valueIdentifier>
                    <system value="http://dsf.dev/sid/organization-identifier"/>
                    <value value="My_Organization"/>
                </valueIdentifier>
            </extension>
            <extension url="organization-role">
                <valueCoding>
                    <system value="http://dsf.dev/fhir/CodeSystem/organization-role"/>
                    <code value="DIC"/>
                </valueCoding>
            </extension>
        </extension>
        <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
        <code value="ROLE"/> 
    </tag>
</meta>
```

You can follow the same method to configure the other types of Read Access Tags as well.