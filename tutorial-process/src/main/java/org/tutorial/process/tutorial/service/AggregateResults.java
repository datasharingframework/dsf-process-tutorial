package org.tutorial.process.tutorial.service;

import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTING_RESULT;
import static org.tutorial.process.tutorial.ConstantsTutorial.SYSTEM_DSF_ORGANIZATION_IDENTIFIER;
import static org.tutorial.process.tutorial.ConstantsTutorial.VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER;
import static org.tutorial.process.tutorial.ConstantsTutorial.VOTING_RESULT_EXTENSION_URL;

import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.tutorial.process.tutorial.VoteResponse;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Variables;

public class AggregateResults implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Task taskStartVotingProcess = variables.getStartTask();
		Bundle resultBundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
		resultBundle.setMeta(new Meta()
				.addTag(new Coding().setSystem("http://dsf.dev/fhir/CodeSystem/read-access-tag").setCode("ALL")));
		Optional<String> optQuestion = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION);
		optQuestion.ifPresent(q -> variables.getTargets().getEntries().forEach(target ->
		{
			VoteResponse response = VoteResponse.valueOf(
					variables.getString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey()));
			switch (response)
			{
				case YES:
					String voteYes = "yes";
					taskStartVotingProcess
							.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), voteYes));
					break;
				case NO:
					String voteNo = "no";
					taskStartVotingProcess.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), voteNo));
					break;
				case TIMEOUT:
					taskStartVotingProcess.addOutput(createTimeoutOutput(target.getOrganizationIdentifierValue()));
					break;
			}
		}));
	}

	private TaskOutputComponent createVoteOutput(String organizationIdentifierValue, String vote)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(
				new Coding().setCode(CODESYSTEM_VOTING_PROCESS_VOTING_RESULT).setSystem(CODESYSTEM_VOTING_PROCESS)));
		voteOutput.setValue(new Coding().setSystem(CODESYSTEM_VOTING_PROCESS).setCode(vote));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER).setValue(
				new Identifier().setSystem(SYSTEM_DSF_ORGANIZATION_IDENTIFIER).setValue(organizationIdentifierValue));
		return voteOutput;
	}

	private TaskOutputComponent createTimeoutOutput(String organizationIdentifierValue)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(
				new Coding().setCode(CODESYSTEM_VOTING_PROCESS_VOTING_RESULT).setSystem(CODESYSTEM_VOTING_PROCESS)));
		voteOutput.setValue(new Coding().setSystem(CODESYSTEM_VOTING_PROCESS).setCode("timeout"));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER).setValue(
				new Identifier().setSystem(SYSTEM_DSF_ORGANIZATION_IDENTIFIER).setValue(organizationIdentifierValue));
		return voteOutput;
	}
}
