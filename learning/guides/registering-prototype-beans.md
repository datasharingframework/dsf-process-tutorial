### Registering Prototype Beans

All Beans have to be registered in a class annotated with Spring Framework's `@Configuration` annotation.
This configuration class has to be returned by `ProcessPluginDefinition#getSpringConfigurations`. If the class you want to 
register as a Bean has a default constructor you may use the `ActivityPrototypeBeanCreator` to register your Bean:  
```java
@Bean
public static ActivityPrototypeBeanCreator activityPrototypeBeanCreator()
{
	return new ActivityPrototypeBeanCreator(ClassToRegister.class, AnotherClassToRegister.class);
}
```

If your class does not have a default constructor you need to register it as a Bean separately:

```java
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public ClassToRegister classToRegister()
{
    return new ClassToRegister(arg1, arg2);
}
```