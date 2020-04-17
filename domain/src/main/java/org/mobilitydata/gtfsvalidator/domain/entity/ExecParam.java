package org.mobilitydata.gtfsvalidator.domain.entity;

public class ExecParam {

    private String key;
    private String shortName;
    private String longName;
    private String description;
    private boolean hasArgument;
    private String value;
    private String defaultValue;

    public ExecParam() {
    }

    public ExecParam(String key,
                     String shortName,
                     String longName,
                     String description,
                     boolean hasArgument,
                     String value) {
        this.key = key;
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.hasArgument = hasArgument;
        this.value = value;
    }

    public String getKey() {
        return key;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ExecParam setShortName(final String shortName) {
        this.shortName = shortName;
        return this;
    }

    public ExecParam setLongName(final String longName) {
        this.longName = longName;
        return this;
    }

    public ExecParam setDescription(final String description) {
        this.description = description;
        return this;
    }

    public ExecParam setHasArgument(final boolean hasArgument) {
        this.hasArgument = hasArgument;
        return this;
    }

    public ExecParam setValue(final String value) {
        this.value = value;
        return this;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}