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

import java.util.Date;

public class NVvolProfile {
    private int id;

    private String vvolid;

    private String profileName;

    private String profileId;

    private String createdBy;

    private Date creationTime;

    private long generationId;

    private String capability;

    private int type;

    private String value;

    @Override
    public String toString() {
        return new StringBuilder().append("[id:").append(id).append(", vvolid:").append(vvolid)
                .append(", profileName:").append(profileName).append(", profileId:").append(profileId)
                .append(", createdBy:").append(createdBy).append(", creationTime:").append(creationTime)
                .append(", generationId:").append(generationId).append(", capability:").append(capability)
                .append(", type:").append(type).append(", value:").append(value)
                .append("]").toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVvolid() {
        return vvolid;
    }

    public void setVvolid(String vvolid) {
        this.vvolid = vvolid;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public long getGenerationId() {
        return generationId;
    }

    public void setGenerationId(long generationId) {
        this.generationId = generationId;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
