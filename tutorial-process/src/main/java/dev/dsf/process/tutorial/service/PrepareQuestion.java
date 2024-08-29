package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_USER_VOTE;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;

public class PrepareQuestion extends AbstractServiceDelegate
{

	private boolean userVote;
	public PrepareQuestion(ProcessPluginApi api, boolean userVote)
	{
		super(api);
		this.userVote = userVote;
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Optional<String> question = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(), ConstantsTutorial.CODESYSTEM_TUTORIAL, ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);
		question.ifPresent(q -> variables.setString(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION, q));

		variables.setBoolean(VOTE_PROCESS_VARIABLE_USER_VOTE, userVote);
	}
}
