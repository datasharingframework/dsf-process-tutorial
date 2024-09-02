package dev.dsf.process.tutorial.util;

public enum VoteResponse
{
	YES,
	NO,
	TIMEOUT;

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}
}
