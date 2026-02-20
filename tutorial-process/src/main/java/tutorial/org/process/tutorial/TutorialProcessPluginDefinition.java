package tutorial.org.process.tutorial;

import java.util.List;
import java.util.Map;

import dev.dsf.bpe.v2.AbstractProcessPluginDefinition;
import tutorial.org.process.tutorial.spring.config.TutorialConfig;

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

		return Map.of(ConstantsTutorial.PROCESS_NAME_FULL_DIC, List.of(aDicProcess, sTaskDicProcess));
	}

	@Override
	public List<Class<?>> getSpringConfigurations()
	{
		return List.of(TutorialConfig.class);
	}
}
