package dev.dsf.process.tutorial;

import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.VERSION;

public interface ConstantsTutorial
{
	String PROCESS_VERSION = VERSION.substring(4, 7);
	String RESOURCE_VERSION = VERSION.substring(0, 3);
	String PROCESS_NAME_DIC = "dicProcess";
	String PROCESS_NAME_FULL_DIC = "dsfdev_" + PROCESS_NAME_DIC;

	String PROFILE_TUTORIAL_TASK_DIC_PROCESS = "http://dsf.dev/fhir/StructureDefinition/task-start-dic-process";
	String PROFILE_TUTORIAL_TASK_DIC_PROCESS_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_DIC_PROCESS + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI = "http://dsf.dev/bpe/Process/" + PROCESS_NAME_DIC;
	String PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_DIC_PROCESS_URI + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_DIC_PROCESS_MESSAGE_NAME = "startDicProcess";

	String PROFILE_TUTORIAL_TASK_GOODBYE_DIC = "http://dsf.dev/fhir/StructureDefinition/task-goodbye-dic";
	String PROFILE_TUTORIAL_TASK_GOODBYE_DIC_MESSAGE_NAME = "goodbyeDic";

	String TUTORIAL_DIC_ORGANIZATION_IDENTIFIER = "dic.dsf.test";

	// The HELLO_COS constants are only needed for exercise 4 and above
	String PROCESS_NAME_COS = "cosProcess";
	String PROCESS_NAME_FULL_COS = "dsfdev_" + PROCESS_NAME_COS;

	String PROFILE_TUTORIAL_TASK_HELLO_COS = "http://dsf.dev/fhir/StructureDefinition/task-hello-cos";
	String PROFILE_TUTORIAL_TASK_HELLO_COS_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_HELLO_COS + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_COS_PROCESS_URI = "http://dsf.dev/bpe/Process/" + PROCESS_NAME_COS;
	String PROFILE_TUTORIAL_TASK_HELLO_COS_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_COS_PROCESS_URI + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_COS_PROCESS_MESSAGE_NAME = "cosProcess";

	String TUTORIAL_COS_ORGANIZATION_IDENTIFIER = "cos.dsf.test";

	// The HELLO_HRP constants are only needed for exercise 6 and above
	String PROCESS_NAME_HRP = "hrpProcess";
	String PROCESS_NAME_FULL_HRP = "dsfdev_" + PROCESS_NAME_HRP;

	String PROFILE_TUTORIAL_TASK_HELLO_HRP = "http://dsf.dev/fhir/StructureDefinition/task-hello-hrp";
	String PROFILE_TUTORIAL_TASK_HELLO_HRP_AND_LATEST_VERSION = PROFILE_TUTORIAL_TASK_HELLO_HRP + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI = "http://dsf.dev/bpe/Process/" + PROCESS_NAME_HRP;
	String PROFILE_TUTORIAL_TASK_HELLO_HRP_INSTANTIATES_CANONICAL = PROFILE_TUTORIAL_TASK_HRP_PROCESS_URI + "|"
			+ RESOURCE_VERSION;
	String PROFILE_TUTORIAL_TASK_HELLO_HRP_MESSAGE_NAME = "helloHrp";

	String CODESYSTEM_TUTORIAL = "http://dsf.dev/fhir/CodeSystem/tutorial";
	String CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT = "tutorial-input";

	String TUTORIAL_HRP_ORGANIZATION_IDENTIFIER = "hrp.dsf.test";
}
