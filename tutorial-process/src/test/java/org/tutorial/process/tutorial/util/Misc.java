package org.tutorial.process.tutorial.util;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import dev.dsf.bpe.v2.activity.DefaultUserTaskListener;
import dev.dsf.bpe.v2.spring.ActivityPrototypeBeanCreator;
import org.tutorial.process.tutorial.spring.config.TutorialConfig;

public class Misc
{
	public static List<Class<? extends DefaultUserTaskListener>> getUserTaskListeners(String packageName)
	{
		Reflections reflections = new Reflections(packageName, Scanners.SubTypes);
		return new ArrayList<>(reflections.getSubTypesOf(DefaultUserTaskListener.class));
	}

	public static long countBeanMethods(Class<?> returnType)
	{
		long beanCount = 0;
		long numBeanMethods = Arrays.stream(TutorialConfig.class.getMethods())
				.filter(m -> returnType.equals(m.getReturnType())).filter(m -> Modifier.isPublic(m.getModifiers()))
				.filter(m -> m.getAnnotation(Bean.class) != null).count();
		beanCount += numBeanMethods;

		TutorialConfig tutorialConfig = new TutorialConfig();
		BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
		Arrays.stream(TutorialConfig.class.getMethods())
				.filter(m -> ActivityPrototypeBeanCreator.class.equals(m.getReturnType()))
				.filter(m -> Modifier.isPublic(m.getModifiers())).map(m ->
				{
					try
					{
						return (ActivityPrototypeBeanCreator) m.invoke(tutorialConfig);
					}
					catch (IllegalAccessException | InvocationTargetException e)
					{
						throw new RuntimeException(e);
					}
				}).forEach(a -> a.postProcessBeanDefinitionRegistry(beanDefinitionRegistry));
		long numBeansInActivityPrototypeBeanCreator = Arrays.stream(beanDefinitionRegistry.getBeanDefinitionNames())
				.map(beanDefinitionRegistry::getBeanDefinition)
				.map(definition -> definition.getResolvableType().getRawClass()).filter(returnType::equals).count();
		beanCount += numBeansInActivityPrototypeBeanCreator;
		return beanCount;
	}

	public static String errorMessageBeanMethod(Class<?> returnType)
	{
		return "One public spring bean method with return type " + returnType.getSimpleName() + ", annotation "
				+ Bean.class.getSimpleName() + " and annotation " + Scope.class.getSimpleName() + " with type "
				+ SCOPE_PROTOTYPE + " expected in " + TutorialConfig.class.getSimpleName();
	}
}
