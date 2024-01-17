package dev.dsf.process.tutorial.exercise_3.profile;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_COS_PROCESS_URI_AND_LATEST_VERSION;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_DIC;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_DIC_MESSAGE_NAME;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_HELLO_DIC_PROCESS_URI_AND_LATEST_VERSION;
import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.RELEASE_DATE;
import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.VERSION;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import dev.dsf.fhir.validation.ResourceValidator;
import dev.dsf.fhir.validation.ResourceValidatorImpl;
import dev.dsf.fhir.validation.ValidationSupportRule;
import dev.dsf.bpe.v1.constants.*;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskIntent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;

public class TaskProfileTest
{
	private static final Logger logger = LoggerFactory.getLogger(TaskProfileTest.class);

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(VERSION, RELEASE_DATE,
			Arrays.asList("dsf-task-base-1.0.0.xml", "task-hello-dic.xml", "task-hello-cos.xml"),
			Arrays.asList("dsf-read-access-tag-1.0.0.xml", "dsf-bpmn-message-1.0.0.xml", "tutorial.xml"),
			Arrays.asList("dsf-read-access-tag-1.0.0.xml", "dsf-bpmn-message-1.0.0.xml", "tutorial.xml"));

	private ResourceValidator resourceValidator = new ResourceValidatorImpl(validationRule.getFhirContext(),
			validationRule.getValidationSupport());

	@Test
	public void testTaskHelloDicValid()
	{
		Task task = createValidTaskHelloDic();

		ValidationResult result = resourceValidator.validate(task);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0,
				result.getMessages().stream()
						.filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
								|| ResultSeverityEnum.FATAL.equals(m.getSeverity()))
						.peek(m -> logger.error(m.getMessage())).count());
	}

	private Task createValidTaskHelloDic()
	{
		Task task = new Task();
		task.getMeta().addProfile(PROFILE_TUTORIAL_TASK_HELLO_DIC);
		task.setInstantiatesCanonical(PROFILE_TUTORIAL_TASK_HELLO_DIC_PROCESS_URI_AND_LATEST_VERSION);
		task.setStatus(TaskStatus.REQUESTED);
		task.setIntent(TaskIntent.ORDER);
		task.setAuthoredOn(new Date());
		task.getRequester().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("Test_DIC");
		task.getRestriction().addRecipient().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("Test_DIC");

		task.addInput().setValue(new StringType(PROFILE_TUTORIAL_TASK_HELLO_DIC_MESSAGE_NAME)).getType().addCoding()
				.setSystem(CodeSystems.BpmnMessage.URL).setCode(CodeSystems.BpmnMessage.messageName().getCode());
		task.addInput().setValue(new StringType("Tutorial input")).getType().addCoding()
				.setSystem("http://dsf.dev/fhir/CodeSystem/tutorial").setCode("tutorial-input");

		return task;
	}

	@Test
	public void testTaskHelloCosValid()
	{
		Task task = createValidTaskHelloCos();

		ValidationResult result = resourceValidator.validate(task);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0,
				result.getMessages().stream()
						.filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
								|| ResultSeverityEnum.FATAL.equals(m.getSeverity()))
						.peek(m -> logger.error(m.getMessage())).count());
	}

	private Task createValidTaskHelloCos()
	{
		Task task = new Task();
		task.getMeta().addProfile(PROFILE_TUTORIAL_TASK_HELLO_COS);
		task.setInstantiatesCanonical(PROFILE_TUTORIAL_TASK_HELLO_COS_PROCESS_URI_AND_LATEST_VERSION);
		task.setStatus(TaskStatus.REQUESTED);
		task.setIntent(TaskIntent.ORDER);
		task.setAuthoredOn(new Date());
		task.getRequester().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("Test_DIC");
		task.getRestriction().addRecipient().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("Test_COS");

		task.addInput().setValue(new StringType(PROFILE_TUTORIAL_TASK_HELLO_COS_MESSAGE_NAME)).getType().addCoding()
				.setSystem(CodeSystems.BpmnMessage.URL).setCode(CodeSystems.BpmnMessage.messageName().getCode());
		task.addInput().setValue(new StringType(UUID.randomUUID().toString())).getType().addCoding()
				.setSystem(CodeSystems.BpmnMessage.URL).setCode(CodeSystems.BpmnMessage.businessKey().getCode());

		return task;
	}
}
