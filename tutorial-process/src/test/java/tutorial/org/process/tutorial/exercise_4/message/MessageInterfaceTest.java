package tutorial.org.process.tutorial.exercise_4.message;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dev.dsf.bpe.v2.activity.MessageEndEvent;
import tutorial.org.process.tutorial.message.HelloCosMessage;

public class MessageInterfaceTest
{
	@Test
	public void testHelloCosMessage()
	{
		String error = "Expected HelloCosMessage to implement MessageEndEvent interface";
		assertTrue(error, MessageEndEvent.class.isAssignableFrom(HelloCosMessage.class));
	}
}
