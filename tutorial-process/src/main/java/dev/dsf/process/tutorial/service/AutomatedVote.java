package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTED_AUTOMATICALLY;

import java.util.Random;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;

public class AutomatedVote extends AbstractServiceDelegate
{
	public AutomatedVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		variables.setBoolean(VOTE_PROCESS_VARIABLE_VOTED_AUTOMATICALLY, true);
		variables.setBoolean(VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE, new Random().nextBoolean());
	}
}
