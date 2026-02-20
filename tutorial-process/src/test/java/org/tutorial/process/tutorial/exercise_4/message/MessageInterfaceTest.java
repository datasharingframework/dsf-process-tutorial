package org.tutorial.process.tutorial.exercise_4.message;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tutorial.process.tutorial.message.HelloCosMessage;

import dev.dsf.bpe.v2.activity.MessageEndEvent;

public class MessageInterfaceTest
{
	@Test
	public void testHelloCosMessage()
	{
		String error = "Expected HelloCosMessage to implement MessageEndEvent interface";
		assertTrue(error, MessageEndEvent.class.isAssignableFrom(HelloCosMessage.class));
	}
}
