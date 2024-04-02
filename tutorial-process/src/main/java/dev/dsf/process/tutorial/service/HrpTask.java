package dev.dsf.process.tutorial.service;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;

public class HrpTask extends AbstractServiceDelegate
{
	private static final Logger logger = LoggerFactory.getLogger(HrpTask.class);

	public HrpTask(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables)
	{
		Optional<String> tutorialInputParameter = api.getTaskHelper().getFirstInputParameterStringValue(
				variables.getStartTask(), "http://dsf.dev/fhir/CodeSystem/tutorial", "tutorial-input");
		boolean sendResponse = tutorialInputParameter.map("send-response"::equals).orElse(false);
		variables.setBoolean("sendResponse", sendResponse);

		if (sendResponse)
		{
			Target target = variables.createTarget("dic.dsf.test", "dic.dsf.test_Endpoint", "https://dic/fhir");
			variables.setTarget(target);
		}
		else
		{
			logger.info("Not sending response to organization with identifier 'dic.dsf.test'");
		}
	}
}
