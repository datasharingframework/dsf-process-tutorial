package org.tutorial.process.tutorial.exercise_6.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.ParameterComponent;
import org.hl7.fhir.r4.model.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.bpe.v2.activity.values.SendTaskValues;
import dev.dsf.bpe.v2.constants.NamingSystems;
import dev.dsf.bpe.v2.service.TaskHelper;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;
import org.tutorial.process.tutorial.TutorialProcessPluginDefinition;
import org.tutorial.process.tutorial.message.HelloCosMessage;

@RunWith(MockitoJUnitRunner.class)
public class HelloCosMessageTest
{
	private static final String RESOURCE_VERSION = "1.4";

	@Mock
	private TaskHelper taskHelper;

	@Mock
	private DelegateExecution execution;

	@Mock
	private ProcessPluginApi api;

	@Mock
	private Variables variables;

	@Mock
	private Target target;

	private class MockableHelloCosMessage extends HelloCosMessage
	{
		@Override
		public List<ParameterComponent> getAdditionalInputParameters(ProcessPluginApi api, Variables variables,
				SendTaskValues sendTaskValues, Target target)
		{
			return super.getAdditionalInputParameters(api, variables, sendTaskValues, target);
		}
	}

	@Test
	public void testGetAdditionalInputParameters() throws Exception
	{

		MockableHelloCosMessage messageDelegate = new MockableHelloCosMessage();

		ProcessPluginDefinition definition = new TutorialProcessPluginDefinition();

		Mockito.when(api.getTaskHelper()).thenReturn(taskHelper);

		Mockito.when(variables.getStartTask()).thenReturn(getTask());

		Mockito.when(taskHelper.getFirstInputParameterStringValue(any(),
				eq("http://tutorial.org/fhir/CodeSystem/tutorial"), eq("tutorial-input")))
				.thenReturn(Optional.of("Test"));

		Mockito.when(api.getProcessPluginDefinition()).thenReturn(definition);

		Mockito.when(
				taskHelper.createInput(any(Type.class), eq("http://tutorial.org/fhir/CodeSystem/tutorial"),
						eq("tutorial-input"), eq(RESOURCE_VERSION)))
				.thenReturn(new ParameterComponent(new CodeableConcept(
						new Coding("http://tutorial.org/fhir/CodeSystem/tutorial", "tutorial-input", null)
								.setVersion(RESOURCE_VERSION)),
						new StringType("Test")));

		List<ParameterComponent> testParameterComponents = messageDelegate.getAdditionalInputParameters(api, variables,
				new SendTaskValues("", "", ""), target);

		Mockito.verify(variables).getStartTask();
		Mockito.verify(taskHelper).createInput(any(Type.class), anyString(), anyString(), anyString());
		Mockito.verify(api).getProcessPluginDefinition();

		ParameterComponent tutorialInput = testParameterComponents.stream()
				.filter(parameterComponent -> ((StringType) parameterComponent.getValue()).getValue().equals("Test"))
				.findFirst().get();
		assertEquals(1,
				tutorialInput.getType().getCoding().stream()
						.filter(c -> "http://tutorial.org/fhir/CodeSystem/tutorial".equals(c.getSystem()))
						.filter(c -> "tutorial-input".equals(c.getCode())).count());
		assertTrue(tutorialInput.getValue() instanceof StringType);
		assertEquals("Test", ((StringType) tutorialInput.getValue()).getValue());
	}

	private Task getTask()
	{
		Task task = new Task();
		task.getRestriction().addRecipient().getIdentifier().setSystem(NamingSystems.OrganizationIdentifier.SID)
				.setValue("MeDIC");
		task.addInput().setValue(new StringType("Test")).getType().addCoding()
				.setSystem("http://tutorial.org/fhir/CodeSystem/tutorial").setCode("tutorial-input");

		return task;
	}
}