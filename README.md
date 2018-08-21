

# Glue - dependency injection framework

Every bean must be annotated with @GlueBean.

## HOW IT WORKS:
Initially, all the bindings ( interface:implClass) will be added in the Binder.beanMap
Then, the map will create all the objects needed, within the same map