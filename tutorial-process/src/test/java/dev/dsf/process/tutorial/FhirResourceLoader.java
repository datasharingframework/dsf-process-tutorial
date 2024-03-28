package dev.dsf.process.tutorial;

import java.util.List;

import org.hl7.fhir.r4.model.Resource;

import dev.dsf.bpe.plugin.ProcessIdAndVersion;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;

public class FhirResourceLoader
{
	public static List<Resource> loadResourcesFor(ProcessPluginImpl processPlugin, String processId)
	{

		var fhirResources = processPlugin.getFhirResources();

		return fhirResources.get(new ProcessIdAndVersion(
				processId, ConstantsTutorial.RESOURCE_VERSION));
	}
}
