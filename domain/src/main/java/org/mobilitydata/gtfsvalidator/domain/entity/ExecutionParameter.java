package org.mobilitydata.gtfsvalidator.domain.entity;

public class ExecutionParameter {

    private final String shortName;
    private final String longName;
    private final String description;
    private final boolean hasArgument;
    private final String value;

    public ExecutionParameter(final String shortName,
                              final String longName,
                              final String description,
                              final boolean hasArgument,
                              final String value) {
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
}