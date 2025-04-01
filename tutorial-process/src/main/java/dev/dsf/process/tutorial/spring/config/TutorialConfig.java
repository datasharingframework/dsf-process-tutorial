package dev.dsf.process.tutorial.spring.config;

import static dev.dsf.process.tutorial.ConstantsTutorial.PROCESS_NAME_FULL_DIC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import dev.dsf.bpe.v2.documentation.ProcessDocumentation;
import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;
import dev.dsf.process.tutorial.service.DicTask;

@Configuration
public class TutorialConfig
{
	@Value("${dev.dsf.process.tutorial.loggingEnabled:false}")
	@ProcessDocumentation(description = "Set to true to enable logging", required = false, processNames = PROCESS_NAME_FULL_DIC)
	private boolean loggingEnabled;

	@Bean
	public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DicTask dicTask()
	{
		return new DicTask(loggingEnabled);
	}

}
