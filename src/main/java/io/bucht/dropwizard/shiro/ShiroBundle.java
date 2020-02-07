package io.bucht.dropwizard.shiro;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Environment;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.jaxrs.ShiroAnnotationFilterFeature;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.function.Supplier;

public abstract class ShiroBundle<T> implements ConfiguredBundle<T> {

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(new ShiroAnnotationFilterFeature());
        environment.jersey().register((Supplier<Subject>) SecurityUtils::getSubject);

        ShiroConfiguration shiroConfiguration = getShiroConfiguration(configuration);

        environment.servlets()
                .addFilter("ShiroFilter", createFilter(configuration))
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, shiroConfiguration.getFilterUrlPatterns());
    }

    protected abstract ShiroConfiguration narrow(T configuration);

    protected Filter createFilter(final T configuration) {
        final WebEnvironment webEnvironment = getWebEnvironment(configuration);
        final Collection<Realm> realms = getRealms(configuration);

        return new AbstractShiroFilter() {

            @Override
            public void init() throws Exception {
                setSecurityManager(getWebSecurityManager(realms, webEnvironment));
                setFilterChainResolver(webEnvironment.getFilterChainResolver());
            }
        };
    }

    protected WebSecurityManager getWebSecurityManager(Collection<Realm> realms, WebEnvironment webEnv) {
        if (realms.isEmpty()) {
            return webEnv.getWebSecurityManager();
        }

        return new DefaultWebSecurityManager(realms);
    }

    protected WebEnvironment getWebEnvironment(T configuration) {
        ShiroConfiguration shiroConfiguration = getShiroConfiguration(configuration);

        IniWebEnvironment env = new IniWebEnvironment();
        env.setConfigLocations(shiroConfiguration.getShiroConfigLocations());
        env.init();

        return env;
    }

    protected Collection<Realm> getRealms(T configuration) {
        return Collections.emptyList();
    }

    private ShiroConfiguration getShiroConfiguration(T configuration) {
        ShiroConfiguration shiroConfig = narrow(configuration);

        if (shiroConfig != null) {
            return shiroConfig;
        }

        return new ShiroConfiguration();
    }
}