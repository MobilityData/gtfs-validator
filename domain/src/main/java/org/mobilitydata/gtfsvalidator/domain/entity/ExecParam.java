package org.mobilitydata.gtfsvalidator.domain.entity;

public class ExecParam {
    private String paramKey;
    private String paramValue;

    public ExecParam() {
    }

    public ExecParam(String paramKey,
                     String paramValue) {
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    public String getKey() {
        return key;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setParamValue(final String paramValue) {
        this.paramValue = paramValue;
    }
}