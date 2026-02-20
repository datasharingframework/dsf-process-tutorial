package org.tutorial.process.tutorial;

import java.util.List;
import java.util.Map;

import org.tutorial.process.tutorial.spring.config.TutorialConfig;

import dev.dsf.bpe.v2.AbstractProcessPluginDefinition;

public class TutorialProcessPluginDefinition extends AbstractProcessPluginDefinition
{
	@Override
	public List<String> getProcessModels()
	{

		return List.of("bpe/dic-process.bpmn", "bpe/cos-process.bpmn", "bpe/hrp-process.bpmn", "bpe/vote.bpmn",
				"bpe/voting-process.bpmn");
	}

	@Override
	public Map<String, List<String>> getFhirResourcesByProcessId()
	{

		String aDicProcess = "fhir/ActivityDefinition/dic-process.xml";
		String sTaskDicProcess = "fhir/StructureDefinition/task-start-dic-process.xml";
		String tTaskDicProcess = "fhir/Task/task-start-dic-process.xml";
		String sTaskGoodbyeDic = "fhir/StructureDefinition/task-goodbye-dic.xml";

		String cTutorial = "fhir/CodeSystem/tutorial.xml";
		String vTutorial = "fhir/ValueSet/tutorial.xml";

		String aCosProcess = "fhir/ActivityDefinition/cos-process.xml";
		String sTaskHelloCos = "fhir/StructureDefinition/task-hello-cos.xml";

		String aHrpProcess = "fhir/ActivityDefinition/hrp-process.xml";
		String sTaskHelloHrp = "fhir/StructureDefinition/task-hello-hrp.xml";

		String aVotingProcess = "fhir/ActivityDefinition/voting-process.xml";
		String sTaskStartVotingProcess = "fhir/StructureDefinition/task-start-voting-process.xml";
		String tTaskStartVotingProcess = "fhir/Task/task-start-voting-process.xml";
		String sTaskReturnVote = "fhir/StructureDefinition/task-return-vote.xml";

		String aVoteProcess = "fhir/ActivityDefinition/vote.xml";
		String sTaskStartVote = "fhir/StructureDefinition/task-start-vote.xml";
		String qUserVote = "fhir/Questionnaire/user-vote.xml";

		String cVotingProcess = "fhir/CodeSystem/voting-process.xml";
		String sExtensionVotingResult = "fhir/StructureDefinition/extension-voting-result.xml";
		String vBinaryQuestionAnswers = "fhir/ValueSet/voting-results.xml";
		String vVotingParameters = "fhir/ValueSet/voting-parameters.xml";
		String vVotingResults = "fhir/ValueSet/voting-results.xml";

		return Map.of(ConstantsTutorial.PROCESS_NAME_FULL_DIC,
				List.of(aDicProcess, sTaskDicProcess, tTaskDicProcess, sTaskGoodbyeDic, cTutorial, vTutorial),
				ConstantsTutorial.PROCESS_NAME_FULL_COS, List.of(aCosProcess, sTaskHelloCos, cTutorial, vTutorial),
				ConstantsTutorial.PROCESS_NAME_FULL_HRP, List.of(aHrpProcess, sTaskHelloHrp, cTutorial, vTutorial),
				ConstantsTutorial.PROCESS_NAME_FULL_VOTING_PROCESS,
				List.of(aVotingProcess, sTaskStartVotingProcess, tTaskStartVotingProcess, sTaskReturnVote,
						sExtensionVotingResult, cTutorial, vBinaryQuestionAnswers, vTutorial, cVotingProcess,
						vVotingParameters, vVotingResults),
				ConstantsTutorial.PROCESS_NAME_FULL_VOTE,
				List.of(aVoteProcess, sTaskStartVote, qUserVote, sExtensionVotingResult, cTutorial,
						vBinaryQuestionAnswers, vTutorial, cVotingProcess, vVotingParameters, vVotingResults));
	}

	@Override
	public List<Class<?>> getSpringConfigurations()
	{
		return List.of(TutorialConfig.class);
	}
}
