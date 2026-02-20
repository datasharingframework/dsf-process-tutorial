package org.tutorial.process.tutorial.service;

import static org.tutorial.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_SHOULD_USER_VOTE;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Variables;

public class DecideWhetherUserVote implements ServiceTask
{

	private boolean userVote;

	public DecideWhetherUserVote(boolean userVote)
	{
		this.userVote = userVote;
	}

	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		variables.setBoolean(VOTE_PROCESS_VARIABLE_SHOULD_USER_VOTE, userVote);
	}
}
