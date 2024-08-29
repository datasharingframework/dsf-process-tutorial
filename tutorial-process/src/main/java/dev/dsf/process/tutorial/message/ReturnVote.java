package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_VOTED_AUTOMATICALLY;

import java.util.Optional;
import java.util.stream.Stream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Task.ParameterComponent;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractTaskMessageSend;
import dev.dsf.bpe.v1.variables.Variables;

public class ReturnVote extends AbstractTaskMessageSend
{
	public ReturnVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected Stream<ParameterComponent> getAdditionalInputParameters(DelegateExecution execution,
			Variables variables)
	{
		if (variables.getBoolean(VOTE_PROCESS_VARIABLE_VOTED_AUTOMATICALLY))
		{
			return Stream.of(createInputParameter(variables.getBoolean(VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE)));
		} else {
			Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> optionalItem = api.getQuestionnaireResponseHelper().getFirstItemLeaveMatchingLinkId(variables.getLatestReceivedQuestionnaireResponse(), CODESYSTEM_TUTORIAL_VALUE_VOTE);
			return optionalItem.stream().map(questionnaireResponseItemComponent -> createInputParameter(((BooleanType) questionnaireResponseItemComponent.getAnswerFirstRep().getValue()).booleanValue()));
		}
	}

	private ParameterComponent createInputParameter(boolean boolVote)
	{
		ParameterComponent inputParameter = new ParameterComponent();
		inputParameter.setType(new CodeableConcept().addCoding(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(CODESYSTEM_TUTORIAL_VALUE_VOTE)));
		String voteCode = boolVote ? "yes" : "no";
		inputParameter.setValue(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(voteCode));
		return inputParameter;
	}
}
