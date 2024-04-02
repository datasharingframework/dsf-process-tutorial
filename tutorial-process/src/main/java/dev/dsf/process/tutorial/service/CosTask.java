package dev.dsf.process.tutorial.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;

public class CosTask extends AbstractServiceDelegate
{
	public CosTask(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables)
	{
		Target target = variables.createTarget("hrp.dsf.test", "hrp.dsf.test_Endpoint", "https://hrp/fhir");
		variables.setTarget(target);
	}
}
