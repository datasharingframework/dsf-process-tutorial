package dev.dsf.process.tutorial.message;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE;

import java.util.Objects;
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
		Boolean automatedVote = variables.getBoolean(VOTE_PROCESS_VARIABLE_AUTOMATED_VOTE);
		if (Objects.nonNull(automatedVote))
		{
			return Stream.of(createVoteResultInputParameter(automatedVote));
		} else {
			Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> optionalItem = api.getQuestionnaireResponseHelper().getFirstItemLeaveMatchingLinkId(variables.getLatestReceivedQuestionnaireResponse(), CODESYSTEM_TUTORIAL_VALUE_VOTE);
			return optionalItem.stream().map(questionnaireResponseItemComponent -> createVoteResultInputParameter(((BooleanType) questionnaireResponseItemComponent.getAnswerFirstRep().getValue()).booleanValue()));
		}
	}

	private ParameterComponent createVoteResultInputParameter(boolean boolVote)
	{
		ParameterComponent inputParameter = new ParameterComponent();
		inputParameter.setType(new CodeableConcept().addCoding(new Coding().setSystem(CODESYSTEM_TUTORIAL).setCode(CODESYSTEM_TUTORIAL_VALUE_VOTE)));
		inputParameter.setValue(new BooleanType(boolVote));
		return inputParameter;
	}
}
