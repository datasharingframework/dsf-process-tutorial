package dev.dsf.process.tutorial.exercise_7.questionnaire;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_VOTE;
import static dev.dsf.process.tutorial.ConstantsTutorial.RESOURCE_VERSION;
import static dev.dsf.process.tutorial.TutorialProcessPluginDefinition.RELEASE_DATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import dev.dsf.fhir.validation.ResourceValidator;
import dev.dsf.fhir.validation.ResourceValidatorImpl;
import dev.dsf.fhir.validation.ValidationSupportRule;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;

public class QuestionnaireUserVoteTest
{
	Questionnaire userVote;

	private final Logger logger = LoggerFactory.getLogger(QuestionnaireUserVoteTest.class);

	@ClassRule
	public static final ValidationSupportRule validationRule = new ValidationSupportRule(RESOURCE_VERSION, RELEASE_DATE,
			Arrays.asList("dsf-task-base-1.0.0.xml", "dsf-questionnaire-1.5.0.xml"),
			Arrays.asList("dsf-read-access-tag-1.0.0.xml"), Arrays.asList("dsf-read-access-tag-1.0.0.xml"));

	private ResourceValidator resourceValidator = new ResourceValidatorImpl(validationRule.getFhirContext(),
			validationRule.getValidationSupport());

	@Before
	public void setUp() throws URISyntaxException, IOException
	{
		FhirContext ctx = FhirContext.forR4();

		Pattern versionPattern = Pattern.compile("#\\{version}");
		String versionReplacement = new TutorialProcessPluginDefinition().getVersion();

		Pattern datePattern = Pattern.compile("#\\{date}");
		String dateReplacement = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());

		IParser parser = ctx.newXmlParser();

		String xml;

		try (Stream<String> lines = Files.lines(
				Path.of(Objects.requireNonNull(getClass().getResource("/fhir/Questionnaire/user-vote.xml")).toURI())))
		{
			xml = lines.map(line -> versionPattern.matcher(line).replaceAll(versionReplacement))
					.map(line -> datePattern.matcher(line).replaceAll(dateReplacement)).collect(Collectors.joining());
		}
		catch (NullPointerException e)
		{
			throw new RuntimeException("Missing Questionnaire resource with file name 'user-vote.xml' in 'resources/fhir/Questionnaire'.");
		}

		userVote = parser.parseResource(Questionnaire.class, xml);
	}

	@Test
	public void testQuestionnaireIsValid()
	{
		ValidationResult result = resourceValidator.validate(userVote);
		ValidationSupportRule.logValidationMessages(logger, result);

		assertEquals(0,
				result.getMessages().stream()
						.filter(m -> ResultSeverityEnum.ERROR.equals(m.getSeverity())
								|| ResultSeverityEnum.FATAL.equals(m.getSeverity()))
						.peek(m -> logger.error(m.getMessage())).count());
	}

	@Test
	public void testQuestionnaireHasValidBinaryQuestionItem()
	{
		String errorNoItemWithLinkIdBinaryQuestion = "Questionnaire 'user-vote' is missing item with linkId 'binary-question'";
		Optional<Questionnaire.QuestionnaireItemComponent> optBinaryQuestion = userVote.getItem().stream()
				.filter(item -> item.getLinkId().equals(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION)).findAny();
		assertTrue(errorNoItemWithLinkIdBinaryQuestion, optBinaryQuestion.isPresent());
		Questionnaire.QuestionnaireItemComponent binaryQuestion = optBinaryQuestion.get();

		String errorItemWithLinkIdBinaryQuestionNotOfTypeDisplay = "Item with linkId 'binary-question' in Questionnaire 'user-vote' is of wrong type. Expected type 'display' but got '"
				+ binaryQuestion.getType().getDisplay().toLowerCase() + "'.";
		assertTrue(errorItemWithLinkIdBinaryQuestionNotOfTypeDisplay,
				binaryQuestion.getType().equals(Questionnaire.QuestionnaireItemType.DISPLAY));
	}

	@Test
	public void testQuestionnaireHasValidVoteItem()
	{
		String errorNoItemWithLinkIdVote = "Questionnaire 'user-vote' is missing item with linkId 'vote'";
		Optional<Questionnaire.QuestionnaireItemComponent> optVote = userVote.getItem().stream()
				.filter(item -> item.getLinkId().equals(CODESYSTEM_VOTING_PROCESS_VOTE)).findAny();
		assertTrue(errorNoItemWithLinkIdVote, optVote.isPresent());
		Questionnaire.QuestionnaireItemComponent vote = optVote.get();

		String errorItemWithLinkIdVoteNotOfTypeBoolean = "Item with linkId 'vote' in Questionnaire 'user-vote' is of wrong type. Expected type 'boolean' but got '"
				+ vote.getType().getDisplay().toLowerCase() + "'.";
		assertTrue(errorItemWithLinkIdVoteNotOfTypeBoolean,
				vote.getType().equals(Questionnaire.QuestionnaireItemType.BOOLEAN));

		String errorItemWithLinkIdVoteNotRequired = "Item with linkId 'vote' in Questionnaire 'user-vote' must have 'required' set to 'true'";
		assertTrue(errorItemWithLinkIdVoteNotRequired, vote.getRequired());
	}

	@Test
	public void testQuestionnaireHasCorrectUrl()
	{
		String correctUrl = "http://dsf.dev/fhir/Questionnaire/user-vote";
		String errorWrongUrl = "Questionnaire 'user-vote' has wrong URL.";
		assertEquals(errorWrongUrl, correctUrl, userVote.getUrl());
	}

	@Test
	public void testQuestionnaireInProcessPluginDefinition()
	{
		String questionnairePath = "fhir/Questionnaire/user-vote.xml";
		TutorialProcessPluginDefinition tutorialProcessPluginDefinition = new TutorialProcessPluginDefinition();
		String errorQuestionnaireNotDefinedAsResourceForVoteProcess = "The process '" + PROCESS_NAME_FULL_VOTE
				+ "' is missing path to Questionnaire resource 'user-vote' in the process plugin definition.";
		assertTrue(errorQuestionnaireNotDefinedAsResourceForVoteProcess,
				tutorialProcessPluginDefinition.getFhirResourcesByProcessId().get(PROCESS_NAME_FULL_VOTE).stream()
						.anyMatch(path -> path.equals(questionnairePath)));
	}
}
