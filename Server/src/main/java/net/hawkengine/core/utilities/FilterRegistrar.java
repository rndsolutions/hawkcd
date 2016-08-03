package net.hawkengine.core.utilities;

import net.hawkengine.core.utilities.securityFilters.AuthenticationFilter;
import net.hawkengine.core.utilities.securityFilters.ResponseFilter;

import org.glassfish.jersey.server.ResourceConfig;

public class FilterRegistrar extends ResourceConfig {

    public FilterRegistrar() {
        super.register(AuthenticationFilter.class);
        super.register(ResponseFilter.class);
        System.out.println("filters registered");
    }
}
