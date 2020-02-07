package io.bucht.dropwizard.shiro;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ShiroConfiguration {

    @NotNull
    @JsonProperty("shiroConfigLocations")
    private String[] shiroConfigLocations = {"classpath:shiro.ini"};

    @JsonProperty("filterUrlPatterns")
    private String[] filterUrlPatterns = {"/*"};

    public String[] getShiroConfigLocations() {
        return shiroConfigLocations;
    }

    public String[] getFilterUrlPatterns() {
        return filterUrlPatterns;
    }
}