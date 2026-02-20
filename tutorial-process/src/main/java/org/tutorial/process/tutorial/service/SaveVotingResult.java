package org.tutorial.process.tutorial.service;

import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;

import org.hl7.fhir.r4.model.BooleanType;
import org.tutorial.process.tutorial.VoteResponse;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class SaveVotingResult implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Target target = variables.getTarget();
		boolean vote = api.getTaskHelper().getFirstInputParameterValue(variables.getLatestTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VOTE, BooleanType.class).get().getValue();
		variables.setString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey(),
				vote ? VoteResponse.YES.name() : VoteResponse.NO.name());
	}
}
