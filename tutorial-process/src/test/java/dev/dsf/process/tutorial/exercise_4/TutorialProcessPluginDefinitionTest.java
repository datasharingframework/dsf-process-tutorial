package dev.dsf.process.tutorial.exercise_4;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_COS_PROCESS_URI;
import static dev.dsf.process.tutorial.ConstantsTutorial.RESOURCE_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.Test;

import dev.dsf.bpe.plugin.ProcessIdAndVersion;
import dev.dsf.bpe.v1.ProcessPluginDefinition;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TestProcessPluginGenerator;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
import dev.dsf.process.tutorial.message.HelloCosMessage;
import dev.dsf.process.tutorial.service.DicTask;

public class TutorialProcessPluginDefinitionTest
{
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
		String codeSystemUrl = "http://dsf.dev/fhir/CodeSystem/tutorial";
		String codeSystemCode = "tutorial-input";
		String valueSetUrl = "http://dsf.dev/fhir/ValueSet/tutorial";

		String codeSystemFile = "fhir/CodeSystem/tutorial.xml";
		String valueSetFile = "fhir/ValueSet/tutorial.xml";
		String draftTaskFile = "fhir/Task/task-start-dic-process.xml";

		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		ProcessPluginImpl processPlugin = TestProcessPluginGenerator.generate(definition, false, getClass());
		boolean initialized = processPlugin
				.initializeAndValidateResources(ConstantsTutorial.TUTORIAL_DIC_ORGANIZATION_IDENTIFIER);

		assertEquals(true, initialized);

		Map<String, List<String>> dicProcess = definition.getFhirResourcesByProcessId();

		int numberEntries = dicProcess.size();
		String errorTooManyEntries = "Too many processes in Map. Got " + numberEntries + " entries. Expected 1.";
		assertEquals(errorTooManyEntries, 2, numberEntries);

		String dicProcessKey = dicProcess.keySet().stream()
				.filter(k -> k.equals(ConstantsTutorial.PROCESS_NAME_FULL_DIC)).findFirst().get();
		String errorFaultyProcessName = "Process name is either wrong or missing. Expected '"
				+ ConstantsTutorial.PROCESS_NAME_FULL_DIC + "' but got '" + dicProcessKey + "'";
		assertEquals(errorFaultyProcessName, ConstantsTutorial.PROCESS_NAME_FULL_DIC, dicProcessKey);

		String errorCodeSystem = "Process is missing CodeSystem with file name '" + codeSystemFile + "'";
		assertEquals(errorCodeSystem, 1, dicProcess.get(ConstantsTutorial.PROCESS_NAME_FULL_DIC).stream()
				.filter(r -> codeSystemFile.equals(r)).count());

		String errorValueSet = "Process is missing ValueSet with file name '" + valueSetFile + "'";
		assertEquals(errorValueSet, 1, dicProcess.get(ConstantsTutorial.PROCESS_NAME_FULL_DIC).stream()
				.filter(r -> valueSetFile.equals(r)).count());

		List<Resource> dicProcessResources = processPlugin.getFhirResources().get(new ProcessIdAndVersion(
				ConstantsTutorial.PROCESS_NAME_FULL_DIC, definition.getResourceVersion()));

		errorCodeSystem = "Process is missing CodeSystem with url '" + codeSystemUrl + "' and concept '"
				+ codeSystemCode + "' with type 'string'";
		assertEquals(errorCodeSystem, 1, dicProcessResources.stream().filter(r -> r instanceof CodeSystem)
				.map(r -> (CodeSystem) r).filter(c -> codeSystemUrl.equals(c.getUrl()))
				.filter(c -> c.getConcept().stream().anyMatch(con -> codeSystemCode.equals(con.getCode()))).count());

		errorValueSet = "Process is missing ValueSet with url '" + valueSetUrl + "'";
		assertEquals(errorValueSet, 1, dicProcessResources.stream().filter(r -> r instanceof ValueSet)
				.map(r -> (ValueSet) r).filter(v -> valueSetUrl.equals(v.getUrl()))
				.filter(v -> v.getCompose().getInclude().stream().anyMatch(i -> codeSystemUrl.equals(i.getSystem())))
				.count());

		int numExpectedResources = 4;

		if (draftTaskExists(draftTaskFile))
		{
			numExpectedResources = 5;
			String errorDraftTask = "Process is missing Task resource with status 'draft'.";
			assertEquals(errorDraftTask, 1, dicProcessResources.stream().filter(r -> r instanceof Task).count());
		}

		assertEquals(numExpectedResources, dicProcessResources.size());
	}

	private boolean draftTaskExists(String draftTaskFile)
	{
		return Objects.nonNull(getClass().getClassLoader().getResourceAsStream(draftTaskFile));
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
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();

		ProcessPluginImpl processPlugin = TestProcessPluginGenerator.generate(definition, false, getClass());
		boolean initialized = processPlugin
				.initializeAndValidateResources(ConstantsTutorial.TUTORIAL_COS_ORGANIZATION_IDENTIFIER);

		assertEquals(true, initialized);

		List<Resource> cosProcess = processPlugin.getFhirResources().get(new ProcessIdAndVersion(
				ConstantsTutorial.PROCESS_NAME_FULL_COS, definition.getResourceVersion()));

		String processUrl = "http://dsf.dev/bpe/Process/cosProcess";
		List<ActivityDefinition> activityDefinitions = cosProcess.stream().filter(r -> r instanceof ActivityDefinition)
				.map(r -> (ActivityDefinition) r).filter(a -> processUrl.equals(a.getUrl()))
				.filter(a -> RESOURCE_VERSION.equals(a.getVersion())).collect(Collectors.toList());

		String errorActivityDefinition = "Process is missing ActivityDefinition with url '" + processUrl
				+ "' and version '" + RESOURCE_VERSION + "'";
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
				.filter(i -> "Test_DIC".equals(i.getValue())).count());

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
				.filter(i -> "Test_COS".equals(i.getValue())).count());

		String taskHelloCosUrl = "http://dsf.dev/fhir/StructureDefinition/task-hello-cos";
		List<StructureDefinition> structureDefinitions = cosProcess.stream().filter(r -> r instanceof StructureDefinition)
				.map(r -> (StructureDefinition) r).filter(s -> taskHelloCosUrl.equals(s.getUrl()))
				.filter(s -> RESOURCE_VERSION.equals(s.getVersion())).collect(Collectors.toList());

		String errorStructureDefinition = "Process is missing StructureDefinition with url '" + taskHelloCosUrl
				+ "' and version '" + RESOURCE_VERSION + "'";
		assertEquals(errorStructureDefinition, 1, structureDefinitions.size());

		assertEquals(2, cosProcess.size());
	}
}
