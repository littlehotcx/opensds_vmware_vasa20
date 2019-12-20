/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.db.model;

public class NCapabilityMetadata {
    private int id;

    private String category;

    private String capabilityId;

    private String namespace;

    private Boolean mandatory;

    private String key;

    private String summary;

    private int type;

    private int requirementsTypeHint;

    private String defaultValue;

    private String allowedValues;

    @Override
    public String toString() {
        return new StringBuilder().append("[id:").append(id).append(", category:").append(category)
                .append(", capabilityId:").append(capabilityId).append(", namespace:").append(namespace)
                .append(", mandatory:").append(mandatory).append(", key:").append(key)
                .append(", summary:").append(summary).append(", type:").append(type)
                .append(", requirementsTypeHint:").append(requirementsTypeHint).append(", defaultValue:").append(defaultValue)
                .append(", allowedValues:").append(allowedValues).append("]").toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRequirementsTypeHint() {
        return requirementsTypeHint;
    }

    public void setRequirementsTypeHint(int requirementsTypeHint) {
        this.requirementsTypeHint = requirementsTypeHint;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
        this.allowedValues = allowedValues;
    }

}
