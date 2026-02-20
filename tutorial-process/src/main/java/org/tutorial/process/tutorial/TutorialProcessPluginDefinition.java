package org.tutorial.process.tutorial;

import java.util.List;
import java.util.Map;

import org.tutorial.process.tutorial.spring.config.TutorialConfig;

import dev.dsf.bpe.v2.AbstractProcessPluginDefinition;

public class TutorialProcessPluginDefinition extends AbstractProcessPluginDefinition
{
	@Override
	public List<String> getProcessModels()
	{

		return List.of("bpe/dic-process.bpmn");
	}

	@Override
	public Map<String, List<String>> getFhirResourcesByProcessId()
	{

		String aDicProcess = "fhir/ActivityDefinition/dic-process.xml";
		String sTaskDicProcess = "fhir/StructureDefinition/task-start-dic-process.xml";
		String tTaskDicProcess = "fhir/Task/task-start-dic-process.xml";

		String cTutorial = "fhir/CodeSystem/tutorial.xml";
		String vTutorial = "fhir/ValueSet/tutorial.xml";

		return Map.of(ConstantsTutorial.PROCESS_NAME_FULL_DIC,
				List.of(aDicProcess, sTaskDicProcess, tTaskDicProcess, cTutorial, vTutorial));
	}

	@Override
	public List<Class<?>> getSpringConfigurations()
	{
		return List.of(TutorialConfig.class);
	}
}
