package dev.dsf.process.tutorial.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import dev.dsf.bpe.v1.ProcessPluginApi;

@Configuration
public class TutorialConfig
{
	@Autowired
	private ProcessPluginApi api;

}
