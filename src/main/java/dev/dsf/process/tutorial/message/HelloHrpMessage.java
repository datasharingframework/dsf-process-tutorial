package dev.dsf.process.tutorial.message;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.MessageEndEvent;
import dev.dsf.bpe.v2.activity.values.SendTaskValues;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

// Only needed for exercise 6 and above
public class HelloHrpMessage implements MessageEndEvent
{
	@Override
	public List<Task.ParameterComponent> getAdditionalInputParameters(ProcessPluginApi api, Variables variables,
			SendTaskValues sendTaskValues, Target target)
	{
		Optional<Task.ParameterComponent> tutorialInputParameter = api.getTaskHelper().getFirstInputParameter(
				variables.getStartTask(), "http://dsf.dev/fhir/CodeSystem/tutorial", "tutorial-input",
				StringType.class);

		return tutorialInputParameter.map(i -> List.of(api.getTaskHelper().createInput(i.getValue(),
				"http://dsf.dev/fhir/CodeSystem/tutorial", "tutorial-input"))).orElse(List.of());
	}
}
