package org.tutorial.process.tutorial.exercise_4.config;

import static org.junit.Assert.assertEquals;
import static org.tutorial.process.tutorial.util.Misc.countBeanMethods;
import static org.tutorial.process.tutorial.util.Misc.errorMessageBeanMethod;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.tutorial.process.tutorial.message.HelloCosMessage;
import org.tutorial.process.tutorial.service.DicTask;
import org.tutorial.process.tutorial.spring.config.TutorialConfig;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DicTask.class), 1, countBeanMethods(DicTask.class));
	}

	@Test
	public void testCosMessageBeanDefined() throws Exception
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
}
