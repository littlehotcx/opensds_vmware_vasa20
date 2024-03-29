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

package org.opensds.vasa.vasa.rest.bean;

import java.util.List;

import org.opensds.vasa.vasa.db.model.VasaProperty;

public class VasaPropertyRes extends ResponseHeader {

    private List<VasaProperty> properties;

    public List<VasaProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<VasaProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "VasaPropertyRes [properties=" + properties + ", getResultCode()=" + getResultCode()
                + ", getResultDescription()=" + getResultDescription() + "]";
    }


}
