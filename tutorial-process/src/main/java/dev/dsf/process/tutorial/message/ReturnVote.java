package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTE;

import java.util.List;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.ParameterComponent;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.MessageSendTask;
import dev.dsf.bpe.v2.activity.values.SendTaskValues;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class ReturnVote implements MessageSendTask
{
	@Override
	public List<ParameterComponent> getAdditionalInputParameters(ProcessPluginApi api, Variables variables,
			SendTaskValues sendTaskValues, Target target)
	{
		boolean vote = variables.getBoolean(VOTE_PROCESS_VARIABLE_VOTE);
		Task.ParameterComponent voteComponent = api.getTaskHelper().createInput(new BooleanType(vote),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VOTE);

		return List.of(voteComponent);
	}
}