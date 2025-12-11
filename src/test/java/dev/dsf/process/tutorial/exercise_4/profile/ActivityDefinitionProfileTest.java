package dev.dsf.process.tutorial.exercise_4.profile;

import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.RELEASE_DATE;
import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Type;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import dev.dsf.bpe.v2.service.process.ProcessAuthorizationHelper;
import dev.dsf.fhir.validation.ResourceValidator;
import dev.dsf.fhir.validation.ResourceValidatorImpl;
import dev.dsf.fhir.validation.ValidationSupportRule;
import dev.dsf.process.tutorial.util.ProcessAuthorizationHelperImpl;

public class ActivityDefinitionProfileTest
{
	private static final Logger logger = LoggerFactory.getLogger(ActivityDefinitionProfileTest.class);

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(VERSION, RELEASE_DATE,
			Arrays.asList("dsf-activity-definition-2.0.0.xml", "dsf-extension-process-authorization-2.0.0.xml",
					"dsf-extension-process-authorization-parent-organization-role-2.0.0.xml",
					"dsf-extension-process-authorization-parent-organization-role-practitioner-2.0.0.xml",
					"dsf-extension-process-authorization-organization-2.0.0.xml",
					"dsf-extension-process-authorization-organization-practitioner-2.0.0.xml",
					"dsf-extension-process-authorization-practitioner-2.0.0.xml",
					"dsf-coding-process-authorization-local-all-2.0.0.xml",
					"dsf-coding-process-authorization-local-all-practitioner-2.0.0.xml",
					"dsf-coding-process-authorization-local-parent-organization-role-2.0.0.xml",
					"dsf-coding-process-authorization-local-parent-organization-role-practitioner-2.0.0.xml",
					"dsf-coding-process-authorization-local-organization-2.0.0.xml",
					"dsf-coding-process-authorization-local-organization-practitioner-2.0.0.xml",
					"dsf-coding-process-authorization-remote-all-2.0.0.xml",
					"dsf-coding-process-authorization-remote-parent-organization-role-2.0.0.xml",
					"dsf-coding-process-authorization-remote-organization-2.0.0.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-process-authorization-2.0.0.xml"),
			Arrays.asList("dsf-read-access-tag-2.0.0.xml", "dsf-process-authorization-recipient-2.0.0.xml",
					"dsf-process-authorization-requester-2.0.0.xml"));

	private final ResourceValidator resourceValidator = new ResourceValidatorImpl(validationRule.getFhirContext(),
			validationRule.getValidationSupport());

	private final ProcessAuthorizationHelper processAuthorizationHelper = new ProcessAuthorizationHelperImpl();

	@Test
	public void testDicProcessValid() throws Exception
	{
		ActivityDefinition ad = validationRule
				.readActivityDefinition(Paths.get("src/main/resources/fhir/ActivityDefinition/dic-process.xml"));

		ValidationResult result = resourceValidator.validate(ad);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0, result.getMessages().stream().filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
				|| ResultSeverityEnum.FATAL.equals(m.getSeverity())).count());

		assertTrue(processAuthorizationHelper.isValid(ad, taskProfile -> true, practitionerRole -> true,
				orgIdentifier -> true, role -> true));
	}

	@Test
	public void testCosProcessValid() throws Exception
	{
		ActivityDefinition ad = validationRule
				.readActivityDefinition(Paths.get("src/main/resources/fhir/ActivityDefinition/cos-process.xml"));

		ValidationResult result = resourceValidator.validate(ad);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0, result.getMessages().stream().filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
				|| ResultSeverityEnum.FATAL.equals(m.getSeverity())).count());

		assertTrue(processAuthorizationHelper.isValid(ad, taskProfile -> true, practitionerRole -> true,
				orgIdentifier -> true, role -> true));
	}

	@Test
	public void testDicProcessRequester() throws Exception
	{
		ActivityDefinition ad = validationRule
				.readActivityDefinition(Paths.get("src/main/resources/fhir/ActivityDefinition/dic-process.xml"));

		Extension processAuthorization = ad
				.getExtensionByUrl("http://dsf.dev/fhir/StructureDefinition/extension-process-authorization");
		assertNotNull(processAuthorization);

		List<Extension> requesters = processAuthorization.getExtensionsByUrl("requester");
		assertTrue(requesters.size() == 2);

		Extension localAllPractitionerRequester = requesters.stream()
				.filter(r -> ((Coding) r.getValue()).getCode().equals("LOCAL_ALL_PRACTITIONER")).findFirst().get();

		Type value = localAllPractitionerRequester.getValue();
		assertTrue(value instanceof Coding);

		Coding coding = (Coding) value;
		assertEquals("http://dsf.dev/fhir/CodeSystem/process-authorization", coding.getSystem());
		assertEquals("LOCAL_ALL_PRACTITIONER", coding.getCode());

		Extension practitioner = coding.getExtensionByUrl(
				"http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-practitioner");
		assertNotNull(practitioner);

		value = practitioner.getValue();
		assertTrue(value instanceof Coding);

		coding = (Coding) value;
		assertEquals("http://dsf.dev/fhir/CodeSystem/practitioner-role", coding.getSystem());
		assertEquals("DSF_ADMIN", coding.getCode());

		Extension localOrganizationRequester = requesters.stream()
				.filter(r -> ((Coding) r.getValue()).getCode().equals("LOCAL_ORGANIZATION")).findFirst().get();

		value = localOrganizationRequester.getValue();
		assertTrue(value instanceof Coding);

		coding = (Coding) value;
		assertEquals("http://dsf.dev/fhir/CodeSystem/process-authorization", coding.getSystem());
		assertEquals("LOCAL_ORGANIZATION", coding.getCode());

		Extension organization = coding.getExtensionByUrl(
				"http://dsf.dev/fhir/StructureDefinition/extension-process-authorization-organization");
		assertNotNull(organization);

		value = organization.getValue();
		assertTrue(value instanceof Identifier);

		Identifier identifier = (Identifier) value;
		assertEquals("http://dsf.dev/sid/organization-identifier", identifier.getSystem());
		assertEquals("dic.dsf.test", identifier.getValue());
	}
}
