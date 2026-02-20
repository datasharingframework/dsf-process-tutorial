package org.tutorial.process.tutorial.message;

import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;

import java.util.List;

import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.MessageIntermediateThrowEvent;
import dev.dsf.bpe.v2.activity.values.SendTaskValues;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class StartVote implements MessageIntermediateThrowEvent
{
	@Override
	public List<Task.ParameterComponent> getAdditionalInputParameters(ProcessPluginApi api, Variables variables,
			SendTaskValues sendTaskValues, Target target)
	{
		String question = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION).get();
		String resourceVersion = api.getProcessPluginDefinition().getResourceVersion();
		Task.ParameterComponent questionComponent = api.getTaskHelper().createInput(new StringType(question),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION, resourceVersion);

		return List.of(questionComponent);
	}
}
