package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;

import java.util.stream.Stream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractTaskMessageSend;
import dev.dsf.bpe.v1.variables.Variables;

public class StartVote extends AbstractTaskMessageSend
{
	public StartVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected Stream<Task.ParameterComponent> getAdditionalInputParameters(DelegateExecution execution,
			Variables variables)
	{
		String question = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(), CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION).get();
		Task.ParameterComponent questionComponent = api.getTaskHelper().createInput(new StringType(question), CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION);

		return Stream.of(questionComponent);
	}
}
