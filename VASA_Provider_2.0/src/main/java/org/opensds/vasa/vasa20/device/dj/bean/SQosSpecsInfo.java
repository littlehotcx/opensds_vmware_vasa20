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

package org.opensds.vasa.vasa20.device.dj.bean;

public class SQosSpecsInfo {
    private SNewSpecsInfo specs;

    private String id; //Qos id??????????

    private String name;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[specs:").append(specs.toString()).append(", id:").append(id).append(", name:").append(name).append("]").toString();
    }

    public SNewSpecsInfo getSpecs() {
        return specs;
    }

    public void setSpecs(SNewSpecsInfo specs) {
        this.specs = specs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
