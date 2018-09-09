package com.taobao.pojo;

import java.io.Serializable;

public class PropertyAndValue implements Serializable {
    private Property property;
    private PropertyValue propertyValue;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }
}
