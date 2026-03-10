package org.example.process.tutorial.exercise_1.spring.config;

import static org.junit.Assert.assertEquals;
import static org.example.process.tutorial.util.Misc.countBeanMethods;
import static org.example.process.tutorial.util.Misc.errorMessageBeanMethod;

import org.junit.Test;
import org.example.process.tutorial.service.DicTask;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DicTask.class), 1, countBeanMethods(DicTask.class));
	}
}
