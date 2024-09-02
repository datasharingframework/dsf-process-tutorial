package dev.dsf.process.tutorial.service;

import java.util.Optional;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;

import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.activity.AbstractServiceDelegate;
import dev.dsf.bpe.v1.constants.CodeSystems;
import dev.dsf.bpe.v1.variables.Target;
import dev.dsf.bpe.v1.variables.Variables;

public class PrepareReturnVote extends AbstractServiceDelegate
{
	public PrepareReturnVote(ProcessPluginApi api)
	{
		super(api);
	}

	@Override
	protected void doExecute(DelegateExecution delegateExecution, Variables variables) throws BpmnError, Exception
	{
		Task startTask = variables.getStartTask();
		Reference requesterRef = startTask.getRequester();
		Optional<Organization> optionalOrganization = api.getOrganizationProvider().getOrganization(requesterRef.getIdentifier());
		if (optionalOrganization.isPresent())
		{
			String[] readParams = optionalOrganization.get().getEndpoint().get(0).getReference().split("/");
			String resourceType = readParams[0];
			String id = readParams[1];
			Endpoint requesterEndpoint = (Endpoint) api.getFhirWebserviceClientProvider().getLocalWebserviceClient().read(resourceType, id);

			Target target = variables.createTarget(requesterRef.getIdentifier().getValue(), requesterEndpoint.getIdentifierFirstRep().getValue(), requesterEndpoint.getAddress(), api.getTaskHelper()
					.getFirstInputParameterStringValue(startTask, CodeSystems.BpmnMessage.URL, CodeSystems.BpmnMessage.Codes.CORRELATION_KEY).get());
			variables.setTarget(target);
		}
	}
}
