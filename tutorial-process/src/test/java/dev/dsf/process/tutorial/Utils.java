package dev.dsf.process.tutorial;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Resource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import ca.uhn.fhir.context.FhirContext;
import dev.dsf.bpe.v2.ProcessPluginDefinition;
import dev.dsf.bpe.v2.activity.DefaultUserTaskListener;
import dev.dsf.process.tutorial.exercise_1.TutorialProcessPluginDefinitionTest;
import dev.dsf.process.tutorial.spring.config.TutorialConfig;

public class Utils
{
	public static List<Class<? extends DefaultUserTaskListener>> getUserTaskListeners(String packageName)
	{
		Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
		return new ArrayList<>(reflections.getSubTypesOf(DefaultUserTaskListener.class));
	}

	public static long countBeanMethods(Class<?> returnType)
	{
		return Arrays.stream(TutorialConfig.class.getMethods()).filter(m -> returnType.equals(m.getReturnType()))
				.filter(m -> Modifier.isPublic(m.getModifiers())).filter(m -> m.getAnnotation(Bean.class) != null)
				.count();
	}

	public static String errorMessageBeanMethod(Class<?> returnType)
	{
		return "One public spring bean method with return type " + returnType.getSimpleName() + ", annotation "
				+ Bean.class.getSimpleName() + " and annotation " + Scope.class.getSimpleName() + " with type "
				+ SCOPE_PROTOTYPE + " expected in " + TutorialConfig.class.getSimpleName();
	}
}
