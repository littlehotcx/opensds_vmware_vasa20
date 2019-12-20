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

public class DFileSystem extends DBaseStorageEntity {
    protected DBackingConfig backingConfig;

    protected String fileSystem;

    protected List<DFileSystemInfo> fileSystemInfo;

    protected String fileSystemVersion;
    protected Boolean nativeSnapshotSupported;

    protected String thinProvisioningStatus;

    public DBackingConfig getBackingConfig() {
        return this.backingConfig;
    }

    public void setBackingConfig(DBackingConfig paramBackingConfig) {
        this.backingConfig = paramBackingConfig;
    }

    public String getFileSystem() {
        return this.fileSystem;
    }

    public void setFileSystem(String paramString) {
        this.fileSystem = paramString;
    }

    public List<DFileSystemInfo> getFileSystemInfo() {
        if (this.fileSystemInfo == null)
            this.fileSystemInfo = new ArrayList();
        return this.fileSystemInfo;
    }

    public String getFileSystemVersion() {
        return this.fileSystemVersion;
    }

    public void setFileSystemVersion(String paramString) {
        this.fileSystemVersion = paramString;
    }

    public Boolean isNativeSnapshotSupported() {
        return this.nativeSnapshotSupported;
    }

    public void setNativeSnapshotSupported(Boolean paramBoolean) {
        this.nativeSnapshotSupported = paramBoolean;
    }

    public String getThinProvisioningStatus() {
        return this.thinProvisioningStatus;
    }

    public void setThinProvisioningStatus(String paramString) {
        this.thinProvisioningStatus = paramString;
    }
}
