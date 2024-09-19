package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_SHOULD_USER_VOTE;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;

public class DecideWhetherUserVote extends AbstractServiceDelegate
{

	private boolean userVote;

	public DecideWhetherUserVote(ProcessPluginApi api, boolean userVote)
	{
		super(api);
		this.userVote = userVote;
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		variables.setBoolean(VOTE_PROCESS_VARIABLE_SHOULD_USER_VOTE, userVote);
	}
}
