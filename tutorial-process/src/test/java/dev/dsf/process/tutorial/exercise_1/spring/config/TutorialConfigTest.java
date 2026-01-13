package dev.dsf.process.tutorial.exercise_1.spring.config;

import static dev.dsf.process.tutorial.util.Misc.countBeanMethods;
import static dev.dsf.process.tutorial.util.Misc.errorMessageBeanMethod;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dev.dsf.process.tutorial.service.DicTask;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DicTask.class), 1, countBeanMethods(DicTask.class));
	}
}
