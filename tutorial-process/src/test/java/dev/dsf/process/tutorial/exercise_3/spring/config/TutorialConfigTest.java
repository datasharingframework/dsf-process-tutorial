package dev.dsf.process.tutorial.exercise_3.spring.config;

import static dev.dsf.process.tutorial.Utils.countBeanMethods;
import static dev.dsf.process.tutorial.Utils.errorMessageBeanMethod;
import static org.junit.Assert.assertEquals;

import java.security.DigestException;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;
import dev.dsf.process.tutorial.service.DicTask;
import dev.dsf.process.tutorial.spring.config.TutorialConfig;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DigestException.class), 1, countBeanMethods(DicTask.class));
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
