package dev.dsf.process.tutorial.exercise_7.structuredefinition;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_INSTANTIATES_CANONICAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.RESOURCE_VERSION;
import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.RELEASE_DATE;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import dev.dsf.bpe.v2.constants.CodeSystems;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.fhir.validation.ResourceValidator;
import dev.dsf.fhir.validation.ResourceValidatorImpl;
import dev.dsf.fhir.validation.ValidationSupportRule;

public class TaskStartVotingProcessTest
{
	private final Logger logger = LoggerFactory.getLogger(TaskStartVotingProcessTest.class);

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(RESOURCE_VERSION, RELEASE_DATE,
			Arrays.asList("dsf-task-2.0.0.xml", "task-start-voting-process.xml", "extension-voting-result.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-bpmn-message-2.0.0.xml", "voting-process.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-bpmn-message-2.0.0.xml", "voting-parameters.xml",
					"voting-results.xml"));

	private ResourceValidator resourceValidator = new ResourceValidatorImpl(validationRule.getFhirContext(),
			validationRule.getValidationSupport());

	@Test
	public void structureDefinitionHasCorrectOutputParameterTest()
	{
		Task task = createValidStartVotingProcessTask();

		ValidationResult result = resourceValidator.validate(task);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0,
				result.getMessages().stream()
						.filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
								|| ResultSeverityEnum.FATAL.equals(m.getSeverity()))
						.peek(m -> logger.error(m.getMessage())).count());
	}

	private Task createValidStartVotingProcessTask()
	{
		Task task = new Task();
		task.getMeta().addProfile(PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS);
		task.setInstantiatesCanonical(PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_INSTANTIATES_CANONICAL);
		task.setStatus(Task.TaskStatus.REQUESTED);
		task.setIntent(Task.TaskIntent.ORDER);
		task.setAuthoredOn(new Date());
		task.getRequester().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("dic.dsf.test");
		task.getRestriction().addRecipient().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("dic.dsf.test");

		task.addInput().setValue(new StringType(PROFILE_TUTORIAL_TASK_START_VOTING_PROCESS_MESSAGE_NAME)).getType()
				.addCoding(CodeSystems.BpmnMessage.messageName());
		task.addInput().setValue(new StringType("A question?")).getType().addCoding()
				.setSystem(CODESYSTEM_VOTING_PROCESS).setCode(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION);

		Extension votingResultExtension = new Extension();
		votingResultExtension.addExtension().setUrl("organization-identifier").setValue(
				new Identifier().setSystem("http://dsf.dev/sid/organization-identifier").setValue("dic.dsf.test"));
		votingResultExtension.setUrl("http://dsf.dev/fhir/StructureDefinition/extension-voting-result");
		Task.TaskOutputComponent outputComponent = task.addOutput();
		outputComponent.setValue(new Coding().setSystem(CODESYSTEM_VOTING_PROCESS).setCode("yes"));
		outputComponent.getType().addCoding(new Coding().setSystem(CODESYSTEM_VOTING_PROCESS).setCode("voting-result"));
		outputComponent.addExtension(votingResultExtension);

		return task;
	}
}
