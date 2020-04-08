package org.mobilitydata.gtfsvalidator.domain.entity;

public class ExecutionParameter {
    private String shortName;
    private String longName;
    private String description;
    private boolean hasArgument;
    private String value;

    public ExecutionParameter() {
    }

    public ExecutionParameter(String shortName,
                              String longName,
                              String description,
                              boolean hasArgument,
                              String value) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.hasArgument = hasArgument;
        this.value = value;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasArgument() {
        return hasArgument;
    }

    public String getValue() {
        return value;
    }

    public ExecutionParameter setShortName(final String shortName) {
        this.shortName = shortName;
        return this;
    }

    public ExecutionParameter setLongName(final String longName) {
        this.longName = longName;
        return this;
    }

    public ExecutionParameter setDescription(final String description) {
        this.description = description;
        return this;
    }

    public ExecutionParameter setHasArgument(final boolean hasArgument) {
        this.hasArgument = hasArgument;
        return this;
    }

    public ExecutionParameter setValue(final String value) {
        this.value = value;
        return this;
    }
}