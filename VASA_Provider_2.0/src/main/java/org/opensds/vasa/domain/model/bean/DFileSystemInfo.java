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

public class DFileSystemInfo {

    protected String fileServerName;

    protected String fileSystemPath;

    protected String ipAddress;

    public String getFileServerName() {
        return this.fileServerName;
    }

    public void setFileServerName(String paramString) {
        this.fileServerName = paramString;
    }

    public String getFileSystemPath() {
        return this.fileSystemPath;
    }

    public void setFileSystemPath(String paramString) {
        this.fileSystemPath = paramString;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String paramString) {
        this.ipAddress = paramString;
    }
}
