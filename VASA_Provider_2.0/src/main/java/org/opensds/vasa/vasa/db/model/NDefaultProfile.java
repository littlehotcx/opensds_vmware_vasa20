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

public class NDefaultProfile {
    private int id;

    private String name;

    private String profileId;

    private String proName;

    private int proType;

    private int proRequirementsTypeHint;

    private String proValue;

    private String createdAt;

    @Override
    public String toString() {
        return new StringBuilder().append("[id:").append(id).append(", name:").append(name)
                .append(", profileId:").append(profileId).append(", proName:").append(proName).append(", proValue:").append(proValue)
                .append(", createdAt:").append(createdAt).append("]").toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProValue() {
        return proValue;
    }

    public void setProValue(String proValue) {
        this.proValue = proValue;
    }

    public int getProType() {
        return proType;
    }

    public void setProType(int proType) {
        this.proType = proType;
    }

    public int getProRequirementsTypeHint() {
        return proRequirementsTypeHint;
    }

    public void setProRequirementsTypeHint(int proRequirementsTypeHint) {
        this.proRequirementsTypeHint = proRequirementsTypeHint;
    }

}
