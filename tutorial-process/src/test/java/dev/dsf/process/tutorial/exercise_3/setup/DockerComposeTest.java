package dev.dsf.process.tutorial.exercise_3.setup;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.hl7.fhir.r4.model.Coding;
import org.junit.Test;

import dev.dsf.process.tutorial.util.DsfRole;
import dev.dsf.process.tutorial.util.RoleConfig;
import dev.dsf.process.tutorial.util.RoleConfigReader;

public class DockerComposeTest
{
	private final String TEST_COS_FHIR_NAME = "cos-fhir";
	private final String TEST_DIC_FHIR_NAME = "dic-fhir";
	private final String TEST_HRP_FHIR_NAME = "hrp-fhir";

	private final List<String> dsfInstances = List.of(TEST_DIC_FHIR_NAME);
	private final String ROLECONFIG_FINISHED_PATTERN = "(^\\s{4}\\w+:)|(^\\s*$)"; // Either starting the line with 4
																					// whitespaces followed by a word
																					// ending with a colon or an "empty"
																					// line (line with no chars or only
																					// whitespaces)
	private final int numRoleConfigs = dsfInstances.size();

	private enum TestRole implements DsfRole
	{
		CREATE, READ, UPDATE, DELETE, SEARCH, HISTORY;

		public static boolean isValid(String role)
		{
			return role != null && !role.isBlank() && Stream.of(values()).map(Enum::name).anyMatch(n -> n.equals(role));
		}

		@Override
		public boolean matches(DsfRole role)
		{
			return false;
		}
	}

	@Test
	public void testDevDsfFhirServerRoleConfig() throws IOException
	{
		Map<String, RoleConfig<? extends DsfRole>> roleConfigs = getRoleConfigsByDsfInstance();
		assertEquals(numRoleConfigs, roleConfigs.size());

		int numTokenRolesValid = roleConfigs.entrySet().stream()
				.filter(entry -> !entry.getValue().getDsfRolesForTokenRole("tutorial").isEmpty()
						&& !entry.getValue().getPractitionerRolesForTokenRole("tutorial").isEmpty())
				.toList().size();
		assertEquals(numRoleConfigs, numTokenRolesValid);

		int numDsfRolesValid = roleConfigs.entrySet().stream()
				.filter(entry -> entry.getValue().getDsfRolesForTokenRole("tutorial").stream()
						.filter(dsfRole -> TestRole.isValid(dsfRole.name())).toList()
						.size() == TestRole.values().length)
				.toList().size();
		assertEquals(numRoleConfigs, numDsfRolesValid);

		int numPractitionerRolesValid = roleConfigs.entrySet().stream()
				.filter(entry -> entry.getValue().getPractitionerRolesForTokenRole("tutorial").stream()
						.filter(practitionerRole -> practitionerRole.getSystem()
								.equals("http://dsf.dev/fhir/CodeSystem/practitioner-role")
								&& practitionerRole.getCode().equals("DSF_ADMIN"))
						.toList().size() == 1)
				.toList().size();
		assertEquals(numRoleConfigs, numPractitionerRolesValid);
	}

	private Map<String, RoleConfig<? extends DsfRole>> getRoleConfigsByDsfInstance() throws IOException
	{

		Map<String, RoleConfig<? extends DsfRole>> roleConfigsByDsfInstance = new HashMap<>();

		String instance = null;
		String roleConfigString = "";
		boolean readRoleConfig = false;

		BufferedReader reader = new BufferedReader(Files.newBufferedReader(Path.of("../dev-setup/docker-compose.yml")));
		String line;
		while (Objects.nonNull(line = reader.readLine()))
		{
			String finalLine = line;
			Optional<String> optInstance = dsfInstances.stream().filter(i -> finalLine.contains(i + ":")).findFirst();
			instance = optInstance.orElse(instance);
			if (readRoleConfig)
			{
				if (line.matches(ROLECONFIG_FINISHED_PATTERN))
				{
					roleConfigsByDsfInstance.put(instance, getRoleConfig(roleConfigString));
					readRoleConfig = false;
					roleConfigString = "";
					instance = null;
				}
				else
				{
					roleConfigString += line + "\n";
				}
			}
			if (Objects.nonNull(instance) && line.contains("DEV_DSF_FHIR_SERVER_ROLECONFIG: |"))
			{
				readRoleConfig = true;
			}
		}
		return roleConfigsByDsfInstance;
	}

	private RoleConfig<? extends DsfRole> getRoleConfig(String roleConfigString)
	{
		Function<String, Coding> practitionerRoleFactory = role ->
		{
			if (role != null)
			{
				String[] roleParts = role.split("\\|");
				if (roleParts.length == 2)
					return new Coding().setSystem(roleParts[0]).setCode(roleParts[1]);
			}

			return null;
		};
		return new RoleConfigReader().read(roleConfigString,
				roleKeyAndValues -> TestRole.isValid(roleKeyAndValues.key()) ? TestRole.valueOf(roleKeyAndValues.key())
						: null,
				practitionerRoleFactory);
	}
}
