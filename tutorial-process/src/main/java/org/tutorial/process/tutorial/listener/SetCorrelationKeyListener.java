package org.tutorial.process.tutorial.listener;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ExecutionListener;
import dev.dsf.bpe.v2.constants.BpmnExecutionVariables;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class SetCorrelationKeyListener implements ExecutionListener
{
	@Override
	public void notify(ProcessPluginApi api, Variables variables) throws Exception
	{
		Target target = variables.getTarget();

		variables.setStringLocal(BpmnExecutionVariables.CORRELATION_KEY, target.getCorrelationKey());
	}
}