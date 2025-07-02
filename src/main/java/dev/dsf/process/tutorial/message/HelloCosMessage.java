package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.MessageIntermediateThrowEvent;
import dev.dsf.bpe.v2.activity.values.SendTaskValues;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

// Only needed for exercise 4 and above
public class HelloCosMessage implements MessageIntermediateThrowEvent
{
	@Override
	public List<Task.ParameterComponent> getAdditionalInputParameters(ProcessPluginApi api, Variables variables,
			SendTaskValues sendTaskValues, Target target)
	{
		Optional<String> tutorialInputParameter = api.getTaskHelper().getFirstInputParameterStringValue(
				variables.getStartTask(), CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT);

		return tutorialInputParameter.map(s -> List.of(api.getTaskHelper().createInput(new StringType().setValue(s),
				CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT))).orElse(List.of());
	}
}
