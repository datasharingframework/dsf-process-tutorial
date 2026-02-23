package org.tutorial.process.tutorial.exercise_2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL;
import static org.tutorial.process.tutorial.ConstantsTutorial.CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tutorial.process.tutorial.ConstantsTutorial;
import org.tutorial.process.tutorial.TutorialProcessPluginDefinition;
import org.tutorial.process.tutorial.service.DicTask;
import org.tutorial.process.tutorial.util.FhirResourceLoader;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.bpe.v2.service.TaskHelper;
import dev.dsf.bpe.v2.variables.Variables;

@RunWith(MockitoJUnitRunner.class)
public class DicTaskServiceTest
{

	public static final String GET_FIRST_INPUT_PARAMETER_STRING_VALUE = "getFirstInputParameterStringValue";
	public static final String GET_FIRST_INPUT_PARAMETER_VALUE = "getFirstInputParameterValue";
	public static final String GET_FIRST_INPUT_PARAMETER = "getFirstInputParameter";
	public static final String GET_INPUT_PARAMETER_STRING_VALUES = "getInputParameterStringValues";
	public static final String GET_INPUT_PARAMETER_VALUES = "getInputParameterValues";
	public static final String GET_INPUT_PARAMETERS = "getInputParameters";
	@Mock
	private TaskHelper taskHelperMock;

	@Mock
	private ProcessPluginApi apiMock;

	@Mock
	private Variables variablesMock;

	@Mock
	private Task.ParameterComponent parameterComponentMock;

	@Mock
	private StringType stringTypeMock;
	private final String TEST_STRING = "Test";
	private final Logger logger = LoggerFactory.getLogger(DicTaskServiceTest.class.getName());
	private List<String> taskHelperMethodsToVerify = List.of(GET_FIRST_INPUT_PARAMETER_STRING_VALUE,
			GET_FIRST_INPUT_PARAMETER_VALUE, GET_FIRST_INPUT_PARAMETER, GET_INPUT_PARAMETER_STRING_VALUES,
			GET_INPUT_PARAMETER_VALUES, GET_INPUT_PARAMETERS);

	private Optional<Constructor<DicTask>> getConstructor(Class<?>... args)
	{
		try
		{
			return Optional.of(DicTask.class.getConstructor(args));
		}
		catch (NoSuchMethodException e)
		{
			return Optional.empty();
		}
		catch (SecurityException e)
		{
			throw e;
		}
	}

	@Test
	public void testDicTaskConstructorWithAdditionalBooleanParameterExists() throws Exception
	{
		Optional<Constructor<DicTask>> constructor = getConstructor(boolean.class);

		if (constructor.isEmpty())
		{
			String errorMessage = "One public constructor in class " + DicTask.class.getSimpleName()
					+ " with parameters (" + ProcessPluginApi.class.getSimpleName() + ") expected";
			fail(errorMessage);
		}
	}

	private Optional<DicTask> getInstance(List<Class<?>> types, Object... args)
	{
		try
		{
			return Optional.of(DicTask.class.getConstructor(types.toArray(Class[]::new))).map(c ->
			{
				try
				{
					return c.newInstance(args);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e)
				{
					throw new RuntimeException(e);
				}
			});
		}
		catch (NoSuchMethodException e)
		{
			return Optional.empty();
		}
		catch (SecurityException e)
		{
			throw e;
		}
	}

	@Test
	public void testDicTaskServiceDoExecute() throws Exception
	{
		Optional<DicTask> optService = getInstance(Arrays.asList(boolean.class), true);

		assumeTrue(optService.isPresent());

		Task task = getTask();
		DefaultMockingDetails taskHelperMockingDetails = new DefaultMockingDetails(taskHelperMock);
		DefaultMockingDetails variablesMockingDetails = new DefaultMockingDetails(variablesMock);

		Mockito.when(apiMock.getTaskHelper()).thenReturn(taskHelperMock);

		// Mock ways to get start task
		Mockito.lenient().when(variablesMock.getStartTask()).thenReturn(task);
		Mockito.lenient().when(variablesMock.getLatestTask()).thenReturn(null); // latest task only refers to tasks
																				// received after an intermediate
																				// message catch event. Therefore, the
																				// start task is not returned by
																				// getLatestTask
		Mockito.lenient().when(variablesMock.getTasks()).thenReturn(List.of(task));
		Mockito.lenient().when(variablesMock.getCurrentTasks()).thenReturn(Collections.emptyList()); // getCurrentTasks
																										// doesn't
																										// return the
																										// start task

		// Mock ways to get input parameter contents
		TaskMatcher taskMatcher = new TaskMatcher(task);
		Mockito.lenient().when(taskHelperMock.getFirstInputParameterStringValue(argThat(taskMatcher),
				eq(CODESYSTEM_TUTORIAL), eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT)))
				.thenReturn(Optional.of(TEST_STRING));
		Mockito.lenient()
				.when(taskHelperMock.getFirstInputParameter(argThat(taskMatcher), eq(CODESYSTEM_TUTORIAL),
						eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT), eq(StringType.class)))
				.thenReturn(Optional.of(parameterComponentMock));
		Mockito.lenient()
				.when(taskHelperMock.getFirstInputParameterValue(argThat(taskMatcher), eq(CODESYSTEM_TUTORIAL),
						eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT), eq(StringType.class)))
				.thenReturn(Optional.of(stringTypeMock));
		Mockito.lenient()
				.when(taskHelperMock.getInputParameters(argThat(taskMatcher), eq(CODESYSTEM_TUTORIAL),
						eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT), eq(StringType.class)))
				.thenReturn(Stream.of(parameterComponentMock));
		Mockito.lenient().when(taskHelperMock.getInputParameterStringValues(argThat(taskMatcher),
				eq(CODESYSTEM_TUTORIAL), eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT)))
				.thenReturn(Stream.of(TEST_STRING));
		Mockito.lenient()
				.when(taskHelperMock.getInputParameterValues(argThat(taskMatcher), eq(CODESYSTEM_TUTORIAL),
						eq(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT), eq(StringType.class)))
				.thenReturn(Stream.of(stringTypeMock));

		// Mock calls to get value from ParameterComponent and StringType
		Mockito.lenient().when(parameterComponentMock.getValue()).thenReturn(stringTypeMock);
		Mockito.lenient().when(stringTypeMock.getValue()).thenReturn(TEST_STRING);

		try
		{
			optService.get().execute(apiMock, variablesMock);
		}
		catch (NullPointerException e)
		{
			logger.error(
					"You might see this error message when calling 'getCurrentTasks' or 'getLatestTask' in your service delegate because those methods return an empty list in case of 'getCurrentTasks' or null in case of 'getLatestTask'. You shouldn't be using them here. Check their Java Doc for more information");
		}

		String errorMessage = "'getLatestTask' only refers to tasks received after an intermediate message catch event. Therefore, the start task is not returned by getLatestTask. GetLatestTask cannot be used here.";
		Mockito.verify(variablesMock, Mockito.never().description(errorMessage)).getLatestTask();

		errorMessage = "'getCurrentTasks' doesn't include the start task in the list it returns.";
		Mockito.verify(variablesMock, Mockito.never().description(errorMessage)).getCurrentTasks();

		int numCallsGetStartTask = getNumInvocationsForMethod("getStartTask", variablesMockingDetails);
		int numCallsGetTasks = getNumInvocationsForMethod("getTasks", variablesMockingDetails);

		if (numCallsGetTasks > 0)
			logger.warn("You don't have to use 'getTasks' to find the start task. Try using 'getStartTask'.");

		errorMessage = "Expected 'getStartTask' or 'getTasks' to be called at least once.";
		assertTrue(errorMessage, numCallsGetStartTask > 0 || numCallsGetTasks > 0);

		Mockito.verify(variablesMock,
				atLeastOnce().description(
						"'getStartTask' is the most effective way to get the start task. Use this method here."))
				.getStartTask();

		Map<String, Integer> numInvocationsForMethods = taskHelperMethodsToVerify.stream().collect(Collectors
				.toMap(Function.identity(), method -> getNumInvocationsForMethod(method, taskHelperMockingDetails)));

		errorMessage = "Expected one of the following methods to be called at least once:\n" + numInvocationsForMethods
				.keySet().stream().reduce("", (previousLine, method) -> previousLine + method + "\n");

		int totalInvocations = numInvocationsForMethods.values().stream().mapToInt(Integer::valueOf).sum();

		assertTrue(errorMessage, totalInvocations > 0);

		verifyTaskHelperInvocations(taskHelperMockingDetails);

		int numInvocationsFirstInputParameter = numInvocationsForMethods.get(GET_FIRST_INPUT_PARAMETER);
		int numInvocationsInputParameters = numInvocationsForMethods.get(GET_INPUT_PARAMETERS);

		if (numInvocationsFirstInputParameter + numInvocationsInputParameters > 0
				&& totalInvocations - (numInvocationsFirstInputParameter + numInvocationsInputParameters) == 0)
		{
			Mockito.verify(parameterComponentMock,
					atLeastOnce().description("Input parameter was retrieved but its value element never got read."))
					.getValue();
			Mockito.verify(stringTypeMock,
					atLeastOnce().description(
							"Value element of input parameter was retrieved but the string value was never read."))
					.getValue();
		}

		int numInvocationsFirstInputParameterValue = numInvocationsForMethods.get(GET_FIRST_INPUT_PARAMETER_VALUE);
		int numInvocationsInputParameterValues = numInvocationsForMethods.get(GET_INPUT_PARAMETER_VALUES);

		if (numInvocationsFirstInputParameterValue + numInvocationsInputParameterValues > 0 && totalInvocations
				- (numInvocationsFirstInputParameterValue + numInvocationsInputParameterValues) == 0)
		{
			Mockito.verify(stringTypeMock,
					atLeastOnce().description(
							"Value element of input parameter was retrieved but the string value was never read."))
					.getValue();
		}
		// Not verifying for getFirstInputParameterStringValue and getInputParameterStringValues since they return the
		// right string value themselves without needing to traverse more getters
	}

	private Task getTask()
	{
		Task task = new Task();
		task.getRestriction().addRecipient().getIdentifier().setSystem(NamingSystems.OrganizationIdentifier.SID)
				.setValue("MeDIC");
		task.addInput().setValue(new StringType("Test")).getType().addCoding()
				.setSystem("http://example.org/fhir/CodeSystem/tutorial").setCode("tutorial-input");

		return task;
	}

	private int getNumInvocationsForMethod(String methodName, DefaultMockingDetails mockingDetails)
	{
		return (int) mockingDetails.getInvocations().stream().map(InvocationOnMock::getMethod)
				.filter(method -> method.getName().equals(methodName)).count();
	}

	private CodeSystem getCodeSystem()
	{
		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();
		Map<String, List<Resource>> fhirResources = FhirResourceLoader.getFhirResourcesByProcessId(definition);

		List<Resource> dicProcessResources = fhirResources.get(ConstantsTutorial.PROCESS_NAME_FULL_DIC);

		return dicProcessResources.stream().filter(resource -> resource instanceof CodeSystem)
				.map(resource -> (CodeSystem) resource)
				.filter(codeSystem -> codeSystem.getUrl().equals(CODESYSTEM_TUTORIAL))
				.filter(codeSystem -> codeSystem.getConcept().stream()
						.anyMatch(concept -> concept.getCode().equals(CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT)))
				.findFirst().get();
	}

	private void verifyTaskHelperInvocations(DefaultMockingDetails taskHelperMockingDetails)
	{
		String errorMessage = "";
		Collection<Invocation> invocations = taskHelperMockingDetails.getInvocations();

		List<Invocation> codingArgumentInvocations = invocations.stream()
				.filter(invocation -> taskHelperMethodsToVerify.stream()
						.anyMatch(method -> method.equals(invocation.getMethod().getName())))
				.filter(invocation -> Arrays.stream(invocation.getRawArguments()).anyMatch(isCodingArgument()))
				.toList();
		List<Invocation> stringArgumentInvocations = invocations.stream()
				.filter(invocation -> taskHelperMethodsToVerify.stream()
						.anyMatch(method -> method.equals(invocation.getMethod().getName())))
				.filter(invocation -> isStringMethod(invocation.getArguments())).toList();

		CodeSystem codeSystem = getCodeSystem();
		errorMessage = "No CodeSystem found with URL " + CODESYSTEM_TUTORIAL + " including a concept with code "
				+ CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT + ".";
		assertNotNull(errorMessage, codeSystem);

		Map<Invocation, Task> tasksForInvocations = Stream
				.concat(codingArgumentInvocations.stream(), stringArgumentInvocations.stream())
				.collect(Collectors.toMap(Function.identity(), invocation -> Arrays.stream(invocation.getRawArguments())
						.filter(arg -> arg instanceof Task).map(arg -> (Task) arg).findFirst().get()));

		tasksForInvocations.forEach((invocation, task) ->
		{
			TaskMatcher taskMatcher = new TaskMatcher(getTask());
			String error = "Invocation of " + invocation.getMethod().getName()
					+ " expected argument of type Task to have least one input parameter which has a CodeSystem "
					+ CODESYSTEM_TUTORIAL + " and Code " + CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT + ".";
			assertTrue(error, taskMatcher.matches(task));
		});

		Map<Invocation, Coding> invocationsWithCodingArgument = codingArgumentInvocations.stream()
				.collect(Collectors.toMap(Function.identity(), codingForInvocationArgument()));
		Map<Invocation, String[]> invocationsWithStringArguments = stringArgumentInvocations.stream()
				.collect(Collectors.toMap(Function.identity(), this::getCodeSystemAndCodeArguments));

		invocationsWithCodingArgument.forEach((invocation, coding) ->
		{
			String error = "Invocation of " + invocation.getMethod().getName() + " has wrong CodeSystem,";
			assertEquals(error, CODESYSTEM_TUTORIAL, coding.getSystem());
			error = "Invocation of " + invocation.getMethod().getName() + " has CodeSystem with wrong Code,";
			assertEquals(error, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT, coding.getCode());
		});

		invocationsWithStringArguments.forEach((invocation, strings) ->
		{
			String error = "Invocation of " + invocation.getMethod().getName() + " has wrong CodeSystem,";
			assertEquals(error, CODESYSTEM_TUTORIAL, strings[0]);
			error = "Invocation of " + invocation.getMethod().getName() + " has CodeSystem with wrong Code,";
			assertEquals(error, CODESYSTEM_TUTORIAL_VALUE_TUTORIAL_INPUT, strings[1]);
		});
	}

	private Predicate<Object> isCodingArgument()
	{
		return object -> object instanceof Coding;
	}

	private boolean isStringMethod(Object[] args)
	{
		return args[1] instanceof String && args[2] instanceof String;
	}

	private Function<Invocation, Coding> codingForInvocationArgument()
	{
		return invocation -> (Coding) Arrays.stream(invocation.getRawArguments()).filter(isCodingArgument()).findFirst()
				.get();
	}

	private String[] getCodeSystemAndCodeArguments(Invocation invocation)
	{
		String[] codeSystemAndCode = new String[2];
		int argumentsAdded = 0;
		for (Object object : invocation.getArguments())
		{
			if (object instanceof String && argumentsAdded < 2)
			{
				codeSystemAndCode[argumentsAdded] = (String) object;
				argumentsAdded++;
			}
		}
		return codeSystemAndCode;
	}

	private class TaskMatcher implements ArgumentMatcher<Task>
	{
		private Task task;
		private String system;
		private String code;

		public TaskMatcher(Task task)
		{
			this.task = task;
			system = task.getInputFirstRep().getType().getCodingFirstRep().getSystem();
			code = task.getInputFirstRep().getType().getCodingFirstRep().getCode();
		}

		@Override
		public boolean matches(Task task)
		{
			return task.getInput().stream().anyMatch(parameterComponent -> parameterComponent.getType().getCoding()
					.stream().anyMatch(coding -> coding.getSystem().equals(system) && coding.getCode().equals(code)));
		}
	}
}
