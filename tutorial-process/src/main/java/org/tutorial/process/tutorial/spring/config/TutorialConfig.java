package org.tutorial.process.tutorial.spring.config;

import static org.tutorial.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;
import static org.tutorial.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_VOTING_PROCESS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.tutorial.process.tutorial.listener.SetCorrelationKeyListener;
import org.tutorial.process.tutorial.message.GoodbyeDicMessage;
import org.tutorial.process.tutorial.message.HelloCosMessage;
import org.tutorial.process.tutorial.message.HelloHrpMessage;
import org.tutorial.process.tutorial.message.ReturnVote;
import org.tutorial.process.tutorial.message.StartVote;
import org.tutorial.process.tutorial.message.StartVotingProcess;
import org.tutorial.process.tutorial.service.AggregateResults;
import org.tutorial.process.tutorial.service.AutomatedVote;
import org.tutorial.process.tutorial.service.CosTask;
import org.tutorial.process.tutorial.service.DecideWhetherUserVote;
import org.tutorial.process.tutorial.service.DicTask;
import org.tutorial.process.tutorial.service.HrpTask;
import org.tutorial.process.tutorial.service.PrepareReturnVote;
import org.tutorial.process.tutorial.service.SaveTimeoutResult;
import org.tutorial.process.tutorial.service.SaveUserVote;
import org.tutorial.process.tutorial.service.SaveVotingResult;
import org.tutorial.process.tutorial.service.SelectTargets;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;
import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;

@Configuration
public class TutorialConfig
{
	@Value("${org.tutorial.process.tutorial.loggingEnabled:false}")
	@ProcessDocumentation(description = "Set to true to enable logging", example = "true", processNames = PROCESS_NAME_FULL_DIC)
	private boolean loggingEnabled;

	@Value("${org.tutorial.process.tutorial.userVote:false}")
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
	public SetCorrelationKeyListener setCorrelationKeyListener()
	{
		return new SetCorrelationKeyListener();
	}
}
