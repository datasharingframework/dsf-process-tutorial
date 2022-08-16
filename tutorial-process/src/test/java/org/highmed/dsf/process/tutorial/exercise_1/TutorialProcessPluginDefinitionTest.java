package org.highmed.dsf.process.tutorial.exercise_1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.highmed.dsf.bpe.ProcessPluginDefinition;
import org.highmed.dsf.fhir.resources.ResourceProvider;
import org.highmed.dsf.process.tutorial.ConstantsTutorial;
import org.highmed.dsf.process.tutorial.TutorialProcessPluginDefinition;
import org.highmed.dsf.process.tutorial.service.HelloDic;
import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;

import ca.uhn.fhir.context.FhirContext;

public class TutorialProcessPluginDefinitionTest
{
	@Test
	public void testHelloDicBpmnProcessFile() throws Exception
	{
		String filename = "bpe/hello-dic.bpmn";
		String processId = "highmedorg_helloDic";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		String errorServiceTask = "Process '" + processId + "' in file '" + filename
				+ "' is missing a ServiceTask with java implementation class '" + HelloDic.class.getName() + "'";
		assertTrue(errorServiceTask, processes.get(0).getChildElementsByType(ServiceTask.class).stream()
				.filter(Objects::nonNull).map(ServiceTask::getCamundaClass).anyMatch(HelloDic.class.getName()::equals));
	}

	@Test
	public void testHelloDicResources() throws Exception
	{
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		ResourceProvider provider = definition.getResourceProvider(FhirContext.forR4(), getClass().getClassLoader(),
				new StandardEnvironment());
		assertNotNull(provider);

		var helloDic = provider.getResources(
				ConstantsTutorial.PROCESS_NAME_FULL_HELLO_DIC + "/" + TutorialProcessPluginDefinition.VERSION,
				s -> ResourceProvider.empty());
		assertNotNull(helloDic);
		assertEquals(2, helloDic.count());
	}
}
