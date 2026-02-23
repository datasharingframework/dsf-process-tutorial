package org.tutorial.process.tutorial;

import dev.dsf.bpe.v2.ProcessPluginDefinition;

public abstract class ConstantsTutorial
{
	private ConstantsTutorial()
	{

	}

	private static final ProcessPluginDefinition processPluginDefinition = new TutorialProcessPluginDefinition();
	public static final String RESOURCE_VERSION = processPluginDefinition.getResourceVersion();

	public static final String PROCESS_NAME_DIC = "dicProcess";
	public static final String PROCESS_NAME_FULL_DIC = "tutorialorg_" + PROCESS_NAME_DIC;

	public static final String PROFILE_TUTORIAL_TASK_DIC_PROCESS = "http://example.org/fhir/StructureDefinition/task-start-dic-process";
	public static final String PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI = "http://example.org/bpe/Process/"
			+ PROCESS_NAME_DIC;
	public static final String PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_DIC_PROCESS_MESSAGE_NAME = "startDicProcess";

	public static final String PROFILE_TUTORIAL_TASK_GOODBYE_DIC = "http://example.org/fhir/StructureDefinition/task-goodbye-dic";
	public static final String PROFILE_TUTORIAL_TASK_GOODBYE_DIC_MESSAGE_NAME = "goodbyeDic";

	public static final String TUTORIAL_DIC_ORGANIZATION_IDENTIFIER = "dic.dsf.test";

	// The HELLO_COS constants are only needed for exercise 4 and above
	public static final String PROCESS_NAME_COS = "cosProcess";
	public static final String PROCESS_NAME_FULL_COS = "tutorialorg_" + PROCESS_NAME_COS;

	public static final String PROFILE_TUTORIAL_TASK_HELLO_COS = "http://example.org/fhir/StructureDefinition/task-hello-cos";
	public static final String PROFILE_TUTORIAL_TASK_HELLO_COS_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_HELLO_COS
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_COS_PROCESS_URI = "http://example.org/bpe/Process/"
			+ PROCESS_NAME_COS;
	public static final String PROFILE_TUTORIAL_TASK_HELLO_COS_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_COS_PROCESS_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME = "helloCos";

	public static final String TUTORIAL_COS_ORGANIZATION_IDENTIFIER = "cos.dsf.test";

	// The HELLO_HRP constants are only needed for exercise 6 and above
	public static final String PROCESS_NAME_HRP = "hrpProcess";
	public static final String PROCESS_NAME_FULL_HRP = "tutorialorg_" + PROCESS_NAME_HRP;

	public static final String PROFILE_TUTORIAL_TASK_HELLO_HRP = "http://example.org/fhir/StructureDefinition/task-hello-hrp";
	public static final String PROFILE_TUTORIAL_TASK_HELLO_HRP_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_HELLO_HRP
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI = "http://example.org/bpe/Process/"
			+ PROCESS_NAME_HRP;
	public static final String PROFILE_TUTORIAL_TASK_HELLO_HRP_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_HELLO_HRP_MESSAGE_NAME = "helloHrp";

	public static final String CODESYSTEM_TUTORIAL = "http://example.org/fhir/CodeSystem/tutorial";
	public static final String CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT = "tutorial-input";
	public static final String CODESYSTEM_VOTING_PROCESS = "http://example.org/fhir/CodeSystem/voting-process";
	public static final String CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION = "binary-question";
	public static final String CODESYSTEM_VOTING_PROCESS_BINARY_QUESTION_ANSWER = "http://example.org/fhir/CodeSystem/binary-question-answer";
	public static final String CODESYSTEM_VOTING_PROCESS_VOTE = "vote";
	public static final String CODESYSTEM_VOTING_PROCESS_VOTING_RESULT = "voting-result";
	public static final String CODESYSTEM_VOTING_PROCESS_RESULT_BUNDLE = "result-bundle";

	public static final String VOTING_RESULT_EXTENSION_URL = "http://example.org/fhir/StructureDefinition/extension-voting-result";
	public static final String VOTING_RESULT_EXTENSION_ORGANIZATION_IDENTIFIER = "organization-identifier";
	public static final String VOTING_RESULT_EXTENSION_TIMEOUT = "timeout";

	public static final String TUTORIAL_HRP_ORGANIZATION_IDENTIFIER = "hrp.dsf.test";

	public static final String PROCESS_NAME_VOTING_PROCESS = "votingProcess";
	public static final String PROCESS_NAME_FULL_VOTING_PROCESS = "tutorialorg_" + PROCESS_NAME_VOTING_PROCESS;

	public static final String PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS = "http://example.org/fhir/StructureDefinition/task-start-voting-process";
	public static final String PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_VOTING_PROCESS_URI = "http://example.org/bpe/Process/"
			+ PROCESS_NAME_VOTING_PROCESS;
	public static final String PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_VOTING_PROCESS_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_MESSAGE_NAME = "startVotingProcess";

	public static final String PROFILE_TUTORIAL_TASK_RETURN_VOTE = "http://example.org/fhir/StructureDefinition/task-return-vote";
	public static final String PROFILE_TUTORIAL_TASK_RETURN_VOTE_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_RETURN_VOTE
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_RETURN_VOTE_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_VOTING_PROCESS_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_RETURN_VOTE_MESSAGE_NAME = "returnVote";

	public static final String PROCESS_NAME_VOTE = "vote";
	public static final String PROCESS_NAME_FULL_VOTE = "tutorialorg_" + PROCESS_NAME_VOTE;

	public static final String PROFILE_TUTORIAL_TASK_START_VOTE = "http://example.org/fhir/StructureDefinition/task-start-voting-process";
	public static final String PROFILE_TUTORIAL_TASK_START_VOTE_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_START_VOTE
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_VOTE_URI = "http://example.org/bpe/Process/" + PROCESS_NAME_VOTE;
	public static final String PROFILE_TUTORIAL_TASK_START_VOTE_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_VOTE_URI
			+ "|" + RESOURCE_VERSION;
	public static final String PROFILE_TUTORIAL_TASK_START_VOTE_MESSAGE_NAME = "startVote";

	public static final String VOTE_PROCESS_VARIABLE_VOTE = "vote";
	public static final String VOTE_PROCESS_VARIABLE_SHOULD_USER_VOTE = "userVote";

	public static final String QUESTIONNAIRE_USER_VOTE_URL = "http://example.org/fhir/Questionnaire/user-vote";
	public static final String SYSTEM_DSF_ORGANIZATION_IDENTIFIER = "http://dsf.dev/sid/organization-identifier";
}