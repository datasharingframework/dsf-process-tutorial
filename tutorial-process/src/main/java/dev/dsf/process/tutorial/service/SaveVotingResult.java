package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTE;

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
		variables.setString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey(),
				api.getTaskHelper().getFirstInputParameterValue(variables.getLatestTask(), CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_VOTE, BooleanType.class)
						.get().booleanValue() ? VoteResponse.YES.name() : VoteResponse.NO.name());
	}
}
