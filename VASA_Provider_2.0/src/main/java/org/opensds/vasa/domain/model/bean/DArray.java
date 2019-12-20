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

public class DArray extends DBaseStorageEntity {

    protected List<String> alternateName;

    protected String arrayName;

    protected String firmware;

    protected String modelId;

    protected int priority;

    protected List<String> supportedBlock;

    protected List<String> supportedFileSystem;

    protected List<String> supportedProfile;

    protected String vendorId;

    public List<String> getAlternateName() {
        if (this.alternateName == null)
            this.alternateName = new ArrayList();
        return this.alternateName;
    }

    public String getArrayName() {
        return this.arrayName;
    }

    public void setArrayName(String paramString) {
        this.arrayName = paramString;
    }

    public String getFirmware() {
        return this.firmware;
    }

    public void setFirmware(String paramString) {
        this.firmware = paramString;
    }

    public String getModelId() {
        return this.modelId;
    }

    public void setModelId(String paramString) {
        this.modelId = paramString;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer paramInteger) {
        this.priority = paramInteger;
    }

    public List<String> getSupportedBlock() {
        if (this.supportedBlock == null)
            this.supportedBlock = new ArrayList();
        return this.supportedBlock;
    }

    public List<String> getSupportedFileSystem() {
        if (this.supportedFileSystem == null)
            this.supportedFileSystem = new ArrayList();
        return this.supportedFileSystem;
    }

    public List<String> getSupportedProfile() {
        if (this.supportedProfile == null)
            this.supportedProfile = new ArrayList();
        return this.supportedProfile;
    }

    public String getVendorId() {
        return this.vendorId;
    }

    public void setVendorId(String paramString) {
        this.vendorId = paramString;
    }

    @Override
    public String toString() {
        return "DArray [alternateName=" + alternateName + ", arrayName=" + arrayName + ", firmware=" + firmware
                + ", modelId=" + modelId + ", priority=" + priority + ", supportedBlock=" + supportedBlock
                + ", supportedFileSystem=" + supportedFileSystem + ", supportedProfile=" + supportedProfile
                + ", vendorId=" + vendorId + "]";
    }

}
