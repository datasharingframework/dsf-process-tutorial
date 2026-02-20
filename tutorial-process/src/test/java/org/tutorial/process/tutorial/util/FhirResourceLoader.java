package org.tutorial.process.tutorial.util;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import dev.dsf.bpe.v2.ProcessPluginDefinition;

public class FhirResourceLoader
{
	public static List<Resource> loadResourcesFor(ProcessPluginDefinition processPluginDefinition, String processId)
	{
		return processPluginDefinition.getFhirResourcesByProcessId().get(processId).stream()
				.map(fileName -> Misc.class.getClassLoader().getResourceAsStream(fileName)).map(inputStream ->
				{
					try
					{
						String content = new String(inputStream.readAllBytes());
						content = content.replaceAll("#\\{version}", processPluginDefinition.getResourceVersion());
						content = content.replaceAll("#\\{date}", processPluginDefinition.getReleaseDate()
								.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
						inputStream.close();
						IBaseResource baseResource = FhirContext.forR4().newXmlParser().parseResource(content);
						return baseResource instanceof Resource ? (Resource) baseResource : null;
					}
					catch (Exception e)
					{
						return null;
					}
				}).toList();
	}

	public static Map<String, List<Resource>> getFhirResourcesByProcessId(
			ProcessPluginDefinition processPluginDefinition)
	{
		Map<String, List<Resource>> resourcesByProcessId = new HashMap<>();

		processPluginDefinition.getFhirResourcesByProcessId().entrySet().stream()
				.map(entry -> new Map.Entry<String, List<Resource>>()
				{
					@Override
					public String getKey()
					{
						return entry.getKey();
					}

					@Override
					public List<Resource> getValue()
					{
						return entry.getValue().stream()
								.map(fileName -> Misc.class.getClassLoader().getResourceAsStream(fileName))
								.map(stream ->
								{
									try
									{
										String content = new String(stream.readAllBytes());
										content = content.replaceAll("#\\{version}",
												processPluginDefinition.getResourceVersion());
										content = content.replaceAll("#\\{date}", processPluginDefinition
												.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
										stream.close();
										IBaseResource baseResource = FhirContext.forR4().newXmlParser()
												.parseResource(content);
										return baseResource instanceof Resource ? (Resource) baseResource : null;
									}
									catch (Exception e)
									{
										return null;
									}
								}).toList();
					}

					@Override
					public List<Resource> setValue(List<Resource> value)
					{
						return List.of();
					}

					@Override
					public boolean equals(Object o)
					{
						return false;
					}

					@Override
					public int hashCode()
					{
						return 0;
					}
				}).forEach(entry -> resourcesByProcessId.put(entry.getKey(), entry.getValue()));
		return resourcesByProcessId;
	}
}
