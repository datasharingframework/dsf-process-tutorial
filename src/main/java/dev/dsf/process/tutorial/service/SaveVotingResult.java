package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;

import org.hl7.fhir.r4.model.BooleanType;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.process.tutorial.util.VoteResponse;

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
