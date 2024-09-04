package dev.dsf.process.tutorial.listener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.constants.CodeSystems.BpmnMessage;
import dev.dsf.bpe.v1.constants.CodeSystems.BpmnUserTask;
import dev.dsf.bpe.v1.variables.Variables;

public class CustomUserTaskListener implements TaskListener, InitializingBean
{
	private static final Logger logger = LoggerFactory.getLogger(dev.dsf.bpe.v1.activity.DefaultUserTaskListener.class);

	private final ProcessPluginApi api;
	private String questionnaireUrlWithVersion;
	private Questionnaire questionnaire;
	private boolean uploadQuestionnaire;

	public CustomUserTaskListener(ProcessPluginApi api)
	{
		this.api = api;
		questionnaireUrlWithVersion = "";
		questionnaire = null;
		uploadQuestionnaire = false;
	}

	public CustomUserTaskListener(ProcessPluginApi api, Questionnaire questionnaire)
	{
		this.api = api;
		questionnaireUrlWithVersion = "";
		this.questionnaire = questionnaire;
		uploadQuestionnaire = true;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Objects.requireNonNull(api, "api");
	}

	protected String readQuestionnaireUrlWithVersion(ProcessPluginApi api, DelegateTask userTask)
	{
		return userTask.getBpmnModelElementInstance().getCamundaFormKey();
	}

	@Override
	public final void notify(DelegateTask userTask)
	{
		final DelegateExecution execution = userTask.getExecution();
		final Variables variables = api.getVariables(execution);

		try
		{
			logger.trace("Execution of user task with id='{}'", execution.getCurrentActivityId());

			if (Objects.isNull(questionnaire))
			{
				questionnaireUrlWithVersion = readQuestionnaireUrlWithVersion(api, userTask);
				questionnaire = readQuestionnaire(questionnaireUrlWithVersion);
			} else
			{
				questionnaireUrlWithVersion = questionnaire.getUrl().concat("|").concat(questionnaire.getVersion());
			}

			String businessKey = execution.getBusinessKey();
			String userTaskId = userTask.getId();

			QuestionnaireResponse questionnaireResponse = createDefaultQuestionnaireResponse(
					questionnaireUrlWithVersion, businessKey, userTaskId);
			transformQuestionnaireItemsToQuestionnaireResponseItems(questionnaireResponse, questionnaire);

			beforeQuestionnaireResponseCreate(userTask, questionnaireResponse);
			checkQuestionnaireResponse(questionnaireResponse);

			QuestionnaireResponse created = upload(questionnaireResponse);

			afterQuestionnaireResponseCreate(userTask, created);
		}
		catch (Exception exception)
		{
			logger.debug("Error while executing user task listener {}", getClass().getName(), exception);
			logger.error("Process {} has fatal error in step {} for task {}, reason: {} - {}",
					execution.getProcessDefinitionId(), execution.getActivityInstanceId(),
					api.getTaskHelper().getLocalVersionlessAbsoluteUrl(variables.getStartTask()),
					exception.getClass().getName(), exception.getMessage());

			String errorMessage = "Process " + execution.getProcessDefinitionId() + " has fatal error in step "
					+ execution.getActivityInstanceId() + ", reason: " + exception.getMessage();

			updateFailedIfInprogress(variables.getTasks(), errorMessage);

			// TODO evaluate throwing exception as alternative to stopping the process instance
			execution.getProcessEngine().getRuntimeService().deleteProcessInstance(execution.getProcessInstanceId(),
					exception.getMessage());
		}
	}

	private Questionnaire readQuestionnaire(String urlWithVersion)
	{
		Bundle search = api.getFhirWebserviceClientProvider().getLocalWebserviceClient().search(Questionnaire.class,
				Map.of("url", Collections.singletonList(urlWithVersion)));

		List<Questionnaire> questionnaires = search.getEntry().stream().filter(Bundle.BundleEntryComponent::hasResource)
				.map(Bundle.BundleEntryComponent::getResource).filter(r -> r instanceof Questionnaire)
				.map(r -> (Questionnaire) r).collect(Collectors.toList());

		if (questionnaires.size() < 1)
			throw new RuntimeException("Could not find Questionnaire resource with url|version=" + urlWithVersion);

		if (questionnaires.size() > 1)
			logger.info("Found {} Questionnaire resources with url|version={}, using the first", questionnaires.size(),
					urlWithVersion);

		return questionnaires.get(0);
	}


	private QuestionnaireResponse createDefaultQuestionnaireResponse(String questionnaireUrlWithVersion,
			String businessKey, String userTaskId)
	{
		QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
		questionnaireResponse.setQuestionnaire(questionnaireUrlWithVersion);
		questionnaireResponse.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS);

		questionnaireResponse.setAuthor(new Reference().setType(ResourceType.Organization.name())
				.setIdentifier(api.getOrganizationProvider().getLocalOrganizationIdentifier()
						.orElseThrow(() -> new IllegalStateException("Local organization identifier unknown"))));

		api.getQuestionnaireResponseHelper().addItemLeafWithAnswer(questionnaireResponse,
				BpmnUserTask.Codes.BUSINESS_KEY, "The business-key of the process execution",
				new StringType(businessKey));

		api.getQuestionnaireResponseHelper().addItemLeafWithAnswer(questionnaireResponse,
				BpmnUserTask.Codes.USER_TASK_ID, "The user-task-id of the process execution",
				new StringType(userTaskId));

		return questionnaireResponse;
	}

	private void transformQuestionnaireItemsToQuestionnaireResponseItems(QuestionnaireResponse questionnaireResponse,
			Questionnaire questionnaire)
	{
		questionnaire.getItem().stream().filter(i -> !BpmnUserTask.Codes.BUSINESS_KEY.equals(i.getLinkId()))
				.filter(i -> !BpmnUserTask.Codes.USER_TASK_ID.equals(i.getLinkId()))
				.forEach(i -> transformItem(questionnaireResponse, i));
	}

	private void transformItem(QuestionnaireResponse questionnaireResponse,
			Questionnaire.QuestionnaireItemComponent question)
	{
		if (Questionnaire.QuestionnaireItemType.DISPLAY.equals(question.getType()))
		{
			api.getQuestionnaireResponseHelper().addItemLeafWithoutAnswer(questionnaireResponse, question.getLinkId(),
					question.getText());
		}
		else
		{
			Type answer = api.getQuestionnaireResponseHelper().transformQuestionTypeToAnswerType(question);
			api.getQuestionnaireResponseHelper().addItemLeafWithAnswer(questionnaireResponse, question.getLinkId(),
					question.getText(), answer);
		}
	}

	private void checkQuestionnaireResponse(QuestionnaireResponse questionnaireResponse)
	{
		questionnaireResponse.getItem().stream().filter(i -> BpmnUserTask.Codes.BUSINESS_KEY.equals(i.getLinkId()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("QuestionnaireResponse does not contain an item with linkId='"
						+ BpmnUserTask.Codes.BUSINESS_KEY + "'"));

		questionnaireResponse.getItem().stream().filter(i -> BpmnUserTask.Codes.USER_TASK_ID.equals(i.getLinkId()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("QuestionnaireResponse does not contain an item with linkId='"
						+ BpmnUserTask.Codes.USER_TASK_ID + "'"));

		if (!QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS.equals(questionnaireResponse.getStatus()))
			throw new RuntimeException("QuestionnaireResponse must be in status 'in-progress'");
	}

	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse beforeCreate)
	{
		// Nothing to do in default behavior
	}

	protected void afterQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse afterCreate)
	{
		// Nothing to do in default behavior
	}

	private void updateFailedIfInprogress(List<Task> tasks, String errorMessage)
	{
		for (int i = tasks.size() - 1; i >= 0; i--)
		{
			Task task = tasks.get(i);

			if (TaskStatus.INPROGRESS.equals(task.getStatus()))
			{
				task.setStatus(Task.TaskStatus.FAILED);
				task.addOutput(new TaskOutputComponent(new CodeableConcept(BpmnMessage.error()),
						new StringType(errorMessage)));
				updateAndHandleException(task);
			}
			else
			{
				logger.debug("Not updating Task {} with status: {}",
						api.getTaskHelper().getLocalVersionlessAbsoluteUrl(task), task.getStatus());
			}
		}
	}

	private void updateAndHandleException(Task task)
	{
		try
		{
			logger.debug("Updating Task {}, new status: {}", api.getTaskHelper().getLocalVersionlessAbsoluteUrl(task),
					task.getStatus().toCode());

			api.getFhirWebserviceClientProvider().getLocalWebserviceClient().withMinimalReturn().update(task);
		}
		catch (Exception e)
		{
			logger.debug("Unable to update Task {}", api.getTaskHelper().getLocalVersionlessAbsoluteUrl(task), e);
			logger.error("Unable to update Task {}: {} - {}", api.getTaskHelper().getLocalVersionlessAbsoluteUrl(task),
					e.getClass().getName(), e.getMessage());
		}
	}

	private QuestionnaireResponse upload(QuestionnaireResponse questionnaireResponse)
	{
		Bundle bundle = new Bundle();
		bundle.setType(Bundle.BundleType.TRANSACTION);
		if (uploadQuestionnaire)
		{
			bundle.addEntry()
					.setResource(questionnaire)
					.setFullUrl("urn:uuid:" + questionnaire.getId())
					.getRequest().setMethod(Bundle.HTTPVerb.POST).setUrl(ResourceType.Questionnaire.name());
		}
		bundle.addEntry()
				.setResource(questionnaireResponse)
				.setFullUrl("urn:uuid:" + questionnaireResponse.getId())
				.getRequest().setMethod(Bundle.HTTPVerb.POST).setUrl(ResourceType.QuestionnaireResponse.name());

		Bundle created = api.getFhirWebserviceClientProvider().getLocalWebserviceClient().withRetryForever(60000).postBundle(bundle);

		String resourcesInBundle = uploadQuestionnaire ? "Questionnaire and QuestionnaireResponse" : "QuestionnaireResponse";
		logger.info("Created Bundle with {} for user task at {}, process waiting for it's completion",
				resourcesInBundle, created.getIdElement().toVersionless()
						.withServerBase(api.getFhirWebserviceClientProvider().getLocalWebserviceClient().getBaseUrl(),
								ResourceType.Task.name()).getValue());

		return created.getEntry().stream()
				.filter(entry -> entry.getResource().getResourceType().equals(ResourceType.QuestionnaireResponse))
				.map(entry -> (QuestionnaireResponse) entry.getResource())
				.findFirst().orElse(null);
	}
}