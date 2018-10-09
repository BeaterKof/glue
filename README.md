

# Glue - dependency injection framework

Every bean must be annotated with @GlueBean.
Every dependency must be annotated with @Inject.
By default, the bean Scope is Singleton.

## HOW IT WORKS:
Initially, all the bindings ( interface:implClass) will be added in the Binder.beanMap
Then, the map will create and add the Singleton objects within the same map.
The prototype objects will be created at runtime, whenever getBean(...) is called.