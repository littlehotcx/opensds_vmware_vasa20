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

public class NProfile2VolType {
    private String profileName;

    private String profileId;

    private String voltypeId;

    private String voltypeName;

    private String containerId;

    private long generationId;

    private Date lastUseTime;

    private String thinThick;

    private String deprecated;

    @Override
    public String toString() {
        return new StringBuilder().append("[profileId:").append(profileId).append(", profileName:").append(profileName)
                .append(", voltypeId:").append(voltypeId).append(", voltypeName:").append(voltypeName)
                .append(", containerId:").append(containerId).append(", generationId:").append(generationId).append(", lastUseTime:").append(lastUseTime)
                .append(", thinThick:").append(thinThick).append(", deprecated:").append(deprecated).append("]").toString();
    }

    public String getProfileId() {
        return profileId;
    }

    public void SetProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getVoltypeId() {
        return voltypeId;
    }

    public void setVoltypeId(String voltypeId) {
        this.voltypeId = voltypeId;
    }

    public String getVoltypeName() {
        return voltypeName;
    }

    public void setVoltypeName(String voltypeName) {
        this.voltypeName = voltypeName;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Date getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(Date lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public void setThinThick(String thinThick) {
        this.thinThick = thinThick;
    }

    public String getThinThick() {
        return thinThick;
    }

    public long getGenerationId() {
        return generationId;
    }

    public void setGenerationId(long generationId) {
        this.generationId = generationId;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }
}
