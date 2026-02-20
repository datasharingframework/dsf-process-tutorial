package org.tutorial.process.tutorial.exercise_1.service;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tutorial.process.tutorial.service.DicTask;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.bpe.v2.variables.Variables;

@RunWith(MockitoJUnitRunner.class)
public class DicTaskServiceTest
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Mock
	private DelegateExecution execution;

	@Mock
	private Variables variables;

	@Mock
	private ProcessPluginApi api;

	@InjectMocks
	private DicTask service;

	@Test
	public void testDicTaskServiceValid() throws Exception
	{
		Mockito.lenient().when(variables.getStartTask()).thenReturn(getTask());
		Mockito.lenient().when(variables.getLatestTask()).thenReturn(null); // latest task only refers to tasks received
																			// after an intermediate message catch
																			// event. Therefore, the start task is not
																			// returned by getLatestTask
		Mockito.lenient().when(variables.getTasks()).thenReturn(List.of(getTask()));
		Mockito.lenient().when(variables.getCurrentTasks()).thenReturn(Collections.emptyList()); // getCurrentTasks
																									// doesn't return
																									// the start task

		try
		{
			service.execute(api, variables);
		}
		catch (NullPointerException e)
		{
			logger.error(
					"You might see this error message when calling 'getCurrentTasks' or 'getLatestTask' in your service delegate because those methods return an empty list in case of 'getCurrentTasks' or null in case of 'getLatestTask'. You shouldn't be using them here. Check their Java Doc for more information");
		}

		String errorMessage = "getLatestTask only refers to tasks received after an intermediate message catch event. Therefore, the start task is not returned by getLatestTask. GetLatestTask cannot be used here.";
		Mockito.verify(variables, Mockito.never().description(errorMessage)).getLatestTask();

		errorMessage = "getCurrentTasks doesn't include the start task in the list it returns.";
		Mockito.verify(variables, Mockito.never().description(errorMessage)).getCurrentTasks();

		int numCallsGetStartTask = (int) new DefaultMockingDetails(variables).getInvocations().stream()
				.map(InvocationOnMock::getMethod).filter(method -> method.getName().equals("getStartTask")).count();
		int numCallsGetTasks = (int) new DefaultMockingDetails(variables).getInvocations().stream()
				.map(InvocationOnMock::getMethod).filter(method -> method.getName().equals("getTasks")).count();

		if (numCallsGetTasks > 0)
			logger.warn("You don't have to use 'getTasks' to find the start task. Try using 'getStartTask'.");

		errorMessage = "Expected getStartTask or getTasks to be called at least once.";
		assertTrue(errorMessage, numCallsGetStartTask > 0 || numCallsGetTasks > 0);
	}

	private Task getTask()
	{
		Task task = new Task();
		task.getRestriction().addRecipient().getIdentifier().setSystem(NamingSystems.OrganizationIdentifier.SID)
				.setValue("MeDIC");

		return task;
	}
}
