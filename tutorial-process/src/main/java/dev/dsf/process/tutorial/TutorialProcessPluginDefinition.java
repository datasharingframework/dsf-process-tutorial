package dev.dsf.process.tutorial;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.process.tutorial.spring.config.TutorialConfig;

public class TutorialProcessPluginDefinition implements ProcessPluginDefinition
{
	public static final String VERSION = "2.1.0.0";
	public static final LocalDate RELEASE_DATE = LocalDate.of(2025, 4, 2);

	@Override
	public String getName()
	{
		return "dsf-process-tutorial";
	}

	@Override
	public String getVersion()
	{
		return VERSION;
	}

	@Override
	public LocalDate getReleaseDate()
	{
		return RELEASE_DATE;
	}

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
