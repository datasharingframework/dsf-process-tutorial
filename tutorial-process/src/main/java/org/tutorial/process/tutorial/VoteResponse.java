package org.tutorial.process.tutorial;

public enum VoteResponse
{
	YES, NO, TIMEOUT;

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}
}
