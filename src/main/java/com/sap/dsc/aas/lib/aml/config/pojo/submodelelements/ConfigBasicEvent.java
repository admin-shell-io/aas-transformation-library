/* Copyright (C)2021 SAP SE or an affiliate company. All rights reserved. */
package com.sap.dsc.aas.lib.aml.config.pojo.submodelelements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.dsc.aas.lib.aml.config.pojo.AbstractConfigSubmodelElement;
import com.sap.dsc.aas.lib.aml.config.pojo.ConfigReference;

public class ConfigBasicEvent extends AbstractConfigSubmodelElement implements ConfigReferenceContainer {

    private ConfigReference observed;

    public ConfigBasicEvent() {}

    @JsonCreator
    public ConfigBasicEvent(@JsonProperty(required = true, value = "observed") ConfigReference observed) {
        this.setObserved(observed);
    }

    public ConfigReference getObserved() {
        return observed;
    }

    public void setObserved(ConfigReference observed) {
        this.observed = observed;
    }

    @Override
    public ConfigReference getReference() {
        return getObserved();
    }
}
