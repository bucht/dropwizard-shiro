
# Dropwizard Shiro bundle

Based on https://github.com/silb/dropwizard-shiro and rewritten and simplified to support Jersey 2.30 and Dropwizard 2.0.

Defaults to check for configuration in "classpath:shiro.ini" and adds a filter with an url pattern of "/\*"

# Use
```
<dependency>
  <groupId>io.dropwizard</groupId>
  <artifactId>dropwizard-auth</artifactId>
  <version>2.0.1</version>
</dependency>

<dependency>
  <groupId>io.bucht.dropwizard</groupId>
  <artifactId>dropwizard-shiro</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Add bundle to application:

```
private final ShiroBundle<MyConfiguration> shiro = new ShiroBundle<>() {

    @Override
    protected ShiroConfiguration narrow(MyConfiguration configuration) {
        return configuration.shiro;
    }

    @Override
    protected Collection<Realm> getRealms(MyConfiguration configuration) {
        return Collections.singleton(new MyRealm());
    }
}

[...]

public void initialize(final Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(shiro);
    [...]
}
```

Add support for injecting custom Principal and exception mapping for Shiro's AuthorizationException:

```
env.jersey().register(new AuthValueFactoryProvider.Binder<>(MyPrincipal.class));
env.jersey().register(new MyAuthorizationExceptionMapper());

```

