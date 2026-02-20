package org.tutorial.process.tutorial.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tutorial.process.tutorial.service.DicTask;

import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;

@Configuration
public class TutorialConfig
{
	@Bean
	public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator(DicTask.class);
	}

}
