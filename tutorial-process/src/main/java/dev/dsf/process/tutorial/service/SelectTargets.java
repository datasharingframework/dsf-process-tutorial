package dev.dsf.process.tutorial.service;

import java.util.UUID;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Targets;
import dev.dsf.bpe.v1.variables.Variables;

public class SelectTargets extends AbstractServiceDelegate
{
	public SelectTargets(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Targets targets = variables.createTargets(
				variables.createTarget("dic.dsf.test", "dic.dsf.test_Endpoint", "https://dic/fhir", UUID.randomUUID().toString()),
				variables.createTarget("cos.dsf.test", "cos.dsf.test_Endpoint", "https://cos/fhir", UUID.randomUUID().toString()),
				variables.createTarget("hrp.dsf.test", "hrp.dsf.test_Endpoint", "https://hrp/fhir", UUID.randomUUID().toString())
		);
		variables.setTargets(targets);
	}
}
