package dev.dsf.process.tutorial.service;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_QUESTIONNAIRE_DOWNLOAD_SERVER_URL;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_QUESTIONNAIRE_ID;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_VARIABLE_QUESTIONNAIRE;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.UrlType;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.variables.Variables;

public class DownloadQuestionnaire extends AbstractServiceDelegate
{
	public DownloadQuestionnaire(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution execution, Variables variables) throws BpmnError, Exception
	{
		Task startTask = variables.getStartTask();
		String url = api.getTaskHelper().getFirstInputParameterValue(startTask, CODESYSTEM_TUTORIAL, CODESYSTEM_TUTORIAL_QUESTIONNAIRE_DOWNLOAD_SERVER_URL, UrlType.class).get().getValue();
		String questionnaireId = api.getTaskHelper().getFirstInputParameterValue(startTask, CODESYSTEM_TUTORIAL,
				CODESYSTEM_TUTORIAL_QUESTIONNAIRE_ID, StringType.class).get().getValue();
		Questionnaire questionnaire = api.getFhirWebserviceClientProvider().getWebserviceClient(url).withRetryForever(60000).read(Questionnaire.class, questionnaireId);
		questionnaire = api.getFhirWebserviceClientProvider().getLocalWebserviceClient().withRetryForever(60000).create(questionnaire);
		variables.setResource(PROCESS_VARIABLE_QUESTIONNAIRE, questionnaire);
	}
}
