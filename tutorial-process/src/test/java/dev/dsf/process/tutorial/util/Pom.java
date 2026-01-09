package dev.dsf.process.tutorial.util;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class Pom
{
	private final Model model;

	public Pom() throws Exception
	{
		MavenXpp3Reader reader = new MavenXpp3Reader();
		this.model = reader.read(this.getClass().getResourceAsStream("plugin.properties"));
	}

	public LocalDate getReleaseDate()
	{
		return LocalDate.parse(model.getProperties().getProperty("project.build.outputTimestamp"));
	}

	public String getVersion()
	{
		return model.getVersion();
	}

	public String getName()
	{
		return model.getProperties().getProperty("project.artifactId");
	}

	public String getTitle()
	{
		return model.getProperties().getProperty("project.description");
	}

	public String getPublisher()
	{
		return model.getProperties().getProperty("project.organization.name");
	}

	public String getDsfVersion()
	{
		String version = getVersion();
		Objects.requireNonNull(version, "version");
		return version.replaceFirst("-.*$", "");
	}

	public String getResourceVersion()
	{
		return getDsfVersion().substring(0, 3);
	}
}
