package dev.dsf.process.tutorial.spring.config;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_VOTING_PROCESS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;
import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;
import dev.dsf.process.tutorial.listener.SetCorrelationKeyListener;
import dev.dsf.process.tutorial.listener.UserVoteListener;
import dev.dsf.process.tutorial.message.GoodbyeDicMessage;
import dev.dsf.process.tutorial.message.HelloCosMessage;
import dev.dsf.process.tutorial.message.HelloHrpMessage;
import dev.dsf.process.tutorial.message.ReturnVote;
import dev.dsf.process.tutorial.message.StartVote;
import dev.dsf.process.tutorial.message.StartVotingProcess;
import dev.dsf.process.tutorial.service.AggregateResults;
import dev.dsf.process.tutorial.service.AutomatedVote;
import dev.dsf.process.tutorial.service.CosTask;
import dev.dsf.process.tutorial.service.DecideWhetherUserVote;
import dev.dsf.process.tutorial.service.DicTask;
import dev.dsf.process.tutorial.service.HrpTask;
import dev.dsf.process.tutorial.service.PrepareReturnVote;
import dev.dsf.process.tutorial.service.SaveTimeoutResult;
import dev.dsf.process.tutorial.service.SaveUserVote;
import dev.dsf.process.tutorial.service.SaveVotingResult;
import dev.dsf.process.tutorial.service.SelectTargets;

@Configuration
public class TutorialConfig
{
	@Value("${dev.dsf.process.tutorial.loggingEnabled:false}")
	@ProcessDocumentation(description = "Set to true to enable logging", required = false, processNames = PROCESS_NAME_FULL_DIC)
	private boolean loggingEnabled;

	@Value("${dev.dsf.process.tutorial.userVote:false}")
	@ProcessDocumentation(description = "Set to true to enable users to vote", required = false, processNames = PROCESS_NAME_FULL_VOTING_PROCESS)
	private boolean userVote;

	@Bean
	public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator(HelloCosMessage.class, CosTask.class, HelloHrpMessage.class,
				HrpTask.class, GoodbyeDicMessage.class, StartVotingProcess.class, SelectTargets.class, StartVote.class,
				SaveUserVote.class, AutomatedVote.class, PrepareReturnVote.class, ReturnVote.class,
				SaveVotingResult.class, SaveTimeoutResult.class, AggregateResults.class);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DicTask dicTask()
	{
		return new DicTask(loggingEnabled);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DecideWhetherUserVote decideWhetherUserVote()
	{
		return new DecideWhetherUserVote(userVote);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public UserVoteListener userVoteListener()
	{
		return new UserVoteListener();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SetCorrelationKeyListener setCorrelationKeyListener()
	{
		return new SetCorrelationKeyListener();
	}
}
