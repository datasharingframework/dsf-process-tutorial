package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;

public class DicTask extends AbstractServiceDelegate
{

	private static final Logger logger = LoggerFactory.getLogger(DicTask.class);

	private final boolean loggingEnabled;

	public DicTask(ProcessPluginApi api, boolean loggingEnabled)
	{
		super(api);
		this.loggingEnabled = loggingEnabled;
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables)
	{
		Optional<String> tutorialInputParameter = api.getTaskHelper().getFirstInputParameterStringValue(
				variables.getStartTask(), CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT);
		if (loggingEnabled)
		{
			logger.info("Hello Dic from organization '{}' with message '{}'",
					variables.getStartTask().getRestriction().getRecipientFirstRep().getIdentifier().getValue(),
					tutorialInputParameter.orElse("<no message>"));
		}

		Target target = variables.createTarget("cos.dsf.test", "cos.dsf.test_Endpoint", "https://cos/fhir");
		variables.setTarget(target);

		boolean stop = tutorialInputParameter.map("not-cos"::equals).get();
		variables.setBoolean("stop", stop);
	}
}
