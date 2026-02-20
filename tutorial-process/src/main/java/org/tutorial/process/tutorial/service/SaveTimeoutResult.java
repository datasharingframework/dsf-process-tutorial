package org.tutorial.process.tutorial.service;

import org.tutorial.process.tutorial.VoteResponse;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class SaveTimeoutResult implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Target target = variables.getTarget();
		variables.setString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey(),
				VoteResponse.TIMEOUT.name());
	}
}
