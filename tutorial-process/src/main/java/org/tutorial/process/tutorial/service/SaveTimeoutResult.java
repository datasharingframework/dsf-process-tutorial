package dev.dsf.process.tutorial.service;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.process.tutorial.util.VoteResponse;

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
