package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT;
import static dev.dsf.process.tutorial.ConstantsTutorial.SYSTEM_DSF_ORGANIZATION_IDENTIFIER;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTING_RESULT_EXTENSION_URL;

import java.util.Optional;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.util.VoteResponse;

public class AggregateResults extends AbstractServiceDelegate
{

	public AggregateResults(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Task taskStartVotingProcess = variables.getStartTask();
		Bundle resultBundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
		resultBundle.setMeta(new Meta().addTag(new Coding().setSystem("http://dsf.dev/fhir/CodeSystem/read-access-tag").setCode("ALL")));
		Optional<String> optQuestion = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(), CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);
		optQuestion.ifPresent(q -> variables.getTargets().getEntries().forEach(target -> {
			VoteResponse response = VoteResponse.valueOf(variables.getString(target.getOrganizationIdentifierValue() + "_" + target.getCorrelationKey()));
			switch (response) {
				case YES:
					String voteYes = "yes";
					taskStartVotingProcess.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), voteYes));
					addVoteToBundle(resultBundle, target.getOrganizationIdentifierValue(), q, voteYes);
					break;
				case NO:
					String voteNo = "no";
					taskStartVotingProcess.addOutput(createVoteOutput(target.getOrganizationIdentifierValue(), voteNo));
					addVoteToBundle(resultBundle, target.getOrganizationIdentifierValue(), q, voteNo);
					break;
				case TIMEOUT:
					taskStartVotingProcess.addOutput(createTimeoutOutput(target.getOrganizationIdentifierValue()));
					addVoteToBundle(resultBundle, target.getOrganizationIdentifierValue(), q, "timeout");
					break;
			}
		}));
		saveBundle(resultBundle);
	}

	private TaskOutputComponent createVoteOutput(String organizationIdentifierValue, String vote)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(new Coding().setCode(CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT).setSystem(CODESYSTEM_TUTORIAL)));
		voteOutput.setValue(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(vote));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER)
				.setValue(new Identifier().setSystem(SYSTEM_DSF_ORGANIZATION_IDENTIFIER).setValue(organizationIdentifierValue));
		return voteOutput;
	}

	private TaskOutputComponent createTimeoutOutput(String organizationIdentifierValue)
	{
		TaskOutputComponent voteOutput = new TaskOutputComponent();
		voteOutput.setType(new CodeableConcept().addCoding(new Coding().setCode(CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT).setSystem(CODESYSTEM_TUTORIAL)));
		voteOutput.setValue(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode("timeout"));

		Extension votingResultExtension = voteOutput.addExtension();
		votingResultExtension.setUrl(VOTING_RESULT_EXTENSION_URL);
		votingResultExtension.addExtension().setUrl(VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER)
				.setValue(new Identifier().setSystem(SYSTEM_DSF_ORGANIZATION_IDENTIFIER).setValue(organizationIdentifierValue));
		return voteOutput;
	}

	private void addVoteToBundle(Bundle bundle, String organizationIdentifierValue, String question, String vote)
	{
		Observation voteObservation = new Observation()
				.setStatus(Observation.ObservationStatus.FINAL)
				.setCode(new CodeableConcept(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(CODESYSTEM_TUTORIAL_VALUE_VOTING_RESULT)))
				.setValue(new CodeableConcept(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(vote)))
				.addComponent(
						new Observation.ObservationComponentComponent()
								.setCode(new CodeableConcept(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION)))
								.setValue(new StringType(question))
				)
				.setSubject(new Reference().setIdentifier(new Identifier().setSystem(SYSTEM_DSF_ORGANIZATION_IDENTIFIER).setValue(organizationIdentifierValue)));
		voteObservation.setId(UUID.randomUUID().toString());
		bundle.addEntry()
				.setResource(voteObservation)
				.setFullUrl("urn:uuid:" + voteObservation.getId());
	}

	private void saveBundle(Bundle bundle)
	{
		api.getFhirWebserviceClientProvider().getLocalWebserviceClient().withRetryForever(60000).create(bundle);
	}
}
