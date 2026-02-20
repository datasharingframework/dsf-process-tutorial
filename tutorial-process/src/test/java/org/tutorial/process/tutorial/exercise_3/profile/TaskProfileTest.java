package org.tutorial.process.tutorial.exercise_3.profile;

import static org.junit.Assert.assertEquals;
import static org.tutorial.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS;
import static org.tutorial.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL;
import static org.tutorial.process.tutorial.ConstantsTutorial.PROFILE_TUTORIAL_TASK_DIC_PROCESS_MESSAGE_NAME;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskIntent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tutorial.process.tutorial.TutorialProcessPluginDefinition;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.bpe.v2.constants.CodeSystems;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.fhir.validation.ResourceValidator;
import dev.dsf.fhir.validation.ResourceValidatorImpl;
import dev.dsf.fhir.validation.ValidationSupportRule;

public class TaskProfileTest
{
	private static final Logger logger = LoggerFactory.getLogger(TaskProfileTest.class);
	private static final ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
	private static final String RESOURCE_VERSION = definition.getResourceVersion();
	private static final LocalDate RELEASE_DATE = definition.getReleaseDate();

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(RESOURCE_VERSION, RELEASE_DATE,
			Arrays.asList("dsf-task-2.0.0.xml", "task-start-dic-process.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-bpmn-message-2.0.0.xml", "tutorial.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-bpmn-message-2.0.0.xml", "tutorial.xml"));

	private ResourceValidator resourceValidator = new ResourceValidatorImpl(validationRule.getFhirContext(),
			validationRule.getValidationSupport());

	@Test
	public void testTaskStartDicProcessValid()
	{
		Task task = createValidTaskStartDicProcess();

		ValidationResult result = resourceValidator.validate(task);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0,
				result.getMessages().stream()
						.filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
								|| ResultSeverityEnum.FATAL.equals(m.getSeverity()))
						.peek(m -> logger.error(m.getMessage())).count());
	}

	private Task createValidTaskStartDicProcess()
	{
		Task task = new Task();
		task.getMeta().addProfile(PROFILE_TUTORIAL_TASK_DIC_PROCESS);
		task.setInstantiatesCanonical(PROFILE_TUTORIAL_TASK_DIC_PROCESS_INSTANTIATES_CANONICAL);
		task.setStatus(TaskStatus.REQUESTED);
		task.setIntent(TaskIntent.ORDER);
		task.setAuthoredOn(new Date());
		task.getRequester().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("dic.dsf.test");
		task.getRestriction().addRecipient().setType(ResourceType.Organization.name()).getIdentifier()
				.setSystem(NamingSystems.OrganizationIdentifier.SID).setValue("dic.dsf.test");

		task.addInput().setValue(new StringType(PROFILE_TUTORIAL_TASK_DIC_PROCESS_MESSAGE_NAME)).getType()
				.addCoding(CodeSystems.BpmnMessage.messageName());
		task.addInput().setValue(new StringType("Tutorial input")).getType().addCoding()
				.setSystem("http://tutorial.org/fhir/CodeSystem/tutorial").setCode("tutorial-input");

		return task;
	}
}
