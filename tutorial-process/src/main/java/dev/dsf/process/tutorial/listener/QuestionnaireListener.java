package dev.dsf.process.tutorial.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.beans.factory.InitializingBean;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.DefaultUserTaskListener;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;

public class QuestionnaireListener extends DefaultUserTaskListener implements InitializingBean
{
	ProcessPluginApi api;
	public QuestionnaireListener(ProcessPluginApi api)
	{
		super(api);
		this.api = api;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		super.afterPropertiesSet();
	}

	@Override
	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse beforeCreate)
	{
		Optional<Questionnaire> optionalQuestionnaire = findQuestionnaire();
		if (optionalQuestionnaire.isPresent())
		{
			String question = (String) userTask.getExecution()
					.getVariable(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);

			Questionnaire questionnaire = optionalQuestionnaire.get();
			questionnaire.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst().ifPresent(item -> item.setText(question));
			updateQuestionnaire(questionnaire);

			beforeCreate.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst()
					.ifPresent(item -> item.setText(question));
		}
	}

	private Optional<Questionnaire> findQuestionnaire()
	{
		Map<String, List<String>> searchParams = new HashMap<>();
		searchParams.put("url", List.of(ConstantsTutorial.QUESTIONNAIRE_USER_VOTE_URL));
		searchParams.put("version", List.of(TutorialProcessPluginDefinition.VERSION.substring(0, 3)));
		Bundle result = api.getFhirWebserviceClientProvider().getLocalWebserviceClient().searchWithStrictHandling(Questionnaire.class, searchParams);
		if (result.getTotal() < 1) return Optional.empty();
		return result.getEntry().stream()
				.filter(bundleEntryComponent -> bundleEntryComponent.getResource() instanceof Questionnaire)
				.map(bundleEntryComponent -> (Questionnaire) bundleEntryComponent.getResource())
				.findFirst();
	}

	private void updateQuestionnaire(Questionnaire questionnaire)
	{
		api.getFhirWebserviceClientProvider().getLocalWebserviceClient().update(questionnaire);
	}
}
