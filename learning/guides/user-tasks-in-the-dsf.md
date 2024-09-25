### User Tasks in the DSF

Creating a [User Task](../concepts/bpmn/user-tasks.md) in a BPMN model, causes the DSF to automatically generate a [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) resource
according to a [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html) you provided in the [User Task's](../concepts/bpmn/user-tasks.md) `Forms` field when the process execution reaches the [User Task](../concepts/bpmn/user-tasks.md).
The `Forms` field needs to have a type of `Embedded or External Task Forms` with the `Form key` being the url of your [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html) resource.
The [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html) resource needs to be put in the `src/main/resources/fhir/Questionnaire` directory.
The generated [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) can now be answered by locating 
the [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) in the DSF FHIR server UI through `https://your.dsf.fhir.server/fhir/QuestionnaireResponse?_sort=-_lastUpdated&status=in-progress`.
After filling out the [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) and submitting it, the process execution will continue with the next BPMN element after the
[User Task](../concepts/bpmn/user-tasks.md) and the updated [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) will be available through the [Process Plugin Api's](../concepts/dsf/process-api.md)
`Variables` instance by calling `getLatestReceivedQuestionnaireResponse()`.  

You also have the option to register a [Task Listener](https://docs.camunda.org/manual/7.21/user-guide/process-engine/delegation-code/#task-listener) on the [User Task](../concepts/bpmn/user-tasks.md). This allows you to manipulate the [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) before it is posted to the DSF FHIR server.
You do this by extending the `DefaultUserTaskListener` class which provides overrides to interact with the [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html).
Notice that dynamically changing the `item.text` value of an item in a [QuestionnaireResponse](https://www.hl7.org/fhir/R4/questionnaireresponse.html) (that is **NOT** of type `display`) is not allowed. 
For that, you would have to change the `item.text` value of the corresponding [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html) resource as well. 
Instead, you should have an item of type `display` above the item whose text should change dynamically, like in the template, and change its `item.text` value. 
In this case, you may also leave out `item.text` element of the item below the display item.

Below you can find a template for a [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html) resource. Replace `questionnaire-name` with the name of your [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html)
and have the file be named the same. The items `business-key` and `user-task-id` are required by the DSF and are always included. You can then add any amount of items of your choosing
to the [Questionnaire](https://www.hl7.org/fhir/R4/questionnaire.html).

### Questionnaire Template
```xml
<Questionnaire xmlns="http://hl7.org/fhir">
    <meta>
        <profile value="http://dsf.dev/fhir/StructureDefinition/questionnaire|1.5.0"/>
        <tag>
            <system value="http://dsf.dev/fhir/CodeSystem/read-access-tag"/>
            <code value="ALL"/>
        </tag>
    </meta>
    <url value="http://dsf.dev/fhir/Questionnaire/questionnaire-name"/>     <!-- file name should be same as the name of your Questionnaire -->
    <!-- version managed by bpe -->
    <version value="#{version}"/>
    <!-- date managed by bpe -->
    <date value="#{date}"/>
    <!-- status managed by bpe -->
    <status value="unknown"/>
    <item>
        <!-- required  -->
        <linkId value="business-key"/>
        <type value="string"/>
        <text value="The business-key of the process execution"/>
        <required value="true"/>
    </item>
    <item>
        <!-- required  -->
        <linkId value="user-task-id"/>
        <type value="string"/>
        <text value="The user-task-id of the process execution"/>
        <required value="true"/>
    </item>
    <item>
        <linkId value="text-to-display-above-item"/>
        <type value="display"/>
        <text value="foo"/>
    </item>
    <item>
        <linkId value="item"/>
        <type value="boolean"/>
        <text value="Item description"/>
        <required value="true"/>
    </item>
</Questionnaire>
```