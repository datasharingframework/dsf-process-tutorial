package dev.dsf.process.tutorial;

import static org.mockito.Mockito.mock;

import java.nio.file.Path;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import dev.dsf.bpe.v1.ProcessPluginApi;
import dev.dsf.bpe.v1.ProcessPluginApiImpl;
import dev.dsf.bpe.v1.ProcessPluginDefinition;
import dev.dsf.bpe.v1.config.ProxyConfig;
import dev.dsf.bpe.v1.plugin.ProcessPluginImpl;
import dev.dsf.bpe.v1.service.EndpointProvider;
import dev.dsf.bpe.v1.service.FhirWebserviceClientProvider;
import dev.dsf.bpe.v1.service.MailService;
import dev.dsf.bpe.v1.service.OrganizationProvider;
import dev.dsf.bpe.v1.service.QuestionnaireResponseHelper;
import dev.dsf.bpe.v1.service.TaskHelper;
import dev.dsf.bpe.variables.ObjectMapperFactory;
import dev.dsf.fhir.authorization.process.ProcessAuthorizationHelper;
import dev.dsf.fhir.authorization.read.ReadAccessHelper;

public class TestProcessPluginGenerator
{
	private static ProxyConfig proxyConfig = mock(ProxyConfig.class);
	private static EndpointProvider endpointProvider = mock(EndpointProvider.class);
	private static FhirContext fhirContext = FhirContext.forR4();
	private static FhirWebserviceClientProvider fhirWebserviceClientProvider = mock(FhirWebserviceClientProvider.class);
	private static MailService mailService = mock(MailService.class);
	private static ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper(fhirContext);
	private static OrganizationProvider organizationProvider = mock(OrganizationProvider.class);
	private static QuestionnaireResponseHelper questionnaireResponseHelper = mock(QuestionnaireResponseHelper.class);
	private static ProcessAuthorizationHelper processAuthorizationHelper = mock(ProcessAuthorizationHelper.class);
	private static ReadAccessHelper readAccessHelper = mock(ReadAccessHelper.class);
	private static TaskHelper taskHelper = mock(TaskHelper.class);

	private static ProcessPluginApi processPluginApi = new ProcessPluginApiImpl(proxyConfig, endpointProvider,
			fhirContext, fhirWebserviceClientProvider, mailService, objectMapper, organizationProvider,
			processAuthorizationHelper, questionnaireResponseHelper, readAccessHelper, taskHelper);
	private static ConfigurableEnvironment environment = new StandardEnvironment();

	public static <T> ProcessPluginImpl generate(ProcessPluginDefinition processPluginDefinition, boolean draft,
			Class<T> loadingClass)
	{
		return new ProcessPluginImpl(processPluginDefinition, processPluginApi, draft, Path.of("test.jar"),
				loadingClass.getClassLoader(), fhirContext, environment);
	}
}
