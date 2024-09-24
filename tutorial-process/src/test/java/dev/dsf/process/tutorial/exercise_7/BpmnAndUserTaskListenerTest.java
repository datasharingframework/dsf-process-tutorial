package dev.dsf.process.tutorial.exercise_7;

import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS;
import static dev.dsf.process.tutorial.ConstantsTutorial.CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Task;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.junit.MockitoJUnitRunner;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.ProcessPluginDefinition;
import dev.dsf.bpe.v1.activity.DefaultUserTaskListener;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;
import dev.dsf.bpe.v1.service.TaskHelper;
import dev.dsf.bpe.v1.variables.Variables;
import dev.dsf.process.tutorial.ConstantsTutorial;
import dev.dsf.process.tutorial.TestProcessPluginGenerator;
import dev.dsf.process.tutorial.TutorialProcessPluginDefinition;

@RunWith(MockitoJUnitRunner.class)
public class BpmnAndUserTaskListenerTest
{
	@BeforeClass
	public static void loadResources()
	{
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		ProcessPluginImpl processPlugin = TestProcessPluginGenerator.generate(definition, false,
				BpmnAndUserTaskListenerTest.class);
		boolean initialized = processPlugin
				.initializeAndValidateResources(ConstantsTutorial.TUTORIAL_DIC_ORGANIZATION_IDENTIFIER);

		assertEquals(true, initialized);
	}

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

		String errorMissingUserTask = "Process '" + processId + "' in file '" + filename + "is missing a User Task";
		int userTaskCount = process.getChildElementsByType(UserTask.class).size();
		assertTrue(errorMissingUserTask, userTaskCount > 0);

		String errorMissingCorrectUserTask = "Process '" + processId + "' in file '" + filename
				+ " is missing User Task with incoming flow from exclusive gateway with name 'User Vote?' and outgoing flow to service task with name 'Save User Vote'";
		Optional<UserTask> optUserTask = process.getChildElementsByType(UserTask.class).stream()
				.filter(userTask -> userTask.getIncoming().stream()
						.anyMatch(isFlowConnectingUserTaskAndExclusiveGateway(userTask)))
				.filter(userTask -> userTask.getOutgoing().stream()
						.anyMatch(isFlowConnectingUserTaskAndSaveUserVoteServer(userTask)))
				.findFirst();
		assertTrue(errorMissingCorrectUserTask, optUserTask.isPresent());
		UserTask userTask = optUserTask.get();

		String errorUserTaskIncomingFlowMissingCondition = "User Task in process '" + processId + "' in file '"
				+ filename + " with name " + userTask.getOutgoing()
				+ " is missing condition expression '${userVote}' on incoming flow from exclusive gateway with name 'User Vote?'";
		assertTrue(errorUserTaskIncomingFlowMissingCondition,
				userTask.getIncoming().stream().filter(isFlowConnectingUserTaskAndExclusiveGateway(userTask))
						.allMatch(hasCorrectConditionExpression()));

		String errorUserTaskIsMissingCorrectFormKey = "User Task in process '" + processId + "' in file '" + filename
				+ " with name " + userTask.getOutgoing() + " is missing Form Key with value " + questionnaireUrl;
		assertEquals(errorUserTaskIsMissingCorrectFormKey, userTask.getCamundaFormKey(), questionnaireUrl);

		String packageName = "dev.dsf.process.tutorial.listener";
		String errorNoUserTaskListenerFound = "No class extending DefaultUserTaskListener found in package '"
				+ packageName + "'. Unable to verify if User Task has correct Task Listener set.";
		List<Class<? extends DefaultUserTaskListener>> userTaskListeners = Utils.getUserTaskListeners(packageName);
		assertTrue(errorNoUserTaskListenerFound, !userTaskListeners.isEmpty());

		String errorUserTaskIsMissingTaskListener = "User Task in process '" + processId + "' in file '" + filename
				+ " with name " + userTask.getOutgoing()
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
				.forEach(userTaskListener -> assertEquals(Utils.errorMessageBeanMethod(userTaskListener), 1,
						Utils.countBeanMethods(userTaskListener)));

		Map<Class<? extends DefaultUserTaskListener>, List<String>> userTaskListenersWithErrors = userTaskListenersInUserTask
				.stream()
				.collect(Collectors.toMap(userTaskListener -> userTaskListener, this::validateUserTaskListener));

		String errorNoTaskListenerInUserTaskIsValid = "User Task in process '" + processId + "' in file '" + filename
				+ " with name " + userTask.getOutgoing()
				+ " is missing at least one valid UserTaskListener. Errors are: \n";
		errorNoTaskListenerInUserTaskIsValid += userTaskListenersWithErrors.keySet().stream()
				.map(key -> formatErrors(key, userTaskListenersWithErrors.get(key))).collect(Collectors.joining());

		assertTrue(errorNoTaskListenerInUserTaskIsValid, userTaskListenersWithErrors.keySet().stream()
				.anyMatch(userTaskListener -> userTaskListenersWithErrors.get(userTaskListener).isEmpty()));
	}

	private Predicate<SequenceFlow> isFlowConnectingUserTaskAndExclusiveGateway(UserTask userTask)
	{
		return flow -> flow.getTarget().equals(userTask) && flow.getSource() instanceof ExclusiveGateway
				&& flow.getSource().getName().equals("User Vote?");
	}

	private Predicate<SequenceFlow> hasCorrectConditionExpression()
	{
		return flow -> flow.getConditionExpression().getTextContent().equals("${userVote}");
	}

	private Predicate<SequenceFlow> isFlowConnectingUserTaskAndSaveUserVoteServer(UserTask userTask)
	{
		return flow -> flow.getSource().equals(userTask) && flow.getTarget() instanceof ServiceTask
				&& flow.getTarget().getName().equals("Save User Vote");
	}

	private String formatErrors(Class<? extends DefaultUserTaskListener> userTaskListener, List<String> errors)
	{
		String formatted = "";

		formatted += "Class: " + userTaskListener.getSimpleName() + "\n";
		formatted += "  Errors:\n" + errors.stream().reduce("", (i, next) -> i + "   " + next + "\n");

		return formatted;
	}

	// A UserTaskListener ist considered valid if beforeQuestionnaireResponseCreate() reads the input parameter
	// 'binary-question' from the Start Task and set the item.text value of the item with linkId 'binary-question'
	// to the value of the input parameter in the QuestionnaireResponse
	private List<String> validateUserTaskListener(Class<? extends DefaultUserTaskListener> userTaskListenerClass)
	{
		List<String> errors = new ArrayList<>();
		try
		{
			Constructor<? extends DefaultUserTaskListener> constructor = userTaskListenerClass
					.getConstructor(ProcessPluginApi.class);

			ProcessPluginApi apiMock = Mockito.mock(ProcessPluginApi.class);
			TaskHelper taskHelperMock = Mockito.mock(TaskHelper.class);
			DelegateTask taskMock = Mockito.mock(DelegateTask.class);
			QuestionnaireResponse questionnaireResponseMock = Mockito.mock(QuestionnaireResponse.class);
			Variables variablesMock = Mockito.mock(Variables.class);
			Task startTaskMock = Mockito.mock(Task.class);
			QuestionnaireResponse.QuestionnaireResponseItemComponent itemMock = Mockito
					.mock(QuestionnaireResponse.QuestionnaireResponseItemComponent.class);

			String binaryQuestion = "test?";

			Mockito.lenient().when(apiMock.getVariables(any())).thenReturn(variablesMock);
			Mockito.lenient().when(apiMock.getTaskHelper()).thenReturn(taskHelperMock);
			Mockito.lenient().when(variablesMock.getStartTask()).thenReturn(startTaskMock);
			Mockito.lenient().when(taskHelperMock.getFirstInputParameterStringValue(startTaskMock,
					CODESYSTEM_VOTING_PROCESS, CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION))
					.thenReturn(Optional.of(binaryQuestion));
			Mockito.lenient().when(questionnaireResponseMock.getItem()).thenReturn(List.of(itemMock));
			Mockito.lenient().when(itemMock.getLinkId()).thenReturn(CODESYSTEM_VOTING_PROCESS_VALUE_BINARY_QUESTION);
			Mockito.lenient().when(itemMock.getText()).thenReturn("foo");
			Mockito.lenient().when(itemMock.hasText()).thenReturn(true);

			DefaultUserTaskListener listenerSpy = Mockito.spy(constructor.newInstance(apiMock));
			Method method = userTaskListenerClass.getDeclaredMethod("beforeQuestionnaireResponseCreate",
					DelegateTask.class, QuestionnaireResponse.class);
			method.setAccessible(true);
			method.invoke(listenerSpy, taskMock, questionnaireResponseMock);

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
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e)
		{
			throw new RuntimeException(e);
		}

		return errors;
	}
}