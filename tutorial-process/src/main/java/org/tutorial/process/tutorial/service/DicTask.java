package org.tutorial.process.tutorial.service;

import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class DicTask implements ServiceTask
{
	private static final Logger logger = LoggerFactory.getLogger(DicTask.class);

	private boolean loggingEnabled;

	public DicTask(boolean loggingEnabled)
	{
		this.loggingEnabled = loggingEnabled;
	}

	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		if (loggingEnabled)
		{
			Optional<String> tutorialInputParameter = api.getTaskHelper().getFirstInputParameterStringValue(
					variables.getStartTask(), CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT);

			logger.info("Hello Dic from organization '{}' with message '{}'",
					variables.getStartTask().getRestriction().getRecipientFirstRep().getIdentifier().getValue(),
					tutorialInputParameter.orElse("<no message>"));
		}

		Target target = variables.createTarget("cos.dsf.test", "cos.dsf.test_Endpoint", "https://cos/fhir");
		variables.setTarget(target);
	}
}
