package dev.dsf.process.tutorial.service;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.util.VoteResponse;

public class AggregateResults extends AbstractServiceDelegate
{
	Logger logger = LoggerFactory.getLogger(AggregateResults.class);

	public AggregateResults(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Task taskStartVotingProcess = variables.getStartTask();
		variables.getTargets().getEntries().forEach(target -> {
			VoteResponse response = VoteResponse.valueOf(variables.getString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey()));
			switch (response) {
				case YES: taskStartVotingProcess.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), "yes"));
							break;
				case NO: taskStartVotingProcess.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), "no"));
							break;
				case TIMEOUT: taskStartVotingProcess.addOutput(createTimeoutOutput(target.getOrganizationIdentifierValue()));
							break;
			}
		});
	}

	private TaskOutputComponent createVoteOutput(String organizationIdentifierValue, String vote)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(new Coding().setCode(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT).setSystem(ConstantsTutorial.CODESYSTEM_TUTORIAL)));
		voteOutput.setValue(new Coding().setSystem(ConstantsTutorial.CODESYSTEM_TUTORIAL).setCode(vote));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(ConstantsTutorial.VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(ConstantsTutorial.VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER).setValue(new StringType(organizationIdentifierValue));
		return voteOutput;
	}

	private TaskOutputComponent createTimeoutOutput(String organizationIdentifierValue)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(new Coding().setCode(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT).setSystem(ConstantsTutorial.CODESYSTEM_TUTORIAL)));
		voteOutput.setValue(new Coding().setSystem(ConstantsTutorial.CODESYSTEM_TUTORIAL).setCode("timeout"));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(ConstantsTutorial.VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(ConstantsTutorial.VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER).setValue(new StringType(organizationIdentifierValue));
		return voteOutput;
	}
}
