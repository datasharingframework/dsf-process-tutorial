package dev.dsf.process.tutorial.listener;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Questionnaire;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;

public class QuestionnaireListener extends CustomUserTaskListener
{
	ProcessPluginApi api;
	public QuestionnaireListener(ProcessPluginApi api)
	{
		super(api);
		this.api = api;
	}

	@Override
	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, Questionnaire questionnaire)
	{
		String question = (String) userTask.getExecution()
				.getVariable(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);
		questionnaire.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst().ifPresent(item -> item.setText(question));
	}

	@Override
	protected Questionnaire provideQuestionnaire()
	{
		return getQuestionnaire();
	}

	private Questionnaire getQuestionnaire()
	{
		Questionnaire questionnaire = new Questionnaire();
		questionnaire.setMeta(
				new Meta()
						.addProfile("http://dsf.dev/fhir/StructureDefinition/questionnaire|1.5.0")
						.addTag(new Coding().setSystem("http://dsf.dev/fhir/CodeSystem/read-access-tag").setCode("ALL"))
		);
		questionnaire.setUrl("http://dsf.dev/fhir/Questionnaire/user-vote");
		questionnaire.setId(UUID.randomUUID().toString());

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
		voteItem.setText("Placeholder");
		voteItem.setRequired(true);
		questionnaire.addItem(voteItem);

		return questionnaire;
	}
}
