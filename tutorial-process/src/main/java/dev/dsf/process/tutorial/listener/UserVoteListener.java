package dev.dsf.process.tutorial.listener;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.DefaultUserTaskListener;
import dev.dsf.bpe.v1.variables.Variables;

public class UserVoteListener extends DefaultUserTaskListener
{
	private ProcessPluginApi api;

	public UserVoteListener(ProcessPluginApi api)
	{
		super(api);
		this.api = api;
	}

	@Override
	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse beforeCreate)
	{
		Variables variables = api.getVariables(userTask.getExecution());
		String question = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION).get();
		Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> displayItem = beforeCreate.getItem()
				.stream().filter(i -> i.getLinkId().equals(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION))
				.filter(QuestionnaireResponse.QuestionnaireResponseItemComponent::hasText).findFirst();

		displayItem.ifPresent(questionnaireResponseItemComponent -> questionnaireResponseItemComponent.setText(question));
	}
}
