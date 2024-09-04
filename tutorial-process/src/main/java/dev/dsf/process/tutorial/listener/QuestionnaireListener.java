package dev.dsf.process.tutorial.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.constants.CodeSystems;
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
	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse beforeCreate)
	{
		Optional<Questionnaire> optionalQuestionnaire = findQuestionnaire();
		Optional<String> optCorrelationKey = api.getTaskHelper().getFirstInputParameterStringValue(api.getVariables(userTask.getExecution()).getStartTask(), CodeSystems.BpmnMessage.URL, CodeSystems.BpmnMessage.Codes.CORRELATION_KEY);
		if (optionalQuestionnaire.isPresent() && optCorrelationKey.isPresent())
		{
			String correlationKey = optCorrelationKey.get();
			String question = (String) userTask.getExecution()
					.getVariable(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);

			Questionnaire questionnaire = optionalQuestionnaire.get();
			String questionnaireUrl = questionnaire.getUrl();
			questionnaire.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst().ifPresent(item -> item.setText(question));
			questionnaire.setUrl(questionnaire.getUrl().concat("-").concat(correlationKey));

			beforeCreate.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst().ifPresent(item -> item.setText(question));
			beforeCreate.setQuestionnaire(questionnaireUrl.concat("-").concat(correlationKey).concat("|").concat(new TutorialProcessPluginDefinition().getResourceVersion()));

			uploadQuestionnaire(questionnaire);
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

	private void uploadQuestionnaire(Questionnaire questionnaire)
	{
		api.getFhirWebserviceClientProvider().getLocalWebserviceClient().create(questionnaire);
	}
}
