package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.BooleanType;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.util.VoteResponse;

public class SaveVotingResult extends AbstractServiceDelegate
{
	public SaveVotingResult(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Target target = variables.getTarget();
		boolean vote = api.getTaskHelper().getFirstInputParameterValue(variables.getLatestTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VOTE, BooleanType.class).get().getValue();
		variables.setString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey(),
				vote ? VoteResponse.YES.name() : VoteResponse.NO.name());
	}
}
