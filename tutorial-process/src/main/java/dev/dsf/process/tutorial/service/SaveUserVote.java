package dev.dsf.process.tutorial.service;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;

public class SaveUserVote implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		QuestionnaireResponse userResponse = variables.getLatestReceivedQuestionnaireResponse();
		boolean vote = userResponse.getItem().stream().filter(item -> item.getLinkId().equals("vote"))
				.map(item -> (BooleanType) item.getAnswerFirstRep().getValue()).findFirst().get().getValue();
		variables.setBoolean(ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTE, vote);
	}
}
