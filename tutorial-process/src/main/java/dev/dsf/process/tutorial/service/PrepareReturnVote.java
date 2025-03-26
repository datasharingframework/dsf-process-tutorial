package dev.dsf.process.tutorial.service;

import java.util.Optional;

import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v2.ProcessPluginApi;
import dev.dsf.bpe.v2.activity.ServiceTask;
import dev.dsf.bpe.v2.constants.CodeSystems;
import dev.dsf.bpe.v2.error.ErrorBoundaryEvent;
import dev.dsf.bpe.v2.variables.Target;
import dev.dsf.bpe.v2.variables.Variables;

public class PrepareReturnVote implements ServiceTask
{
	@Override
	public void execute(ProcessPluginApi api, Variables variables) throws ErrorBoundaryEvent, Exception
	{
		Task startTask = variables.getStartTask();
		Reference requesterRef = startTask.getRequester();
		Optional<Organization> optionalOrganization = api.getOrganizationProvider()
				.getOrganization(requesterRef.getIdentifier());
		if (optionalOrganization.isPresent())
		{
			String[] readParams = optionalOrganization.get().getEndpoint().get(0).getReference().split("/");
			String id = readParams[1];
			Endpoint requesterEndpoint = (Endpoint) api.getFhirClientProvider().getClient("#local").orElseThrow().read()
					.resource(Endpoint.class).withId(id).execute();

			Target target = variables.createTarget(requesterRef.getIdentifier().getValue(),
					requesterEndpoint.getIdentifierFirstRep().getValue(), requesterEndpoint.getAddress(), api
							.getTaskHelper().getFirstInputParameterStringValue(startTask,
									CodeSystems.BpmnMessage.SYSTEM, CodeSystems.BpmnMessage.Codes.CORRELATION_KEY)
							.get());
			variables.setTarget(target);
		}
	}
}
