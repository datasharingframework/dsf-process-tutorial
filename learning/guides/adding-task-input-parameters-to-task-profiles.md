### Adding Task Input Parameters to Task Profiles

Task Input Parameters are added to the differential element of a [StructureDefinition](https://www.hl7.org/fhir/R4/structuredefinition.html) in the
`src/resources/fhir/StructureDefinition` directory of a project: 
```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  <meta>
    <tag>
      <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag" />
      <code value="ALL" />
    </tag>
  </meta>
  <url value="http://dsf.dev/fhir/StructureDefinition/my-task" />       <!-- this is a placeholder, replace with the URL of your Task -->
  <!-- version managed by bpe -->
  <version value="#{version}" />
  <name value="MyTask" />                                               <!-- "MyTask" is a placeholder, replace with the name of your Task -->
  <!-- status managed by bpe -->
  <status value="unknown" />
  <experimental value="false" />
  <!-- date managed by bpe -->
  <date value="#{date}" />
  <fhirVersion value="4.0.1" />
  <kind value="resource" />
  <abstract value="false" />
  <type value="Task" />
  <baseDefinition value="http://dsf.dev/fhir/StructureDefinition/task-base" />
  <derivation value="constraint" />
  <differential>
    <!-- Place input parameters here -->
  </differential>
</StructureDefinition>
```
[Input Parameters](../concepts/fhir/task.md#task-input-parameters) are made up of two elements: a `type` and a `value[x]`. `type` does not refer to the actual data type like `String`,
`Integer` or complex types like `Reference` or `Coding`. It instead describes the role of the [Input Parameter](../concepts/fhir/task.md#task-input-parameters)
in its usage context. For example, DSF Tasks require an [Input Parameter](../concepts/fhir/task.md#task-input-parameters) of `type` `message-name` because its role is to
identify the kind of message that is being sent. The actual data type of the [Input Parameter](../concepts/fhir/task.md#task-input-parameters) is 
defined by the `value[x]` element. Staying with the example of the `message-name` [Input Parameter](../concepts/fhir/task.md#task-input-parameters), its `value[x]` element is defined
to contain a `String` value. The `value[x]` element may be defined as either a fixed value or a "dynamic" value. Defining a fixed value means that an [Input Parameter](../concepts/fhir/task.md#task-input-parameters)
of type `type` may only exist with this exact value, or it is deemed invalid. "Dynamic" values may contain any value of the defined data type.

It is important to note that the `type` elements must always contain codes from a [CodeSystem](../concepts/fhir/codesystem.md). Since codes from [CodeSystems](../concepts/fhir/codesystem.md)
can only be included through [ValueSets](../concepts/fhir/valueset.md), all `type` element values require both a [CodeSystem](../concepts/fhir/codesystem.md) and [ValueSet](../concepts/fhir/valueset.md) resource to be valid.
See the guides for creating [CodeSystems](creating-codesystems-for-dsf-processes.md) and [ValueSets](creating-valuesets-for-dsf-processes.md) for more information.

This guide contains templates for the most common types of [Input Parameters](../concepts/fhir/task.md#task-input-parameters).
Before adding any, you should think about how many you want to add and adjust the expected amount of [Input Parameters](../concepts/fhir/task.md#task-input-parameters)
in the [StructureDefinition](https://www.hl7.org/fhir/R4/structuredefinition.html):

```xml
<StructureDefinition xmlns="http://hl7.org/fhir">
  ...
  <differential>
      <element id="Task.input">
          <extension url="http://hl7.org/fhir/StructureDefinition/structuredefinition-explicit-type-name">
              <valueString value="Parameter" />
          </extension>
          <path value="Task.input" />
          <min value="4" />         <!-- adjust minimum number of input parameters -->
          <max value="6" />         <!-- adjust maximum number of input parameters -->
      </element>
  </differential>
</StructureDefinition>
```
If you cannot find an element with id `Task.input` you should create one. It should sit above any other elements that share the path of `Task.input`.

**Note**: The DSF already comes with 3 [Input Parameters](../concepts/fhir/task.md#task-input-parameters) predefined: `message-name`, `business-key` and `correlation-key`. `message-name` and `business-key` are mandatory in order
for the DSF to correlate the [Task](../concepts/fhir/task.md) to a process instance. So expect the minimum number of [Input Parameters](../concepts/fhir/task.md#task-input-parameters) to be at least **2**. When addressing a sub-process, 
a third [Input Parameter](../concepts/fhir/task.md#task-input-parameters) is required: `correlation-key`. This is beyond the scope of the tutorial. Keeping the minimum at **2** should be sufficient.

#### Template: Input Parameter of Primitive Type With Fixed Value

```xml
<element id="Task.input:my-input">          <!-- replace all occurrances of "my-input" with the name of your input parameter. -->
  <path value="Task.input"/>                <!-- this value should also be the value of the code from the CodeSystem in -->
  <sliceName value="my-input"/>             <!-- Task.input.type.coding.code's <fixedCode/> element -->
  <min value="0"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type">
  <path value="Task.input.type"/>
  <min value="1"/>
  <max value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-valueset"/>        <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
<element id="Task.input:my-input.type.coding">
  <path value="Task.input.type.coding"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type.coding.system">
  <path value="Task.input.type.coding.system"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-codesystem"/>      <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.input:my-input.type.coding.code">
  <path value="Task.input.type.coding.code"/>
  <min value="1"/>
  <max value="1"/>
  <fixedCode value="my-input"/>
</element>
<element id="Task.input:my-input.value[x]">
  <path value="Task.input.value[x]"/>
  <min value="1"/>
  <max value="1"/>
  <fixedInteger value="1"/>                                             <!-- replace "fixedInteger" with a primitive data type of your choosing and set the respective value -->
</element>
```

#### Template: Input Parameter of Primitive Type With "Dynamic" Value

```xml
<element id="Task.input:my-input">          <!-- replace all occurrances of "my-input" with the name of your input parameter. -->
  <path value="Task.input"/>                <!-- this value should also be the value of the code from the CodeSystem in -->
  <sliceName value="my-input"/>             <!-- Task.input.type.coding.code's <fixedCode/> element -->
  <min value="0"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type">
  <path value="Task.input.type"/>
  <min value="1"/>
  <max value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-valueset"/>        <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
<element id="Task.input:my-input.type.coding">
  <path value="Task.input.type.coding"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type.coding.system">
  <path value="Task.input.type.coding.system"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-codesystem"/>      <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.input:my-input.type.coding.code">
  <path value="Task.input.type.coding.code"/>
  <min value="1"/>
  <max value="1"/>
  <fixedCode value="my-input"/>
</element>
<element id="Task.input:my-input.value[x]">
  <path value="Task.input.value[x]"/>
  <min value="1"/>
  <max value="1"/>
  <type>
      <code value="integer"/>                                           <!-- replace "integer" with a primitive data type of your choosing -->
  </type>                                            
</element>
```

#### Template: Input Parameter of Coding Type With Fixed Value

```xml
<element id="Task.input:my-input">          <!-- replace all occurrances of "my-input" with the name of your input parameter. -->
  <path value="Task.input"/>                <!-- this value should also be the value of the code from the CodeSystem in -->
  <sliceName value="my-input"/>             <!-- Task.input.type.coding.code's <fixedCode/> element -->
  <min value="0"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type">
  <path value="Task.input.type"/>
  <min value="1"/>
  <max value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-valueset"/>        <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
<element id="Task.input:my-input.type.coding">
  <path value="Task.input.type.coding"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type.coding.system">
  <path value="Task.input.type.coding.system"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-codesystem"/>      <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.input:my-input.type.coding.code">
  <path value="Task.input.type.coding.code"/>
  <min value="1"/>
  <max value="1"/>
  <fixedCode value="my-input"/>
</element>
<element id="Task.output:my-input.value[x]">
  <path value="Task.output.value[x]"/>
  <type>
    <code value="Coding"/>
  </type>
</element>
<element id="Task.output:my-input.value[x].system">
  <path value="Task.output.value[x].system"/>
  <min value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-other-codesystem"/>    <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.output:my-input.value[x].code">
  <path value="Task.output.value[x].code"/>
  <min value="1"/>
  <fixedCode value="my-code"/>                                          <!-- replace "my-code" with the value of your code -->
</element>
```

#### Template: Input Parameter of Coding Type With "Dynamic" Value

```xml
<element id="Task.input:my-input">          <!-- replace all occurrances of "my-input" with the name of your input parameter. -->
  <path value="Task.input"/>                <!-- this value should also be the value of the code from the CodeSystem in -->
  <sliceName value="my-input"/>             <!-- Task.input.type.coding.code's <fixedCode/> element -->
  <min value="0"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type">
  <path value="Task.input.type"/>
  <min value="1"/>
  <max value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-valueset|#{version}"/>        <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
<element id="Task.input:my-input.type.coding">
  <path value="Task.input.type.coding"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type.coding.system">
  <path value="Task.input.type.coding.system"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-codesystem"/>      <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.input:my-input.type.coding.code">
  <path value="Task.input.type.coding.code"/>
  <min value="1"/>
  <max value="1"/>
  <fixedCode value="my-input"/>
</element>
<element id="Task.output:my-input.value[x]">
  <path value="Task.output.value[x]"/>
  <type>
    <code value="Coding"/>
  </type>
</element>
<element id="Task.output:my-input.value[x].system">
  <path value="Task.output.value[x].system"/>
  <min value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-other-codesystem"/>     <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.output:my-input.value[x].code">
  <path value="Task.output.value[x].code"/>
  <min value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-other-valueset|#{version}"/>  <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
```

#### Template: Input Parameter of Reference Type With "Dynamic" Value

```xml
<element id="Task.input:my-input">          <!-- replace all occurrances of "my-input" with the name of your input parameter. -->
  <path value="Task.input"/>                <!-- this value should also be the value of the code from the CodeSystem in -->
  <sliceName value="my-input"/>             <!-- Task.input.type.coding.code's <fixedCode/> element -->
  <min value="0"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type">
  <path value="Task.input.type"/>
  <min value="1"/>
  <max value="1"/>
  <binding>
    <strength value="required"/>
    <valueSet value="http://dsf.dev/fhir/ValueSet/my-valueset"/>        <!-- replace URL with the URL of your ValueSet -->
  </binding>
</element>
<element id="Task.input:my-input.type.coding">
  <path value="Task.input.type.coding"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.type.coding.system">
  <path value="Task.input.type.coding.system"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="http://dsf.dev/fhir/CodeSystem/my-codesystem"/>      <!-- replace URL with the URL of your CodeSystem -->
</element>
<element id="Task.input:my-input.type.coding.code">
  <path value="Task.input.type.coding.code"/>
  <min value="1"/>
  <max value="1"/>
  <fixedCode value="my-input"/>
</element>
<element id="Task.input:my-input.value[x]">
  <path value="Task.input.value[x]"/>
  <type>
    <code value="Reference" />
    <targetProfile value="http://hl7.org/fhir/StructureDefinition/Binary" />    <!-- replace with the profile URL of the kind of resource you want to reference -->
  </type>
</element>
<element id="Task.input:my-input.value[x].reference">        
  <path value="Task.input.value[x].reference"/>
  <min value="1"/>
  <max value="1"/>
</element>
<element id="Task.input:my-input.value[x].type">
  <path value="Task.input.value[x].type"/>
  <min value="1"/>
  <max value="1"/>
  <fixedUri value="Binary"/>                                                    <!-- replace with the kind of resource you want to reference -->
</element>
<element id="Task.input:my-input.value[x].identifier">
  <path value="Task.input.value[x].identifier"/>
  <max value="0"/>
</element>
```