package dev.dsf.process.tutorial.exercise_7;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.junit.MockitoJUnitRunner;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.DefaultUserTaskListener;
import dev.dsf.bpe.v2.activity.values.CreateQuestionnaireResponseValues;
import dev.dsf.bpe.v2.service.TaskHelper;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.process.tutorial.util.Misc;

@RunWith(MockitoJUnitRunner.class)
public class BpmnAndUserTaskListenerTest
{
	@Test
	public void testVoteBpmnFile()
	{
		String filename = "bpe/vote.bpmn";
		String processId = "dsfdev_vote";
		String questionnaireUrl = "http://dsf.dev/fhir/Questionnaire/user-vote|#{version}";

		BpmnModelInstance model = Bpmn
				.readModelFromStream(this.getClass().getClassLoader().getResourceAsStream(filename));
		assertNotNull(model);

		List<Process> processes = model.getModelElementsByType(Process.class).stream()
				.filter(p -> processId.equals(p.getId())).collect(Collectors.toList());
		assertEquals(1, processes.size());

		Process process = processes.get(0);

		String errorMissingUserTask = "Process '" + processId + "' in file '" + filename + "' is missing a User Task";
		int userTaskCount = process.getChildElementsByType(UserTask.class).size();
		assertTrue(errorMissingUserTask, userTaskCount > 0);

		String errorMissingCorrectUserTask = "Process '" + processId + "' in file '" + filename
				+ " is missing User Task with incoming flow from exclusive gateway with name 'User Vote?' and outgoing flow to service task with name 'Save User Vote'";
		Optional<UserTask> optUserTask = process.getChildElementsByType(UserTask.class).stream()
				.filter(userTask -> userTask.getIncoming().stream()
						.anyMatch(isFlowConnectingUserTaskAndExclusiveGateway(userTask)))
				.filter(userTask -> userTask.getOutgoing().stream()
						.anyMatch(isFlowConnectingUserTaskAndSaveUserVoteServiceTask(userTask)))
				.findFirst();
		assertTrue(errorMissingCorrectUserTask, optUserTask.isPresent());
		UserTask userTask = optUserTask.get();

		String errorUserTaskIncomingFlowMissingCondition = "User Task in process '" + processId + "' in file '"
				+ filename + "' with name " + userTask.getName()
				+ " is missing condition expression '${userVote}' on incoming flow from exclusive gateway with name 'User Vote?'";
		assertTrue(errorUserTaskIncomingFlowMissingCondition,
				userTask.getIncoming().stream().filter(isFlowConnectingUserTaskAndExclusiveGateway(userTask))
						.allMatch(hasCorrectConditionExpression()));

		String errorUserTaskIsMissingCorrectFormKey = "User Task in process '" + processId + "' in file '" + filename
				+ "' with name " + userTask.getName() + " is missing Form Key with value " + questionnaireUrl;
		assertEquals(errorUserTaskIsMissingCorrectFormKey, questionnaireUrl, userTask.getCamundaFormKey());

		String packageName = "dev.dsf.process.tutorial.listener";
		String errorNoUserTaskListenerFound = "No class extending DefaultUserTaskListener found in package '"
				+ packageName + "'. Unable to verify if User Task has correct Task Listener set.";
		List<Class<? extends DefaultUserTaskListener>> userTaskListeners = Misc.getUserTaskListeners(packageName);
		assertTrue(errorNoUserTaskListenerFound, !userTaskListeners.isEmpty());

		String errorUserTaskIsMissingTaskListener = "User Task in process '" + processId + "' in file '" + filename
				+ "' with name " + userTask.getName()
				+ " is missing at least one Task Listener which extends DefaultUserTaskListener. Found classes to add which extend DefaultUserTaskListener: "
				+ userTaskListeners.stream().map(Class::getSimpleName).reduce("", (i, next) -> i + next + " ");
		List<CamundaTaskListener> camundaTaskListeners = userTask
				.getExtensionElements().getElements().stream().filter(
						extensionElement -> extensionElement instanceof CamundaTaskListener)
				.map(extensionElement -> (CamundaTaskListener) extensionElement)
				.filter(camundaTaskListener -> userTaskListeners.stream().anyMatch(userTaskListener -> userTaskListener
						.getName().equals(camundaTaskListener.getAttributeValue("class"))))
				.toList();
		assertTrue(errorUserTaskIsMissingTaskListener, !camundaTaskListeners.isEmpty());

		List<Class<? extends DefaultUserTaskListener>> userTaskListenersInUserTask = userTaskListeners.stream().filter(
				userTaskListener -> camundaTaskListeners.stream().anyMatch(camundaTaskListener -> camundaTaskListener
						.getAttributeValue("class").equals(userTaskListener.getName())))
				.toList();
		userTaskListenersInUserTask
				.forEach(userTaskListener -> assertEquals(Misc.errorMessageBeanMethod(userTaskListener), 1,
						Misc.countBeanMethods(userTaskListener)));

		Map<Class<? extends DefaultUserTaskListener>, List<String>> userTaskListenersWithErrors = userTaskListenersInUserTask
				.stream()
				.collect(Collectors.toMap(userTaskListener -> userTaskListener, this::validateUserTaskListener));

		String errorNoTaskListenerInUserTaskIsValid = "User Task in process '" + processId + "' in file '" + filename
				+ " with name " + userTask.getName()
				+ "' is missing at least one valid UserTaskListener. Errors are: \n";
		errorNoTaskListenerInUserTaskIsValid += userTaskListenersWithErrors.keySet().stream()
				.map(key -> formatErrors(key, userTaskListenersWithErrors.get(key))).collect(Collectors.joining());

		assertTrue(errorNoTaskListenerInUserTaskIsValid, userTaskListenersWithErrors.keySet().stream()
				.anyMatch(userTaskListener -> userTaskListenersWithErrors.get(userTaskListener).isEmpty()));
	}

	private Predicate<SequenceFlow> isFlowConnectingUserTaskAndExclusiveGateway(UserTask userTask)
	{
		return flow ->
		{
			FlowNode target = flow.getTarget();
			FlowNode source = flow.getSource();
			if (Objects.nonNull(target) && Objects.nonNull(source))
			{
				return target.equals(userTask) && source instanceof ExclusiveGateway
						&& "User Vote?".equals(source.getName());
			}
			return false;
		};
	}

	private Predicate<SequenceFlow> hasCorrectConditionExpression()
	{
		return flow ->
		{
			ConditionExpression conditionExpression = flow.getConditionExpression();
			if (Objects.nonNull(conditionExpression))
				return "${userVote}".equals(conditionExpression.getTextContent());
			return false;
		};
	}

	private Predicate<SequenceFlow> isFlowConnectingUserTaskAndSaveUserVoteServiceTask(UserTask userTask)
	{
		return flow ->
		{
			FlowNode target = flow.getTarget();
			FlowNode source = flow.getSource();
			if (Objects.nonNull(target) && Objects.nonNull(source))
			{
				return source.equals(userTask) && target instanceof ServiceTask
						&& "Save User Vote".equals(target.getName());
			}
			return false;
		};
	}

	private String formatErrors(Class<? extends DefaultUserTaskListener> userTaskListener, List<String> errors)
	{
		String formatted = "";

		formatted += "Class: " + userTaskListener.getSimpleName() + "\n";
		formatted += errors.stream().reduce("", (i, next) -> i + "   " + next + "\n");

		return formatted;
	}

	// A UserTaskListener is considered valid if beforeQuestionnaireResponseCreate() reads the input parameter
	// 'binary-question' from the Start Task and set the item.text value of the item with linkId 'binary-question'
	// to the value of the input parameter in the QuestionnaireResponse
	private List<String> validateUserTaskListener(Class<? extends DefaultUserTaskListener> userTaskListenerClass)
	{
		List<String> errors = new ArrayList<>();
		try
		{
			Constructor<? extends DefaultUserTaskListener> constructor = userTaskListenerClass.getConstructor();

			ProcessPluginApi apiMock = Mockito.mock(ProcessPluginApi.class);
			TaskHelper taskHelperMock = Mockito.mock(TaskHelper.class);
			QuestionnaireResponse questionnaireResponseMock = Mockito.mock(QuestionnaireResponse.class);
			Variables variablesMock = Mockito.mock(Variables.class);
			Task startTaskMock = Mockito.mock(Task.class);
			QuestionnaireResponse.QuestionnaireResponseItemComponent itemMock = Mockito
					.mock(QuestionnaireResponse.QuestionnaireResponseItemComponent.class);

			String binaryQuestion = "test?";

			Mockito.lenient().when(apiMock.getTaskHelper()).thenReturn(taskHelperMock);
			Mockito.lenient().when(variablesMock.getStartTask()).thenReturn(startTaskMock);
			Mockito.lenient().when(taskHelperMock.getFirstInputParameterStringValue(startTaskMock,
					CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION))
					.thenReturn(Optional.of(binaryQuestion));
			Mockito.lenient().when(questionnaireResponseMock.getItem()).thenReturn(List.of(itemMock));
			Mockito.lenient().when(itemMock.getLinkId()).thenReturn(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION);
			Mockito.lenient().when(itemMock.getText()).thenReturn("foo");
			Mockito.lenient().when(itemMock.hasText()).thenReturn(true);

			DefaultUserTaskListener listenerSpy = Mockito.spy(constructor.newInstance());
			Method method = userTaskListenerClass.getDeclaredMethod("beforeQuestionnaireResponseCreate",
					ProcessPluginApi.class, Variables.class, CreateQuestionnaireResponseValues.class,
					QuestionnaireResponse.class);
			method.setAccessible(true);
			method.invoke(listenerSpy, apiMock, variablesMock, null, questionnaireResponseMock);

			Optional<Invocation> optionalInvocation = Mockito.mockingDetails(taskHelperMock).getInvocations().stream()
					.filter(invocation -> invocation.getMethod().getName().equals("getFirstInputParameterStringValue"))
					.filter(invocation -> invocation.getArguments()[0].equals(startTaskMock))
					.filter(invocation -> invocation.getArguments()[1].equals(CODESYSTEM_VOTING_PROCESS))
					.filter(invocation -> invocation.getArguments()[2]
							.equals(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION))
					.findFirst();
			if (optionalInvocation.isEmpty())
				errors.add(
						"Expected one call to TaskHelper#getFirstInputParameterStringValue for Start Task and CodeSystem '"
								+ CODESYSTEM_VOTING_PROCESS + "' and Code '"
								+ CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION + "'");

			optionalInvocation = Mockito.mockingDetails(itemMock).getInvocations().stream()
					.filter(invocation -> invocation.getMethod().getName().equals("setText"))
					.filter(invocation -> invocation.getArguments()[0].equals(binaryQuestion)).findFirst();
			if (optionalInvocation.isEmpty())
				errors.add(
						"Expected one call to QuestionnaireResponseItemComponent#setText for the QuestionnaireResponseItemComponent with linkId 'binary-question'");
		}
		catch (InvocationTargetException | IllegalAccessException | InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e)
		{
			String errorUserTaskListenerDoesNotOverrideMethod = "Expected override of method 'beforeQuestionnaireResponseCreate'";
			errors.add(errorUserTaskListenerDoesNotOverrideMethod);
		}
		return errors;
	}
}