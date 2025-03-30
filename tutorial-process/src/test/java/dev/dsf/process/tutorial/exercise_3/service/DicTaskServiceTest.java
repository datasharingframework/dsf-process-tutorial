package dev.dsf.process.tutorial.exercise_3.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.bpe.v2.service.TaskHelper;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.fhir.authorization.read.ReadAccessHelper;
import dev.dsf.process.tutorial.service.DicTask;

@RunWith(MockitoJUnitRunner.class)
public class DicTaskServiceTest
{
	@Mock
	private TaskHelper taskHelper;

	@Mock
	private ReadAccessHelper readAccessHelper;

	@Mock
	private DelegateExecution execution;

	@Mock
	private ProcessPluginApi api;

	@Mock
	private Variables variables;

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
		Optional<Constructor<DicTask>> constructor = getConstructor(ProcessPluginApi.class, boolean.class);

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
		Mockito.when(api.getTaskHelper()).thenReturn(taskHelper);
		Mockito.when(variables.getStartTask()).thenReturn(task);
		Mockito.when(taskHelper.getFirstInputParameterStringValue(any(), eq("http://dsf.dev/fhir/CodeSystem/tutorial"),
				eq("tutorial-input"))).thenReturn(Optional.of("Test"));

		optService.get().execute(api, variables);

		ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
		Mockito.verify(taskHelper).getFirstInputParameterStringValue(captor.capture(),
				eq("http://dsf.dev/fhir/CodeSystem/tutorial"), eq("tutorial-input"));
		Mockito.verify(variables, atLeastOnce()).getStartTask();

		assertEquals(task, captor.getValue());
	}

	private Task getTask()
	{
		Task task = new Task();
		task.getRestriction().addRecipient().getIdentifier().setSystem(NamingSystems.OrganizationIdentifier.SID)
				.setValue("MeDIC");
		task.addInput().setValue(new StringType("Test")).getType().addCoding()
				.setSystem("http://dsf.dev/fhir/CodeSystem/tutorial").setCode("tutorial-input");

		return task;
	}
}
