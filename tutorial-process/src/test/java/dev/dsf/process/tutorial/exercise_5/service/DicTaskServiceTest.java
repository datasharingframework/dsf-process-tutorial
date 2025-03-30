package dev.dsf.process.tutorial.exercise_5.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;
import dev.dsf.process.tutorial.service.DicTask;

@RunWith(MockitoJUnitRunner.class)
public class DicTaskServiceTest
{

	@Mock
	private TaskHelper taskHelper;

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
		final String orgIdValue = "cos.dsf.test";
		final String endpointIdValue = "cos.dsf.test_Endpoint";
		final String endpointAddress = "https://cos/fhir";

		Optional<DicTask> optService = getInstance(Arrays.asList(ProcessPluginApi.class, boolean.class), api, true);
		if (optService.isEmpty())
			optService = getInstance(Arrays.asList(boolean.class, ProcessPluginApi.class), true, api);

		assumeTrue(optService.isPresent());

		Task task = getTask();
		Mockito.when(api.getTaskHelper()).thenReturn(taskHelper);
		Mockito.when(variables.getStartTask()).thenReturn(task);
		Mockito.when(taskHelper.getFirstInputParameterStringValue(any(), eq("http://dsf.dev/fhir/CodeSystem/tutorial"),
				eq("tutorial-input"))).thenReturn(Optional.of("Test"));
		Mockito.when(variables.createTarget(orgIdValue, endpointIdValue, endpointAddress)).thenReturn(new Target()
		{
			@Override
			public String getOrganizationIdentifierValue()
			{
				return orgIdValue;
			}

			@Override
			public String getEndpointIdentifierValue()
			{
				return endpointIdValue;
			}

			@Override
			public String getEndpointUrl()
			{
				return endpointAddress;
			}

			@Override
			public String getCorrelationKey()
			{
				return null;
			}
		});

		optService.get().execute(api, variables);

		ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
		Mockito.verify(taskHelper).getFirstInputParameterStringValue(captor.capture(),
				eq("http://dsf.dev/fhir/CodeSystem/tutorial"), eq("tutorial-input"));
		Mockito.verify(variables, atLeastOnce()).getStartTask();
		assertEquals(task, captor.getValue());

		ArgumentCaptor<String> orgIdValueCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> endpointIdValueCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> endpointAddressCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(variables).createTarget(orgIdValueCaptor.capture(), endpointIdValueCaptor.capture(),
				endpointAddressCaptor.capture());
		assertEquals(orgIdValue, orgIdValueCaptor.getValue());
		assertEquals(endpointIdValue, endpointIdValueCaptor.getValue());
		assertEquals(endpointAddress, endpointAddressCaptor.getValue());

		ArgumentCaptor<Target> targetArgumentCaptor = ArgumentCaptor.forClass(Target.class);
		Mockito.verify(variables).setTarget(targetArgumentCaptor.capture());
		assertEquals(orgIdValue, targetArgumentCaptor.getValue().getOrganizationIdentifierValue());
		assertEquals(endpointIdValue, targetArgumentCaptor.getValue().getEndpointIdentifierValue());
		assertEquals(endpointAddress, targetArgumentCaptor.getValue().getEndpointUrl());

		Mockito.verify(variables).setBoolean(anyString(), any());
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
