package org.tutorial.process.tutorial.service;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class CosTask implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Target target = variables.createTarget("hrp.dsf.test", "hrp.dsf.test_Endpoint", "https://hrp/fhir");
		variables.setTarget(target);
	}
}
