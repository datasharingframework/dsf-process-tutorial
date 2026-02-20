package tutorial.org.process.tutorial.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;
import dev.dsf.process.tutorial.service.DicTask;

@Configuration
public class TutorialConfig
{
	@Bean
	public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator(DicTask.class);
	}

}
