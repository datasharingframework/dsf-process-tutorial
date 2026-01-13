package dev.dsf.process.tutorial.exercise_6;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_COS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_HRP;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_HRP;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_HRP_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
import dev.dsf.process.tutorial.message.HelloHrpMessage;
import dev.dsf.process.tutorial.service.CosTask;
import dev.dsf.process.tutorial.util.FhirResourceLoader;

public class TutorialProcessPluginDefinitionTest
{
	private static final ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();

	private final String resourceVersion = "1.4";
	private static List<Resource> dicFhirResources;
	private static List<Resource> cosFhirResources;
	private static List<Resource> hrpFhirResources;

	@BeforeClass
	public static void loadResources() throws Exception
	{
		dicFhirResources = FhirResourceLoader.loadResourcesFor(definition, PROCESS_NAME_FULL_DIC);
		cosFhirResources = FhirResourceLoader.loadResourcesFor(definition, PROCESS_NAME_FULL_COS);
		hrpFhirResources = FhirResourceLoader.loadResourcesFor(definition, PROCESS_NAME_FULL_HRP);
	}

	@Test
	public void testPluginVersion()
	{
		assertEquals(resourceVersion, definition.getResourceVersion());
	}

	@Test
	public void testGetBpmnFiles() throws Exception
	{
		var bpmnFiles = definition.getProcessModels();
		assertNotNull(bpmnFiles);
		assertEquals(3, bpmnFiles.size());
	}

	@Test
	public void testGetBpmnFilesCos() throws Exception
	{
		var bpmnFiles = definition.getProcessModels();
		assumeNotNull(bpmnFiles);
		assertTrue(bpmnFiles.stream().anyMatch("bpe/cos-process.bpmn"::equals));
	}

	@Test
	public void testGetBpmnFilesDic() throws Exception
	{
		var bpmnFiles = definition.getProcessModels();
		assumeNotNull(bpmnFiles);
		assertTrue(bpmnFiles.stream().anyMatch("bpe/dic-process.bpmn"::equals));
	}

	@Test
	public void testGetBpmnFilesHrp() throws Exception
	{
		var bpmnFiles = definition.getProcessModels();
		assumeNotNull(bpmnFiles);
		assertTrue(bpmnFiles.stream().anyMatch("bpe/hrp-process.bpmn"::equals));
	}

	@Test
	public void testGetResourceProviderCos() throws Exception
	{
		assertNotNull(cosFhirResources);
		assertEquals(4, cosFhirResources.size());

		long aCount = cosFhirResources.stream().filter(r -> r instanceof ActivityDefinition)
				.map(r -> (ActivityDefinition) r).filter(a -> "http://dsf.dev/bpe/Process/cosProcess".equals(a.getUrl())
						&& resourceVersion.equals(a.getVersion()))
				.count();
		assertEquals(1, aCount);

		long cCount = cosFhirResources.stream().filter(r -> r instanceof CodeSystem).map(r -> (CodeSystem) r)
				.filter(c -> "http://dsf.dev/fhir/CodeSystem/tutorial".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, cCount);

		long tCount = cosFhirResources.stream().filter(r -> r instanceof StructureDefinition)
				.map(r -> (StructureDefinition) r)
				.filter(c -> "http://dsf.dev/fhir/StructureDefinition/task-hello-cos".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, tCount);

		long vCount = cosFhirResources.stream().filter(r -> r instanceof ValueSet).map(r -> (ValueSet) r)
				.filter(v -> "http://dsf.dev/fhir/ValueSet/tutorial".equals(v.getUrl())
						&& resourceVersion.equals(v.getVersion()))
				.count();
		assertEquals(1, vCount);
	}

	@Test
	public void testGetResourceProviderDic() throws Exception
	{
		String structureDefinitionUrl = ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS;
		assertNotNull(dicFhirResources);

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

		long aCount = dicFhirResources.stream().filter(r -> r instanceof ActivityDefinition)
				.map(r -> (ActivityDefinition) r).filter(a -> "http://dsf.dev/bpe/Process/dicProcess".equals(a.getUrl())
						&& resourceVersion.equals(a.getVersion()))
				.count();
		assertEquals(1, aCount);

		long cCount = dicFhirResources.stream().filter(r -> r instanceof CodeSystem).map(r -> (CodeSystem) r)
				.filter(c -> "http://dsf.dev/fhir/CodeSystem/tutorial".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, cCount);

		long t1Count = dicFhirResources.stream().filter(r -> r instanceof StructureDefinition)
				.map(r -> (StructureDefinition) r)
				.filter(c -> "http://dsf.dev/fhir/StructureDefinition/task-goodbye-dic".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, t1Count);


		String errorStructureDefinition = "Process is missing StructureDefinition with url '" + structureDefinitionUrl
				+ "'";
		Optional<StructureDefinition> optionalStructureDefinition = dicFhirResources.stream()
				.filter(resource -> resource instanceof StructureDefinition)
				.map(resource -> (StructureDefinition) resource)
				.filter(structureDefinition -> structureDefinition.getUrl().equals(structureDefinitionUrl)).findFirst();

		long t2Count = dicFhirResources.stream().filter(r -> r instanceof StructureDefinition)
				.map(r -> (StructureDefinition) r)
				.filter(c -> structureDefinitionUrl.equals(c.getUrl()) && resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, t2Count);

		assertTrue(errorStructureDefinition, optionalStructureDefinition.isPresent());

		StructureDefinition correctStructureDefinition = optionalStructureDefinition.get();
		String errorNotEnoughInputsAllowed = "StructureDefinition with url " + correctStructureDefinition.getUrl()
				+ " has 'Task.input.max' with value 2. Since you added a new input parameter in exercise 2 you need to increase this value to 3.";
		assertTrue(errorNotEnoughInputsAllowed,
				correctStructureDefinition.getDifferential().getElement().stream()
						.filter(elementDefinition -> elementDefinition.getId().equals("Task.input"))
						.anyMatch(elementDefinition -> Integer.valueOf(elementDefinition.getMax()).equals(3)));

		long vCount = dicFhirResources.stream().filter(r -> r instanceof ValueSet).map(r -> (ValueSet) r)
				.filter(v -> "http://dsf.dev/fhir/ValueSet/tutorial".equals(v.getUrl())
						&& resourceVersion.equals(v.getVersion()))
				.count();
		assertEquals(1, vCount);
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
	public void testGetResourceProviderDicActivityDefinitionDicProcess() throws Exception
	{
		assumeNotNull(dicFhirResources);

		var aOpt = dicFhirResources.stream().filter(r -> r instanceof ActivityDefinition)
				.map(r -> (ActivityDefinition) r).filter(a -> "http://dsf.dev/bpe/Process/dicProcess".equals(a.getUrl())
						&& resourceVersion.equals(a.getVersion()))
				.findFirst();
		assumeTrue(aOpt.isPresent());

		var a = aOpt.get();
		var pAuthExts = a.getExtensionsByUrl("http://dsf.dev/fhir/StructureDefinition/extension-process-authorization");
		assertNotNull(pAuthExts);
		assertEquals(2, pAuthExts.size());
	}

	@Test
	public void testGetResourceProviderHrp() throws Exception
	{
		assertNotNull(hrpFhirResources);
		assertEquals(4, hrpFhirResources.size());

		long aCount = hrpFhirResources.stream().filter(r -> r instanceof ActivityDefinition)
				.map(r -> (ActivityDefinition) r).filter(a -> "http://dsf.dev/bpe/Process/hrpProcess".equals(a.getUrl())
						&& resourceVersion.equals(a.getVersion()))
				.count();
		assertEquals(1, aCount);

		long cCount = hrpFhirResources.stream().filter(r -> r instanceof CodeSystem).map(r -> (CodeSystem) r)
				.filter(c -> "http://dsf.dev/fhir/CodeSystem/tutorial".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, cCount);

		long tCount = hrpFhirResources.stream().filter(r -> r instanceof StructureDefinition)
				.map(r -> (StructureDefinition) r)
				.filter(c -> "http://dsf.dev/fhir/StructureDefinition/task-hello-hrp".equals(c.getUrl())
						&& resourceVersion.equals(c.getVersion()))
				.count();
		assertEquals(1, tCount);

		long vCount = hrpFhirResources.stream().filter(r -> r instanceof ValueSet).map(r -> (ValueSet) r)
				.filter(v -> "http://dsf.dev/fhir/ValueSet/tutorial".equals(v.getUrl())
						&& resourceVersion.equals(v.getVersion()))
				.count();
		assertEquals(1, vCount);
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

		Process process = processes.get(0);

		List<MessageEventDefinition> intermediateMessageThrowEvent = process
				.getChildElementsByType(IntermediateThrowEvent.class).stream().filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorIntermediateMessageThrowEvent = "Process '" + processId + "' in file '" + filename
				+ "' is missing an IntermediateMessageThrowEvent";
		assertEquals(errorIntermediateMessageThrowEvent, 1, intermediateMessageThrowEvent.size());

		String errorIntermediateMessageThrowEventMessageName = "Process '" + processId + "' in file '" + filename
				+ "' is missing a IntermediateMessageThrowEvent with message name 'helloCos'";
		assertEquals(errorIntermediateMessageThrowEventMessageName, "helloCos",
				intermediateMessageThrowEvent.get(0).getMessage().getName());

		long eventBasedGatewayCount = processes.get(0).getChildElementsByType(EventBasedGateway.class).stream()
				.filter(Objects::nonNull).count();
		String errorEventBasedGatewayCount = "Process '" + processId + "' in file '" + filename
				+ "' is missing an EventBasedGateway";
		assertEquals(errorEventBasedGatewayCount, 1, eventBasedGatewayCount);

		List<MessageEventDefinition> intermediateMessageCatchEvent = processes.get(0)
				.getChildElementsByType(IntermediateCatchEvent.class).stream().filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorIntermediateMessageCatchEvent = "Process '" + processId + "' in file '" + filename
				+ "' is missing an IntermediateMessageCatchEvent";
		assertEquals(errorIntermediateMessageCatchEvent, 1, intermediateMessageCatchEvent.size());

		String errorIntermediateMessageCatchEventMessageName = "Process '" + processId + "' in file '" + filename
				+ "' is missing a IntermediateMessageCatchEvent with message name 'goodbyeDic'";
		assertEquals(errorIntermediateMessageCatchEventMessageName, "goodbyeDic",
				intermediateMessageCatchEvent.get(0).getMessage().getName());

		List<TimerEventDefinition> intermediateTimerCatchEvent = processes.get(0)
				.getChildElementsByType(IntermediateCatchEvent.class).stream().filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(TimerEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorIntermediateTimerCatchEvent = "Process '" + processId + "' in file '" + filename
				+ "' is missing an IntermediateTimerCatchEvent";
		assertEquals(errorIntermediateTimerCatchEvent, 1, intermediateTimerCatchEvent.size());

		assertNotNull(intermediateTimerCatchEvent.get(0).getTimeDuration());
		assertNotNull(intermediateTimerCatchEvent.get(0).getTimeDuration().getTextContent());
		String errorIntermediateTimerCatchEventTimerDefinition = "Process '" + processId + "' in file '" + filename
				+ "' is missing a IntermediateTimerCatchEvent with timer definition";
		assertTrue(errorIntermediateTimerCatchEventTimerDefinition,
				intermediateTimerCatchEvent.get(0).getTimeDuration().getTextContent().startsWith("P"));
	}

	@Test
	public void testCosProcessBpmnProcessFile() throws Exception
	{
		String filename = "bpe/cos-process.bpmn";
		String processId = "dsfdev_cosProcess";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		String errorServiceTask = "Process '" + processId + "' in file '" + filename
				+ "' is missing implementation of class '" + CosTask.class.getName() + "'";
		assertTrue(errorServiceTask, processes.get(0).getChildElementsByType(ServiceTask.class).stream()
				.filter(Objects::nonNull).map(ServiceTask::getCamundaClass).anyMatch(CosTask.class.getName()::equals));

		List<MessageEventDefinition> messageEndEvent = processes.get(0).getChildElementsByType(EndEvent.class).stream()
				.filter(Objects::nonNull)
				.flatMap(e -> e.getChildElementsByType(MessageEventDefinition.class).stream().filter(Objects::nonNull))
				.collect(Collectors.toList());

		String errorMessageEndEvent = "Process '" + processId + "' in file '" + filename
				+ "' should end with a MessageEndEvent";
		assertEquals(errorMessageEndEvent, 1, messageEndEvent.size());

		String errorMessageEndEventImplementation = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent java implementation class '" + HelloHrpMessage.class.getName() + "'";
		assertEquals(errorMessageEndEventImplementation, HelloHrpMessage.class.getName(),
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
				+ PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI + "|#{version}'";
		assertTrue(errorMessageEndEventInputUri,
				camundaFields.stream().anyMatch(i -> "instantiatesCanonical".equals(i.getCamundaName())
						&& (PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI + "|#{version}").equals(i.getTextContent())));

		String errorMessageEndEventMessageName = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent field injection with name 'messageName' and value '"
				+ PROFILE_TUTORIAL_TASK_HELLO_HRP_MESSAGE_NAME + "'";
		assertTrue(errorMessageEndEventMessageName,
				camundaFields.stream().anyMatch(i -> "messageName".equals(i.getCamundaName())
						&& PROFILE_TUTORIAL_TASK_HELLO_HRP_MESSAGE_NAME.equals(i.getTextContent())));

		String errorMessageEndEventProfile = "Process '" + processId + "' in file '" + filename
				+ "' is missing a MessageEndEvent field injection with name 'profile' and value '"
				+ PROFILE_TUTORIAL_TASK_HELLO_HRP + "|#{version}'";
		assertTrue(errorMessageEndEventProfile,
				camundaFields.stream().anyMatch(i -> "profile".equals(i.getCamundaName())
						&& (PROFILE_TUTORIAL_TASK_HELLO_HRP + "|#{version}").equals(i.getTextContent())));
	}

	@Test
	public void testHrpProcessBpmnProcessFile() throws Exception
	{
		String filename = "bpe/hrp-process.bpmn";
		String processId = "dsfdev_hrpProcess";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());

		String errorProcessDefinitionKey = "Process in file '" + filename + "' is missing process definition key '"
				+ processId + "'";
		assertEquals(errorProcessDefinitionKey, 1, processes.size());

		String errorProcessVersion = "Process '" + processId + "' in file '" + filename
				+ "' is missing version tag '#{version}'";
		assertEquals(errorProcessVersion, "#{version}", processes.get(0).getCamundaVersionTag());
	}
}
