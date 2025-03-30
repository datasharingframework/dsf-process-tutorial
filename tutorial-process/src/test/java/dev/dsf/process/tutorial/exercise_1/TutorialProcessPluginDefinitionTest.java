package dev.dsf.process.tutorial.exercise_1;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI;
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
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Task;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.FhirResourceLoader;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
import dev.dsf.process.tutorial.service.DicTask;

public class TutorialProcessPluginDefinitionTest
{
	private final String version = "2.0.0.1";
	private final String resourceVersion = "2.0";

	private static List<Resource> dicFhirResources;

	@BeforeClass
	public static void loadResources()
	{
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();

		dicFhirResources = FhirResourceLoader.loadResourcesFor(definition, ConstantsTutorial.PROCESS_NAME_FULL_DIC);
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
				+ "' is missing a ServiceTask with java implementation class '" + DicTask.class.getName() + "'";
		assertTrue(errorServiceTask, processes.get(0).getChildElementsByType(ServiceTask.class).stream()
				.filter(Objects::nonNull).map(ServiceTask::getCamundaClass).anyMatch(DicTask.class.getName()::equals));
	}

	@Test
	public void testDicProcessResources() throws Exception
	{
		String structureDefinitionUrl = "http://dsf.dev/fhir/StructureDefinition/task-start-dic-process";

		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		Map<String, List<String>> dicProcess = definition.getFhirResourcesByProcessId();

		int numberEntries = dicProcess.size();
		int expectedEntries = 1;
		String errorTooManyEntries = "Too many processes in Map. Got " + numberEntries + " entries. Expected "
				+ expectedEntries + ".";
		assertEquals(errorTooManyEntries, expectedEntries, numberEntries);

		String dicProcessKey = dicProcess.keySet().stream()
				.filter(k -> k.equals(ConstantsTutorial.PROCESS_NAME_FULL_DIC)).findFirst().get();
		String errorFaultyProcessName = "Process name is either wrong or missing. Expected '"
				+ ConstantsTutorial.PROCESS_NAME_FULL_DIC + "' but got '" + dicProcessKey
				+ "' from TutorialProcessPluginDefinition#getFhirResourcesByProcessId";
		assertEquals(errorFaultyProcessName, ConstantsTutorial.PROCESS_NAME_FULL_DIC, dicProcessKey);

		String errorStructureDefinition = "Process is missing StructureDefinition with url '" + structureDefinitionUrl
				+ "'";
		Optional<StructureDefinition> optionalStructureDefinition = dicFhirResources.stream()
				.filter(resource -> resource instanceof StructureDefinition)
				.map(resource -> (StructureDefinition) resource)
				.filter(structureDefinition -> structureDefinition.getUrl().equals(structureDefinitionUrl)).findFirst();

		assertTrue(errorStructureDefinition, optionalStructureDefinition.isPresent());

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

		String instantiatesCanonical = PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL;
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
	}

	private List<Task> getDraftTasks(List<Resource> resources)
	{
		return resources.stream().filter(resource -> resource instanceof Task).map(resource -> (Task) resource)
				.toList();
	}
}
