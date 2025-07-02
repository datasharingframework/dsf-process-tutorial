package dev.dsf.process.tutorial.exercise_4;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_COS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_COS_PROCESS_URI;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.RESOURCE_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.process.tutorial.FhirResourceLoader;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
import dev.dsf.process.tutorial.message.HelloCosMessage;
import dev.dsf.process.tutorial.service.DicTask;

public class TutorialProcessPluginDefinitionTest
{
	private final String version = "2.3.0.1";
	private final String resourceVersion = "2.3";
	private static List<Resource> dicFhirResources;
	private static List<Resource> cosFhirResources;

	@BeforeClass
	public static void loadResources()
	{
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();

		dicFhirResources = FhirResourceLoader.loadResourcesFor(definition, PROCESS_NAME_FULL_DIC);
		cosFhirResources = FhirResourceLoader.loadResourcesFor(definition, PROCESS_NAME_FULL_COS);
	}

	@Test
	public void testPluginVersion()
	{
		assertEquals(resourceVersion, RESOURCE_VERSION);
	}

	@Test
	public void testDicProcessBpmnProcessFile() throws Exception
	{
		String filename = "bpe/dic-process.bpmn";
		String processId = "dsfdev_dicProcess";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		String errorServiceTask = "Process '" + processId + "' in file '" + filename
				+ "' is missing implementation of class '" + DicTask.class.getName() + "'";
		assertTrue(errorServiceTask, processes.get(0).getChildElementsByType(ServiceTask.class).stream()
				.filter(Objects::nonNull).map(ServiceTask::getCamundaClass).anyMatch(DicTask.class.getName()::equals));

		List<MessageEventDefinition> messageEndEvent = processes.get(0).getChildElementsByType(EndEvent.class).stream()
				.filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorMessageEndEvent = "Process '" + processId + "' in file '" + filename
				+ "' should end with a MessageEndEvent";
		assertEquals(errorMessageEndEvent, 1, messageEndEvent.size());

		String errorMessageEndEventImplementation = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent java implementation class '" + HelloCosMessage.class.getName() + "'";
		assertEquals(errorMessageEndEventImplementation, HelloCosMessage.class.getName(),
				messageEndEvent.get(0).getCamundaClass());

		List<CamundaField> camundaFields = processes.get(0).getChildElementsByType(EndEvent.class).stream().findAny()
				.stream().flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream())
				.flatMap(e -> e.getChildElementsByType(ExtensionElements.class).stream())
				.flatMap(e -> e.getChildElementsByType(CamundaField.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorMessageEndEventInputs = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent with 3 field injections";
		assertEquals(errorMessageEndEventInputs, 3, camundaFields.size());

		String errorMessageEndEventInputUri = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent field injection with name 'instantiatesCanonical' and value '"
				+ PROFILE_TUTORIAL_TASK_COS_PROCESS_URI + "|#{version}'";
		assertTrue(errorMessageEndEventInputUri,
				camundaFields.stream().anyMatch(i -> "instantiatesCanonical".equals(i.getCamundaName())
						&& (PROFILE_TUTORIAL_TASK_COS_PROCESS_URI + "|#{version}").equals(i.getTextContent())));

		String errorMessageEndEventMessageName = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent field injection with name 'messageName' and value '"
				+ PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME + "'";
		assertTrue(errorMessageEndEventMessageName,
				camundaFields.stream().anyMatch(i -> "messageName".equals(i.getCamundaName())
						&& PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME.equals(i.getTextContent())));

		String errorMessageEndEventProfile = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent field injection with name 'profile' and value '"
				+ PROFILE_TUTORIAL_TASK_HELLO_COS + "|#{version}'";
		assertTrue(errorMessageEndEventProfile,
				camundaFields.stream().anyMatch(i -> "profile".equals(i.getCamundaName())
						&& (PROFILE_TUTORIAL_TASK_HELLO_COS + "|#{version}").equals(i.getTextContent())));
	}

	@Test
	public void testDicProcessResources() throws Exception
	{
		String codeSystemUrl = CODESYSTEM_TUTORIAL;
		String codeSystemCode = CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;
		String valueSetUrl = "http://dsf.dev/fhir/ValueSet/tutorial";
		String structureDefinitionUrl = "http://dsf.dev/fhir/StructureDefinition/task-start-dic-process";


		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		Map<String, List<String>> dicProcess = definition.getFhirResourcesByProcessId();

		int numberEntries = dicProcess.size();
		int expectedEntries = 2;
		String errorTooManyEntries = "Too many processes in Map. Got " + numberEntries + " entries. Expected "
				+ expectedEntries + ".";
		assertEquals(errorTooManyEntries, expectedEntries, numberEntries);

		String dicProcessKey = dicProcess.keySet().stream().filter(k -> k.equals(PROCESS_NAME_FULL_DIC)).findFirst()
				.get();
		String errorFaultyProcessName = "Process name is either wrong or missing. Expected '" + PROCESS_NAME_FULL_DIC
				+ "' but got '" + dicProcessKey + "' from TutorialProcessPluginDefinition#getFhirResourcesByProcessId";
		assertEquals(errorFaultyProcessName, PROCESS_NAME_FULL_DIC, dicProcessKey);

		String errorCodeSystem = "Process is missing CodeSystem with url '" + codeSystemUrl + "' and concept '"
				+ codeSystemCode + "' with type 'string'";
		assertEquals(errorCodeSystem, 1, dicFhirResources.stream().filter(r -> r instanceof CodeSystem)
				.map(r -> (CodeSystem) r).filter(c -> codeSystemUrl.equals(c.getUrl()))
				.filter(c -> c.getConcept().stream().anyMatch(con -> codeSystemCode.equals(con.getCode()))).count());

		String errorValueSet = "Process is missing ValueSet with url '" + valueSetUrl
				+ "' which includes CodeSystem with url '" + codeSystemUrl + "'";
		assertEquals(errorValueSet, 1, dicFhirResources.stream().filter(r -> r instanceof ValueSet)
				.map(r -> (ValueSet) r).filter(v -> valueSetUrl.equals(v.getUrl()))
				.filter(v -> v.getCompose().getInclude().stream().anyMatch(i -> codeSystemUrl.equals(i.getSystem())))
				.count());

		String errorStructureDefinition = "Process is missing StructureDefinition with url '" + structureDefinitionUrl
				+ "'";
		Optional<StructureDefinition> optionalStructureDefinition = dicFhirResources.stream()
				.filter(resource -> resource instanceof StructureDefinition)
				.map(resource -> (StructureDefinition) resource)
				.filter(structureDefinition -> structureDefinition.getUrl().equals(structureDefinitionUrl)).findFirst();

		assertTrue(errorStructureDefinition, optionalStructureDefinition.isPresent());

		StructureDefinition correctStructureDefinition = optionalStructureDefinition.get();
		String errorNotEnoughInputsAllowed = "StructureDefinition with url " + correctStructureDefinition.getUrl()
				+ " has 'Task.input.max' with value 2. Since you added a new input parameter in exercise 2 you need to increase this value to 3.";
		assertTrue(errorNotEnoughInputsAllowed,
				correctStructureDefinition.getDifferential().getElement().stream()
						.filter(elementDefinition -> elementDefinition.getId().equals("Task.input"))
						.anyMatch(elementDefinition -> Integer.valueOf(elementDefinition.getMax()).equals(3)));

		List<Task> draftTasks = getDraftTasks(dicFhirResources);
		if (!draftTasks.isEmpty())
		{
			String errorDraftTask = "Process is missing Draft Task Resource with InstantiatesCanonical '"
					+ PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL + "'.";
			Optional<Task> draftTask = draftTasks.stream().filter(task -> task.getInstantiatesCanonical()
					.equals(PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL)).findFirst();
			assertTrue(errorDraftTask, draftTask.isPresent());
			validateDraftTaskResource(draftTask.get());
		}
	}

	private void validateDraftTaskResource(Task draftTask)
	{
		String error = "Draft Task has wrong/missing meta.profile value. Expected 'http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|"
				+ resourceVersion + "' or 'http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|#{version}'.";
		assertTrue(error, draftTask.getMeta().getProfile().stream().anyMatch(profile -> profile.getValue()
				.equals("http://dsf.dev/fhir/StructureDefinition/task-start-dic-process|" + resourceVersion)));

		String identifierSystem = "http://dsf.dev/sid/task-identifier";
		error = "Draft Task has wrong/missing identifier.system value. Expected '" + identifierSystem + "'.";
		assertTrue(error, draftTask.getIdentifier().stream()
				.anyMatch(identifier -> identifier.getSystem().equals(identifierSystem)));

		String identifierValue = "http://dsf.dev/bpe/Process/dicProcess/" + resourceVersion + "/task-start-dic-process";
		String identifierValuePlaceholder = "http://dsf.dev/bpe/Process/dicProcess/#{version}/task-start-dic-process";
		error = "Draft Task has wrong/missing identifier.value. Expected '" + identifierValue + "' or '"
				+ identifierValuePlaceholder + "'.";
		assertTrue(error, draftTask.getIdentifier().stream()
				.anyMatch(identifier -> identifier.getValue().equals(identifierValue)));

		String instantiatesCanonical = PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI.concat("|" + resourceVersion);
		String instantiatesCanonicalPlaceholder = PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI.concat("|#{version}");
		error = "Draft Task has wrong/missing instantiatesCanonical value. Expected '" + instantiatesCanonical
				+ "' or '" + instantiatesCanonicalPlaceholder + "'.";
		assertTrue(error, draftTask.getInstantiatesCanonical().equals(instantiatesCanonical));

		error = "Draft Task has wrong/missing status value. Expected '" + Task.TaskStatus.DRAFT.name() + "'.";
		assertTrue(error, draftTask.getStatus().equals(Task.TaskStatus.DRAFT));

		error = "Draft Task has wrong/missing intent value. Expected '" + Task.TaskIntent.ORDER + "'.";
		assertTrue(error, draftTask.getIntent().equals(Task.TaskIntent.ORDER));

		error = "Draft Task has wrong/missing requester.identifier.value. Expected 'dic.dsf.test' or the organization placeholder '#{organization}'.";
		assertTrue(error, "dic.dsf.test".equals(draftTask.getRequester().getIdentifier().getValue())
				|| "#{organization}".equals(draftTask.getRequester().getIdentifier().getValue()));

		error = "Draft Task has wrong/missing restriction.recipient.identifier.value. Expected 'dic.dsf.test' or the organization placeholder '#{organization}'.";
		assertTrue(error,
				"dic.dsf.test".equals(draftTask.getRestriction().getRecipientFirstRep().getIdentifier().getValue())
						|| "#{organization}"
								.equals(draftTask.getRestriction().getRecipientFirstRep().getIdentifier().getValue()));

		String messageName = "startDicProcess";
		error = "Draft Task has wrong/missing input.valueString. Expected '" + messageName + "'.";
		assertTrue(error, draftTask.getInput().stream().filter(input -> input.getValue() instanceof StringType)
				.anyMatch(input -> ((StringType) input.getValue()).getValue().equals(messageName)));

		error = "Draft Task has wrong/missing input parameter 'tutorial-input'";
		assertTrue(error,
				draftTask.getInput().stream()
						.anyMatch(input -> input.getType().getCoding().stream()
								.allMatch(coding -> coding.getSystem().equals(CODESYSTEM_TUTORIAL)
										&& coding.getCode().equals(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT))));
	}

	private List<Task> getDraftTasks(List<Resource> resources)
	{
		return resources.stream().filter(resource -> resource instanceof Task).map(resource -> (Task) resource)
				.toList();
	}

	@Test
	public void testCosProcessBpmnProcessFile() throws Exception
	{
		String filename = "bpe/cos-process.bpmn";
		String processId = "dsfdev_cosProcess";

		boolean cosProcessConfigured = new TutorialProcessPluginDefinition().getProcessModels().stream()
				.anyMatch(f -> filename.equals(f));
		assertTrue("Process '" + processId + "' from file '" + filename + "' not configured in "
				+ TutorialProcessPluginDefinition.class.getSimpleName(), cosProcessConfigured);

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		List<MessageEventDefinition> messageStartEvent = processes.get(0).getChildElementsByType(StartEvent.class)
				.stream().filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorStartEvent = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageStartEvent";
		assertEquals(errorStartEvent, 1, messageStartEvent.size());

		String errorStartEventMessageName = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageStartEvent with message name 'helloCos'";
		assertEquals(errorStartEventMessageName, "helloCos", messageStartEvent.get(0).getMessage().getName());
	}

	@Test
	public void testCosProcessResources() throws Exception
	{
		String processUrl = "http://dsf.dev/bpe/Process/cosProcess";
		List<ActivityDefinition> activityDefinitions = cosFhirResources.stream()
				.filter(r -> r instanceof ActivityDefinition).map(r -> (ActivityDefinition) r)
				.filter(a -> processUrl.equals(a.getUrl())).filter(a -> resourceVersion.equals(a.getVersion()))
				.collect(Collectors.toList());

		String errorActivityDefinition = "Process is missing ActivityDefinition with url '" + processUrl
				+ "' and version '" + resourceVersion + "'";
		assertEquals(errorActivityDefinition, 1, activityDefinitions.size());

		String errorMessageRequester = "ActivityDefinition with url '" + processUrl + "' and version '"
				+ RESOURCE_VERSION + "' is missing expected requester extension";
		assertEquals(errorMessageRequester, 1, activityDefinitions.get(0).getExtension().stream()
				.filter(e -> "http://dsf.dev/fhir/StructureDefinition/extension-process-authorization"
						.equals(e.getUrl()))
				.flatMap(e -> e.getExtension().stream()).filter(e -> "requester".equals(e.getUrl()))
				.map(Extension::getValue).filter(v -> v instanceof Coding).map(v -> (Coding) v)
				.filter(c -> "http://dsf.dev/fhir/CodeSystem/process-authorization".equals(c.getSystem()))
				.filter(c -> "REMOTE_ORGANIZATION".equals(c.getCode())).flatMap(c -> c.getExtension().stream())
				.filter(e -> "http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization"
						.equals(e.getUrl()))
				.map(Extension::getValue).filter(v -> v instanceof Identifier).map(i -> (Identifier) i)
				.filter(i -> "http://dsf.dev/sid/organization-identifier".equals(i.getSystem()))
				.filter(i -> "dic.dsf.test".equals(i.getValue())).count());

		String errorMessageRecipient = "ActivityDefinition with url '" + processUrl + "' and version '"
				+ RESOURCE_VERSION + "' is missing expected recipient extension";
		assertEquals(errorMessageRecipient, 1, activityDefinitions.get(0).getExtension().stream()
				.filter(e -> "http://dsf.dev/fhir/StructureDefinition/extension-process-authorization"
						.equals(e.getUrl()))
				.flatMap(e -> e.getExtension().stream()).filter(e -> "recipient".equals(e.getUrl()))
				.map(Extension::getValue).filter(v -> v instanceof Coding).map(v -> (Coding) v)
				.filter(c -> "http://dsf.dev/fhir/CodeSystem/process-authorization".equals(c.getSystem()))
				.filter(c -> "LOCAL_ORGANIZATION".equals(c.getCode())).flatMap(c -> c.getExtension().stream())
				.filter(e -> "http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization"
						.equals(e.getUrl()))
				.map(Extension::getValue).filter(v -> v instanceof Identifier).map(i -> (Identifier) i)
				.filter(i -> "http://dsf.dev/sid/organization-identifier".equals(i.getSystem()))
				.filter(i -> "cos.dsf.test".equals(i.getValue())).count());

		String taskHelloCosUrl = "http://dsf.dev/fhir/StructureDefinition/task-hello-cos";
		List<StructureDefinition> structureDefinitions = cosFhirResources.stream()
				.filter(r -> r instanceof StructureDefinition).map(r -> (StructureDefinition) r)
				.filter(s -> taskHelloCosUrl.equals(s.getUrl())).filter(s -> resourceVersion.equals(s.getVersion()))
				.collect(Collectors.toList());

		String errorStructureDefinition = "Process is missing StructureDefinition with url '" + taskHelloCosUrl
				+ "' and version '" + resourceVersion + "'";
		assertEquals(errorStructureDefinition, 1, structureDefinitions.size());

		assertEquals(2, cosFhirResources.size());
	}
}
