package dev.dsf.process.tutorial.exercise_1.spring.config;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.context.annotation.Bean;

import dev.dsf.process.tutorial.service.DicTask;
import dev.dsf.process.tutorial.spring.config.TutorialConfig;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		long count = Arrays.stream(TutorialConfig.class.getMethods())
				.filter(m -> DicTask.class.equals(m.getReturnType())).filter(m -> Modifier.isPublic(m.getModifiers()))
				.filter(m -> m.getAnnotation(Bean.class) != null).count();

		String errorMethod = "Configuration class " + TutorialConfig.class.getSimpleName() + " contains " + count
				+ " public spring bean methods with return type " + DicTask.class.getSimpleName() + ", expected 1";
		assertEquals(errorMethod, 1, count);
	}
}
