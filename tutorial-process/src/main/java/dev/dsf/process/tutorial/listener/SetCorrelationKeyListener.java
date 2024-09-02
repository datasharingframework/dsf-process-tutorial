package dev.dsf.process.tutorial.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.constants.BpmnExecutionVariables;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;

public class SetCorrelationKeyListener implements ExecutionListener
{
	private final ProcessPluginApi api;

	public SetCorrelationKeyListener(ProcessPluginApi api)
	{
		this.api = api;
	}

	@Override
	public void notify(DelegateExecution execution) throws Exception
	{
		Variables variables = api.getVariables(execution);
		Target target = variables.getTarget();

		execution.setVariableLocal(BpmnExecutionVariables.CORRELATION_KEY, target.getCorrelationKey());
	}
}