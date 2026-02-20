package org.tutorial.process.tutorial.listener;

import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;

import java.util.Optional;

import org.hl7.fhir.r4.model.QuestionnaireResponse;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.DefaultUserTaskListener;
import dev.dsf.bpe.v2.activity.values.CreateQuestionnaireResponseValues;
import dev.dsf.bpe.v2.variables.Variables;

public class UserVoteListener extends DefaultUserTaskListener
{
	@Override
	protected void beforeQuestionnaireResponseCreate(ProcessPluginApi api, Variables variables,
			CreateQuestionnaireResponseValues createQuestionnaireResponseValues, QuestionnaireResponse beforeCreate)
	{
		String question = api.getTaskHelper().getFirstInputParameterStringValue(variables.getStartTask(),
				CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION).get();
		Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> displayItem = beforeCreate.getItem().stream()
				.filter(i -> i.getLinkId().equals(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION)).findFirst();

		displayItem
				.ifPresent(questionnaireResponseItemComponent -> questionnaireResponseItemComponent.setText(question));
	}
}
