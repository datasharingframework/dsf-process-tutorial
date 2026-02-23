package org.tutorial.process.tutorial.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class HrpTask implements ServiceTask
{
	private static final Logger logger = LoggerFactory.getLogger(HrpTask.class);

	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Optional<String> tutorialInputParameter = api.getTaskHelper().getFirstInputParameterStringValue(
				variables.getStartTask(), "http://example.org/fhir/CodeSystem/tutorial", "tutorial-input");
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
