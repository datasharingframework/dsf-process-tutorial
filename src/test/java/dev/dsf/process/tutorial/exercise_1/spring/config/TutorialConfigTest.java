package dev.dsf.process.tutorial.exercise_1.spring.config;

import static dev.dsf.process.tutorial.Utils.countBeanMethods;
import static dev.dsf.process.tutorial.Utils.errorMessageBeanMethod;
import static org.junit.Assert.assertEquals;

import java.security.DigestException;

import org.junit.Test;

import dev.dsf.process.tutorial.service.DicTask;

public class TutorialConfigTest
{
	@Test
	public void testDicTaskServiceBeanDefined() throws Exception
	{
		assertEquals(errorMessageBeanMethod(DigestException.class), 1, countBeanMethods(DicTask.class));
	}
}
