package dev.dsf.process.tutorial.exercise_2.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;

import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
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
import dev.dsf.process.tutorial.util.Pom;
import dev.dsf.process.tutorial.util.ProcessAuthorizationHelperImpl;

public class ActivityDefinitionProfileTest
{
	private static final Logger logger = LoggerFactory.getLogger(ActivityDefinitionProfileTest.class);
	private static final String RESOURCE_VERSION;
	private static final LocalDate RELEASE_DATE;
	private static final Pom pom;

	static
	{
		try
		{
			pom = new Pom();
			RESOURCE_VERSION = pom.getResourceVersion();
			RELEASE_DATE = pom.getReleaseDate();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(RESOURCE_VERSION, RELEASE_DATE,
			Arrays.asList("dsf-activity-definition-2.0.0.xml", "dsf-extension-process-authorization-2.0.0.xml",
					"dsf-meta-2.0.0.xml", "dsf-extension-process-authorization-parent-organization-role-2.0.0.xml",
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
			Arrays.asList("dsf-process-authorization-2.0.0.xml", "dsf-read-access-tag-2.0.0.xml"),
			Arrays.asList("dsf-process-authorization-recipient-2.0.0.xml",
					"dsf-process-authorization-requester-2.0.0.xml", "dsf-read-access-tag-2.0.0.xml"));

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
				orgIdentifier -> true, orgRole -> true));
	}

	@Test
	public void testDicProcessRequester() throws Exception
	{
		ActivityDefinition ad = validationRule
				.readActivityDefinition(Paths.get("src/main/resources/fhir/ActivityDefinition/dic-process.xml"));

		Extension processAuthorization = ad
				.getExtensionByUrl("http://dsf.dev/fhir/StructureDefinition/extension-process-authorization");
		assertNotNull(processAuthorization);

		Extension requester = processAuthorization.getExtensionByUrl("requester");
		assertNotNull(requester);

		Type value = requester.getValue();
		assertTrue(value instanceof Coding);

		Coding coding = (Coding) value;
		assertEquals("http://dsf.dev/fhir/CodeSystem/process-authorization", coding.getSystem());
		assertEquals("LOCAL_ALL", coding.getCode());
	}
}
