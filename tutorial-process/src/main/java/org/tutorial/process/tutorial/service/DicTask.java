package org.tutorial.process.tutorial.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Variables;

public class DicTask implements ServiceTask
{
	private static final Logger logger = LoggerFactory.getLogger(DicTask.class);

	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		logger.info("Hello Dic from organization '{}'",
				variables.getStartTask().getRestriction().getRecipientFirstRep().getIdentifier().getValue());
	}
}
