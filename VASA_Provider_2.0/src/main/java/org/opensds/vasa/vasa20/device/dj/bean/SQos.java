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

public class SQos {
    private SQosSpecsInfo qos_specs;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[qos_spesc:").append(qos_specs.toString()).append("]").toString();
    }

    public SQosSpecsInfo getQos_specs() {
        return qos_specs;
    }

    public void setQos_specs(SQosSpecsInfo qos_specs) {
        this.qos_specs = qos_specs;
    }

}
