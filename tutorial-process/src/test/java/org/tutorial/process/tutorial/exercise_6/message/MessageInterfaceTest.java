package org.tutorial.process.tutorial.exercise_6.message;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dev.dsf.bpe.v2.activity.MessageEndEvent;
import dev.dsf.bpe.v2.activity.MessageIntermediateThrowEvent;
import org.tutorial.process.tutorial.message.GoodbyeDicMessage;
import org.tutorial.process.tutorial.message.HelloCosMessage;
import org.tutorial.process.tutorial.message.HelloHrpMessage;

public class MessageInterfaceTest
{
	@Test
	public void testHelloCosMessage()
	{
		String error = "Expected HelloCosMessage to implement MessageEndEvent interface";
		assertTrue(error, MessageIntermediateThrowEvent.class.isAssignableFrom(HelloCosMessage.class));
	}

	@Test
	public void testHelloHrpMessage()
	{
		String error = "Expected HelloHrpMessage to implement MessageEndEvent interface";
		assertTrue(error, MessageEndEvent.class.isAssignableFrom(HelloHrpMessage.class));
	}

	@Test
	public void testGoodbyeDicMessage()
	{
		String error = "Expected GoodbyeDicMessage to implement MessageEndEvent interface";
		assertTrue(error, MessageEndEvent.class.isAssignableFrom(GoodbyeDicMessage.class));
	}
}
