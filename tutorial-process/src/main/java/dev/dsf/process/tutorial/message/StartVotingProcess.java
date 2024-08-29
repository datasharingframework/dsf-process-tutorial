package dev.dsf.process.tutorial.message;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractTaskMessageSend;

public class StartVotingProcess extends AbstractTaskMessageSend
{
	public StartVotingProcess(ProcessPluginApi api)
	{
		super(api);
	}
}
