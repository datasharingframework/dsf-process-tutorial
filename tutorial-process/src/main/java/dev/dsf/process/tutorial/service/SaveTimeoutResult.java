package dev.dsf.process.tutorial.service;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.util.VoteResponse;

public class SaveTimeoutResult extends AbstractServiceDelegate
{
	public SaveTimeoutResult(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Target target = variables.getTarget();
		variables.setString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey(), VoteResponse.TIMEOUT.name());
	}
}
