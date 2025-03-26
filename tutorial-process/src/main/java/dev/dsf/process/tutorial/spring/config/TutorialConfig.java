package dev.dsf.process.tutorial.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;

@Configuration
public class TutorialConfig
{
	@Bean
	public ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator();
	}

}
