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

import java.util.ArrayList;
import java.util.List;

public class DLun extends DBaseStorageEntity {

    protected List<String> alternateIdentifier;

    protected DBackingConfig backingConfig;
    protected Long capacityInMB;

    protected String displayName;
    protected Boolean drsManagementPermitted;

    protected String esxLunIdentifier;
    protected Boolean thinProvisioned;

    protected String thinProvisioningStatus;
    protected Long usedSpaceInMB;

    public List<String> getAlternateIdentifier() {
        if (this.alternateIdentifier == null)
            this.alternateIdentifier = new ArrayList();
        return this.alternateIdentifier;
    }

    public DBackingConfig getBackingConfig() {
        return this.backingConfig;
    }

    public void setBackingConfig(DBackingConfig paramBackingConfig) {
        this.backingConfig = paramBackingConfig;
    }

    public Long getCapacityInMB() {
        return this.capacityInMB;
    }

    public void setCapacityInMB(Long paramLong) {
        this.capacityInMB = paramLong;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String paramString) {
        this.displayName = paramString;
    }

    public Boolean isDrsManagementPermitted() {
        return this.drsManagementPermitted;
    }

    public void setDrsManagementPermitted(Boolean paramBoolean) {
        this.drsManagementPermitted = paramBoolean;
    }

    public String getEsxLunIdentifier() {
        return this.esxLunIdentifier;
    }

    public void setEsxLunIdentifier(String paramString) {
        this.esxLunIdentifier = paramString;
    }

    public Boolean isThinProvisioned() {
        return this.thinProvisioned;
    }

    public void setThinProvisioned(Boolean paramBoolean) {
        this.thinProvisioned = paramBoolean;
    }

    public String getThinProvisioningStatus() {
        return this.thinProvisioningStatus;
    }

    public void setThinProvisioningStatus(String paramString) {
        this.thinProvisioningStatus = paramString;
    }

    public Long getUsedSpaceInMB() {
        return this.usedSpaceInMB;
    }

    public void setUsedSpaceInMB(Long paramLong) {
        this.usedSpaceInMB = paramLong;
    }
}
