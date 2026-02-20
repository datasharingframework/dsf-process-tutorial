package org.tutorial.process.tutorial.service;

import java.util.UUID;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Targets;
import dev.dsf.bpe.v2.variables.Variables;

public class SelectTargets implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Targets targets = variables.createTargets(
				variables.createTarget("dic.dsf.test", "dic.dsf.test_Endpoint", "https://dic/fhir",
						UUID.randomUUID().toString()),
				variables.createTarget("cos.dsf.test", "cos.dsf.test_Endpoint", "https://cos/fhir",
						UUID.randomUUID().toString()),
				variables.createTarget("hrp.dsf.test", "hrp.dsf.test_Endpoint", "https://hrp/fhir",
						UUID.randomUUID().toString()));
		variables.setTargets(targets);
	}
}
