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
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.Test;

import dev.dsf.bpe.plugin.ProcessIdAndVersion;
import dev.dsf.bpe.v1.ProcessPluginDefinition;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TestProcessPluginGenerator;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;
import dev.dsf.process.tutorial.service.HelloDic;

public class TutorialProcessPluginDefinitionTest
{

	@Test
	public void testHelloDicBpmnProcessFile() throws Exception
	{
		String filename = "bpe/hello-dic.bpmn";
		String processId = "dsfdev_helloDic";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		String errorServiceTask = "Process '" + processId + "' in file '" + filename
				+ "' is missing implementation of class '" + HelloDic.class.getName() + "'";
		assertTrue(errorServiceTask, processes.get(0).getChildElementsByType(ServiceTask.class).stream()
				.filter(Objects::nonNull).map(ServiceTask::getCamundaClass).anyMatch(HelloDic.class.getName()::equals));
	}

	@Test
	public void testHelloDicResources() throws Exception
	{
		String codeSystemUrl = "http://dsf.dev/fhir/CodeSystem/tutorial";
		String codeSystemCode = "tutorial-input";
		String valueSetUrl = "http://dsf.dev/fhir/ValueSet/tutorial";

		String codeSystemFile = "fhir/CodeSystem/tutorial.xml";
		String valueSetFile = "fhir/CodeSystem/tutorial.xml";


		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		ProcessPluginImpl processPlugin = TestProcessPluginGenerator.generate(definition, false, getClass());
		boolean initialized = processPlugin
				.initializeAndValidateResources(ConstantsTutorial.TUTORIAL_DIC_ORGANIZATION_IDENTIFIER);

		assertEquals(true, initialized);

		var fhirResources = processPlugin.getFhirResources();

		List<Resource> helloDicResources = fhirResources.get(new ProcessIdAndVersion(
				ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC, definition.getResourceVersion()));

		Map<String, List<String>> helloDic = definition.getFhirResourcesByProcessId();

		int numberEntries = helloDic.size();
		String errorTooManyEntries = "Too many processes in Map. Got " + numberEntries + " entries. Expected 1.";
		assertEquals(errorTooManyEntries, 1, numberEntries);

		String firstKey = helloDic.keySet().stream().findFirst().get();
		String errorFaultyProcessName = "Process name is either wrong or missing. Expected '"
				+ ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC + "' but got '" + firstKey + "'";
		assertEquals(errorFaultyProcessName, ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC, firstKey);

		String errorCodeSystem = "Process is missing CodeSystem with file name '" + codeSystemFile + "'";
		assertEquals(errorCodeSystem, 1, helloDic.get(ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC).stream()
				.filter(r -> codeSystemFile.equals(r)).count());

		String errorValueSet = "Process is missing ValueSet with file name '" + valueSetFile + "'";
		assertEquals(errorValueSet, 1, helloDic.get(ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC).stream()
				.filter(r -> valueSetFile.equals(r)).count());

		errorCodeSystem = "Process is missing CodeSystem with url '" + codeSystemUrl + "' and concept '"
				+ codeSystemCode + "' with type 'string'";
		assertEquals(errorCodeSystem, 1, helloDicResources.stream().filter(r -> r instanceof CodeSystem)
				.map(r -> (CodeSystem) r).filter(c -> codeSystemUrl.equals(c.getUrl()))
				.filter(c -> c.getConcept().stream().anyMatch(con -> codeSystemCode.equals(con.getCode()))).count());

		errorValueSet = "Process is missing ValueSet with url '" + valueSetUrl + "'";
		assertEquals(errorValueSet, 1, helloDicResources.stream().filter(r -> r instanceof ValueSet)
				.map(r -> (ValueSet) r).filter(v -> valueSetUrl.equals(v.getUrl()))
				.filter(v -> v.getCompose().getInclude().stream().anyMatch(i -> codeSystemUrl.equals(i.getSystem())))
				.count());

		assertEquals(4, helloDic.get(ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC).size());
	}
}
