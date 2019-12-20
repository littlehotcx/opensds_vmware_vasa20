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

package org.opensds.vasa.domain.model.bean;

public class S2DVolumeType {
    private String id;

    private String name;

    private String created_at;

    private S2DExtraSpecsInfo extra_specs;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[id:").append(id).append(", name:").append(name).append(", created_at:").append(created_at).append("]").toString();
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public S2DExtraSpecsInfo getExtra_specs() {
        return extra_specs;
    }

    public void setExtra_specs(S2DExtraSpecsInfo extra_specs) {
        this.extra_specs = extra_specs;
    }
}
