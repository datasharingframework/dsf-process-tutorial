package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTE;

import java.util.stream.Stream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.ParameterComponent;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractTaskMessageSend;
import dev.dsf.bpe.v1.variables.Variables;

public class ReturnVote extends AbstractTaskMessageSend
{
	public ReturnVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected Stream<ParameterComponent> getAdditionalInputParameters(DelegateExecution execution, Variables variables)
	{
		boolean vote = variables.getBoolean(VOTE_PROCESS_VARIABLE_VOTE);
		Task.ParameterComponent voteComponent = api.getTaskHelper().createInput(new BooleanType(vote),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VOTE);

		return Stream.of(voteComponent);
	}
}