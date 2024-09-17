package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;

import java.time.Instant;
import java.util.Date;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.constants.CodeSystems;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;

public class CreateQuestionnaire extends AbstractServiceDelegate
{
	public CreateQuestionnaire(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables) throws BpmnError, Exception
	{
		Task startTask = variables.getStartTask();
		String question = api.getTaskHelper().getFirstInputParameterStringValue(startTask,
				CODESYSTEM_TUTORIAL, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION).get();
		String businessKey = api.getTaskHelper().getFirstInputParameterStringValue(startTask, CodeSystems.BpmnMessage.URL, CodeSystems.BpmnMessage.Codes.BUSINESS_KEY).get();
		Questionnaire questionnaire = createQuestionnaire(question, businessKey);
		Questionnaire created = uploadQuestionnaire(questionnaire);
		variables.setResource(ConstantsTutorial.PROCESS_VARIABLE_QUESTIONNAIRE, created);
	}

	private Questionnaire createQuestionnaire(String question, String businessKey)
	{
		Questionnaire questionnaire = new Questionnaire();
		questionnaire.setMeta(
				new Meta()
						.addProfile("http://dsf.dev/fhir/StructureDefinition/questionnaire|1.5.0")
						.addTag(new Coding().setSystem("http://dsf.dev/fhir/CodeSystem/read-access-tag").setCode("ALL"))
		);
		questionnaire.setUrl("http://dsf.dev/fhir/Questionnaire/voting-process-" + businessKey);

		questionnaire.setVersion(new TutorialProcessPluginDefinition().getResourceVersion());
		questionnaire.setDate(Date.from(Instant.now()));
		questionnaire.setStatus(Enumerations.PublicationStatus.DRAFT);

		Questionnaire.QuestionnaireItemComponent businessKeyItem = new Questionnaire.QuestionnaireItemComponent();
		businessKeyItem.setLinkId("business-key");
		businessKeyItem.setType(Questionnaire.QuestionnaireItemType.STRING);
		businessKeyItem.setText("The business-key of the process execution");
		businessKeyItem.setRequired(true);
		questionnaire.addItem(businessKeyItem);

		Questionnaire.QuestionnaireItemComponent userTaskIdItem = new Questionnaire.QuestionnaireItemComponent();
		userTaskIdItem.setLinkId("user-task-id");
		userTaskIdItem.setType(Questionnaire.QuestionnaireItemType.STRING);
		userTaskIdItem.setText("The user-task-id of the process execution");
		userTaskIdItem.setRequired(true);
		questionnaire.addItem(userTaskIdItem);

		Questionnaire.QuestionnaireItemComponent voteItem = new Questionnaire.QuestionnaireItemComponent();
		voteItem.setLinkId("vote");
		voteItem.setType(Questionnaire.QuestionnaireItemType.BOOLEAN);
		voteItem.setText(question);
		voteItem.setRequired(true);
		questionnaire.addItem(voteItem);

		return questionnaire;
	}

	private Questionnaire uploadQuestionnaire(Questionnaire questionnaire)
	{
		return api.getFhirWebserviceClientProvider().getLocalWebserviceClient().withRetryForever(60000).create(questionnaire);
	}
}
