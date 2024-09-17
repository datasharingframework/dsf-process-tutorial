package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_QUESTIONNAIRE_RESPONSE_DOWNLOAD_SERVER_URL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_QUESTIONNAIRE_RESPONSE_ID;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_VARIABLE_QUESTIONNAIRE_RESPONSE;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.UrlType;
import org.hl7.fhir.r4.model.UuidType;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.service.TaskHelper;
import dev.dsf.bpe.v1.variables.Variables;

public class DownloadQuestionnaireResponse extends AbstractServiceDelegate
{
	public DownloadQuestionnaireResponse(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables) throws BpmnError, Exception
	{
		TaskHelper taskHelper = api.getTaskHelper();
		Task latest = variables.getLatestTask();
		String downloadUrl = taskHelper.getFirstInputParameterValue(latest, CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_QUESTIONNAIRE_RESPONSE_DOWNLOAD_SERVER_URL, UrlType.class).get().getValue();
		String questionnaireResponseId = taskHelper.getFirstInputParameterValue(latest, CODESYSTEM_TUTORIAL,
				CODESYSTEM_TUTORIAL_QUESTIONNAIRE_RESPONSE_ID, StringType.class).get().getValue();

		QuestionnaireResponse questionnaireResponse = api.getFhirWebserviceClientProvider().getWebserviceClient(downloadUrl).read(QuestionnaireResponse.class, questionnaireResponseId);
		variables.setResource(PROCESS_VARIABLE_QUESTIONNAIRE_RESPONSE, questionnaireResponse);
	}
}
