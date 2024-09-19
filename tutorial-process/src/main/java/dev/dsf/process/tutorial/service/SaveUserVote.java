package dev.dsf.process.tutorial.service;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;

public class SaveUserVote extends AbstractServiceDelegate
{
	public SaveUserVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables) throws BpmnError, Exception
	{
		QuestionnaireResponse userResponse = variables.getLatestReceivedQuestionnaireResponse();
		boolean vote = userResponse.getItem().stream().filter(item -> item.getLinkId().equals("vote")).map(item -> (BooleanType) item.getAnswerFirstRep().getValue()).findFirst().get().getValue();
		variables.setBoolean(ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTE, vote);
	}
}
