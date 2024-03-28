### Creating Task Resources Based on a Definition

This short guide should help you understand how you can create [Task](../concepts/fhir/task.md)
resources for use in [Starting A Process Via Task Resources](../guides/starting-a-process-via-task-resources.md).
We will employ the use of the free version of [Forge](https://simplifier.net/forge?utm_source=firely-forge) to help
with visualization. You are invited to create a free account and follow along, but we will include screenshots of relevant
views either way. Remember that the free version of Forge [must not be used commercially](https://simplifier.net/pricing).  
As an example, we will create a [Task](../concepts/fhir/task.md) resource from the `task-start-dic-process.xml` profile.

#### 1st Step: Removing Placeholders
`task-start-dic-process.xml` includes placeholders for the `version` and `date` elements. For the duration of this guide,
you can either remove or comment these elements, so Forge does not try to perform type checking on them, which
would result in an error and Forge not loading the file.

#### 2nd Step: Differential Chain
If the resource profile is only available as a [differential](https://www.hl7.org/fhir/R4/profiling.html#snapshot), like in our
case, we will want to aggregate the changes made to the base resource (in this case [Task](../concepts/fhir/task.md)) by all profiles to make
it more readable.
To do this, we first need all the profiles involved. We already have `task-start-dic-process.xml` in our `StructureDefinition` folder.
It lists a resource called `task-base` in its `baseDefinition` element. This resource is part of the DSF and can be
found [here](https://github.com/datasharingframework/dsf/blob/main/dsf-fhir/dsf-fhir-validation/src/main/resources/fhir/StructureDefinition/dsf-task-base-1.0.0.xml).
Put it into the `StructureDefinition` folder. Since `task-base` has the original FHIR Task as its `baseDefinition`
element, we are done with this chain.
In forge, you should now be able to open the `StructureDefinition` folder and select the `task-start-dic-process.xml` profile.
It should look something like this:

![Forge overview](../../exercises/figures/forge_overview.png)

#### 3rd Step: Building the Task Resource
We will now go through each element one by one and include it into our [Task](../concepts/fhir/task.md)
resource, provided it is mandatory (cardinality at least `1..1`) according to the profile. It is important
that you not use any placeholders like `#{version}` for resources not read by the DSF BPE server. This is the case
if we want a [Task](../concepts/fhir/task.md) resource for use with [cURL](../guides/starting-a-process-via-task-resources.md#using-curl).
But, placeholders should be used in [Draft Task Resources](../concepts/dsf/draft-task-resources.md) instead of actual values wherever possible,
since those are read by the DSF BPE server. This guide will create a [Task](../concepts/fhir/task.md) resource without placeholders.  
We will start out with the base element for all [Task](../concepts/fhir/task.md) resources:
```xml
<Task xmlns="http://hl7.org/fhir">

</Task>
```

Before we start adding any elements listed in Forge's element tree, we have to include the `Task.meta.profile` element.
Its requirement cannot be seen here which is why we mention it specifically. This is the only instance you will not see
it in the element tree. It should look like this:
```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
</Task>
```

The first element which can be found in the element tree is the `instantiatesCanonical` element. To add it, we
will create an XML element with the same name and the value according to [URLs](../concepts/dsf/about-version-placeholders-and-urls.md#urls):
```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
</Task>
```
We can continue this process for all primitive elements like these. Just make sure you pay attention to use the correct
data type (e.g. proper coding value for elements with `coding` type).

By now your [Task](../concepts/fhir/task.md) resources should look something like this:
<details>
<summary>Suggested solution</summary>

```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
</Task>
```
</details>

Let us look at a more complex element like the `requester` element:

![Forge requester view](../../exercises/figures/forge_requester_view.png)

We will start the same way we started with primitive elements, by adding the `requester` element:
```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
    <requester>
     
    </requester>
</Task>
```

Then, we will add primitive elements to `requester` like we did before for `Task`:
```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
    <requester>
        <type value="Organization"/>
    </requester>
</Task>
```
*Important to note here that the value for the `status` will always be `requested` for Tasks being posted using cURL and the `type` element for `requester` and `recipient` will always have the value `Organization` in the DSF context.*

Next, we will add the `identifier` element and its primitive sub-elements just like we started out doing it for the `requester` element. The `identifier.value` in this case will be `dic.dsf.test`. To understand why, take a look at the topic on [organization identifiers](../concepts/dsf/organization-identifiers.md):
```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
    <requester>
        <type value="Organization"/>
        <identifier>
            <system value="http://dsf.dev/sid/organization-identifier"/>
            <value value="dic.dsf.test" />
        </identifier>
    </requester>
</Task>
```
*Notice that `requester.identifier.system` has a `Fixed value` annotation. You can see what the value is supposed
to be by clicking on the `system` element in Forge or looking at the XML for the right Task profile. The right side will have all information about that element, including
the actual value for `Fixed value`.*

You should now be able to fill out all elements in your [Task](../concepts/fhir/task.md) resource until you reach
the [slicing](https://www.hl7.org/fhir/R4/profiling.html#slicing) for `Task.input`. Your [Task](../concepts/fhir/task.md)
resource should look something like this:
<details>
<summary>Suggested solution</summary>

```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
    <requester>
        <type value="Organization"/>
        <identifier>
            <system value="http://dsf.dev/sid/organization-identifier"/>
            <value value="dic.dsf.test" />
        </identifier>
    </requester>
    <restriction>
        <recipient>
            <type value="Organization"/>
            <identifier>
                <system value="http://dsf.dev/sid/organization-identifier" />
                <value value="dic.dsf.test" />
            </identifier>
        </recipient>
    </restriction>
</Task>
```
</details>


[Slicings](https://www.hl7.org/fhir/R4/profiling.html#slicing) are a bit different from regular elements. Let us look at the
slice `message-name`:

![Forge slice message name](../../exercises/figures/forge_slice_message_name.png)

If we were to continue including slices to the [Task](../concepts/fhir/task.md) resource like we did so far,
we would add a `message-name` element to our XML like this:

```xml
<Task xmlns="http://hl7.org/fhir">
    ...
    <input>
        <message-name>
            ...
        </message-name>
    </input>
</Task>
```

This approach however, would not work. FHIR processors do not use the name of the slice to map entries in
your [Task](../concepts/fhir/task.md) resource to the correct slice. They use [discriminators](https://www.hl7.org/fhir/R4/profiling.html#discriminator).
Discriminators define the elements a processor needs to distinguish slices by. You can see
how the discriminator is configured by selecting the `input` element in Forge. In our case, a processor
would look at the values for `input.type.coding.system` and `input.type.coding.code` to determine which
slice this element belongs to. This only works because `input.type.coding.system` and `input.type.coding.code`
are present in all slices and have a `Fixed value`. You can learn more about discriminators [here](https://www.hl7.org/fhir/R4/profiling.html#discriminator).  
All this means is that we effectively ignore the name of the slice as an element and start adding elements like we did before:

```xml
<Task xmlns="http://hl7.org/fhir">
    ...
    <input>
        <type>
            <coding>
                <system value="http://dsf.dev/fhir/CodeSystem/bpmn-message" />
                <code value="message-name" />
            </coding>
        </type>
        <valueString value="dicProcess" />
    </input>
</Task>
```

Now you should be able to add all remaining mandatory elements to your [Task](../concepts/fhir/task.md)
resource on your own. In the end, it should look something like this:
<details>
<summary>Suggested solution</summary>

```xml
<Task xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|1.0"/>
    </meta>
    <instantiatesCanonical value="http://dsf.dev/bpe/Process/dicProcess|1.0" />
    <status value="requested"/>
    <intent value="order"/>
    <authoredOn value="2024-02-08T10:00:00+00:00" />
    <requester>
        <type value="Organization"/>
        <identifier>
            <system value="http://dsf.dev/sid/organization-identifier"/>
            <value value="dic.dsf.test" />
        </identifier>
    </requester>
    <restriction>
        <recipient>
            <type value="Organization"/>
            <identifier>
                <system value="http://dsf.dev/sid/organization-identifier" />
                <value value="dic.dsf.test" />
            </identifier>
        </recipient>
    </restriction>
    <input>
        <type>
            <coding>
                <system value="http://dsf.dev/fhir/CodeSystem/bpmn-message" />
                <code value="message-name" />
            </coding>
        </type>
        <valueString value="dicProcess"/>
    </input>
</Task>
```
</details>

**Do not forget to restore the version and date placeholders in `task-start-dic-process.xml`!**