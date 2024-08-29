package dev.dsf.process.tutorial.listener;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.beans.factory.InitializingBean;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.DefaultUserTaskListener;
import dev.dsf.process.tutorial.ConstantsTutorial;

public class QuestionnaireListener extends DefaultUserTaskListener implements InitializingBean
{
	public QuestionnaireListener(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		super.afterPropertiesSet();
	}

	@Override
	protected void beforeQuestionnaireResponseCreate(DelegateTask userTask, QuestionnaireResponse beforeCreate)
	{
		String question = (String) userTask.getExecution().getVariable(ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_BINARY_QUESTION);

		beforeCreate.getItem().stream().filter(item -> item.getLinkId().equals("vote")).findFirst().ifPresent(item -> item.setText(question));
	}
}
