package dev.dsf.process.tutorial.exercise_6.config;

import static dev.dsf.process.tutorial.Utils.countBeanMethods;
import static dev.dsf.process.tutorial.Utils.errorMessageBeanMethod;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;
import dev.dsf.process.tutorial.message.GoodbyeDicMessage;
import dev.dsf.process.tutorial.message.HelloCosMessage;
import dev.dsf.process.tutorial.message.HelloHrpMessage;
import dev.dsf.process.tutorial.service.CosTask;
import dev.dsf.process.tutorial.service.DicTask;
import dev.dsf.process.tutorial.service.HrpTask;
import dev.dsf.process.tutorial.spring.config.TutorialConfig;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DicTask.class), 1, countBeanMethods(DicTask.class));
	}

	@Test
	public void testHelloCosMessageBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(HelloCosMessage.class), 1, countBeanMethods(HelloCosMessage.class));
	}

	@Test
	public void testConfigParameterExists() throws Exception
	{
		long count = Arrays.stream(TutorialConfig.class.getDeclaredFields())
				.filter(f -> boolean.class.equals(f.getType()))
				.filter(f -> f.getAnnotationsByType(Value.class).length == 1)
				.filter(f -> f.getAnnotation(Value.class).value() != null).count();

		String errorMessage = "One private field of type boolean with " + Value.class.getName()
				+ " annotation including value expected in " + TutorialConfig.class.getSimpleName();
		assertEquals(errorMessage, 1, count);
	}

	@Test
	public void testConfigParameterDocumetationExists() throws Exception
	{
		long count = Arrays.stream(TutorialConfig.class.getDeclaredFields())
				.filter(f -> boolean.class.equals(f.getType()))
				.filter(f -> f.getAnnotationsByType(ProcessDocumentation.class).length == 1)
				.filter(f -> f.getAnnotation(ProcessDocumentation.class).description() != null).count();

		String errorMessage = "One private field of type boolean with " + ProcessDocumentation.class.getName()
				+ " annotation including description expected in " + TutorialConfig.class.getSimpleName();
		assertEquals(errorMessage, 1, count);
	}

	@Test
	public void testCosTaskBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(CosTask.class), 1, countBeanMethods(CosTask.class));
	}

	@Test
	public void testHrpTaskBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(HrpTask.class), 1, countBeanMethods(HrpTask.class));
	}

	@Test
	public void testHelloHrpMessageBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(HelloHrpMessage.class), 1, countBeanMethods(HelloHrpMessage.class));
	}

	@Test
	public void testGoodbyeDicMessageBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(GoodbyeDicMessage.class), 1, countBeanMethods(GoodbyeDicMessage.class));
	}
}
