package dev.dsf.process.tutorial.exercise_2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.Test;

import dev.dsf.bpe.plugin.ProcessIdAndVersion;
import dev.dsf.bpe.v1.ProcessPluginDefinition;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TestProcessPluginGenerator;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
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

		var fhirResources = processPlugin.getFhirResources();

		List<Resource> dicProcessResources = fhirResources.get(new ProcessIdAndVersion(
				ConstantsTutorial.PROCESS_NAME_FULL_DIC, definition.getResourceVersion()));

		Map<String, List<String>> dicProcess = definition.getFhirResourcesByProcessId();

		int numberEntries = dicProcess.size();
		String errorTooManyEntries = "Too many processes in Map. Got " + numberEntries + " entries. Expected 1.";
		assertEquals(errorTooManyEntries, 1, numberEntries);

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
}
