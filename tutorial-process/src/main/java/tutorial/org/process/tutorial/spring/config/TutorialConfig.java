package tutorial.org.process.tutorial.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;

@Configuration
public class TutorialConfig
{
	@Bean
	public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
	{
		return new ActivityPrototypeBeanCreator();
	}

}
