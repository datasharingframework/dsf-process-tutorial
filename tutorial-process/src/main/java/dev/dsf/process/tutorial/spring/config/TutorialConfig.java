package dev.dsf.process.tutorial.spring.config;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;
import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_VOTING_PROCESS;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.documentation.ProcessDocumentation;
import dev.dsf.process.tutorial.listener.QuestionnaireListener;
import dev.dsf.process.tutorial.message.GoodbyeDicMessage;
import dev.dsf.process.tutorial.message.HelloCosMessage;
import dev.dsf.process.tutorial.message.HelloHrpMessage;
import dev.dsf.process.tutorial.message.ReturnVote;
import dev.dsf.process.tutorial.message.StartVote;
import dev.dsf.process.tutorial.message.StartVotingProcess;
import dev.dsf.process.tutorial.service.AggregateResults;
import dev.dsf.process.tutorial.service.AutomatedVote;
import dev.dsf.process.tutorial.service.CosTask;
import dev.dsf.process.tutorial.service.DicTask;
import dev.dsf.process.tutorial.service.HrpTask;
import dev.dsf.process.tutorial.service.PrepareQuestion;
import dev.dsf.process.tutorial.service.SaveTimeoutResult;
import dev.dsf.process.tutorial.service.SaveVotingResult;
import dev.dsf.process.tutorial.service.SelectTargets;

@Configuration
public class TutorialConfig
{
	@Autowired
	private ProcessPluginApi api;

	@Value("${dev.dsf.process.tutorial.loggingEnabled:false}")
	@ProcessDocumentation(description = "Set to true to enable logging", required = false, processNames = PROCESS_NAME_FULL_DIC)
	private boolean loggingEnabled;

	@Value("${dev.dsf.process.tutorial.userVote:false}")
	@ProcessDocumentation(description = "Set to true to enable logging", required = false, processNames = PROCESS_NAME_FULL_VOTING_PROCESS)
	private boolean userVote;

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public DicTask dicTask()
	{
		return new DicTask(api, loggingEnabled);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public HelloCosMessage helloCosMessage()
	{
		return new HelloCosMessage(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public CosTask cosTask()
	{
		return new CosTask(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public HelloHrpMessage helloHrpMessage()
	{
		return new HelloHrpMessage(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public HrpTask helloHrp()
	{
		return new HrpTask(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public GoodbyeDicMessage goodbyeDicMessage()
	{
		return new GoodbyeDicMessage(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public StartVotingProcess startVotingProcess()
	{
		return new StartVotingProcess(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public SelectTargets selectTargets()
	{
		return new SelectTargets(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public StartVote startVote()
	{
		return new StartVote(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public PrepareQuestion prepareQuestion()
	{
		return new PrepareQuestion(api, userVote);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public AutomatedVote automatedVote()
	{
		return new AutomatedVote(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public QuestionnaireListener questionnaireListener()
	{
		return new QuestionnaireListener(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public ReturnVote returnVote()
	{
		return new ReturnVote(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public SaveVotingResult saveVotingResult()
	{
		return new SaveVotingResult(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public SaveTimeoutResult saveTimeoutResult()
	{
		return new SaveTimeoutResult(api);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public AggregateResults aggregateResults()
	{
		return new AggregateResults(api);
	}
}
