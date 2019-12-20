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

public class FilesystemShareTable {

    private String filesystemId;
    private String sharePath;
    private String shareId;
    private String hostId;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public FilesystemShareTable(String filesystemId, String sharePath, String shareId, String hostId) {
        super();
        this.filesystemId = filesystemId;
        this.sharePath = sharePath;
        this.shareId = shareId;
        this.hostId = hostId;
    }

    public FilesystemShareTable() {

    }

    public String getFilesystemId() {
        return filesystemId;
    }

    public void setFilesystemId(String filesystemId) {
        this.filesystemId = filesystemId;
    }

    public String getSharePath() {
        return sharePath;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    @Override
    public String toString() {
        return "FilesystemShareTable [filesystemId=" + filesystemId + ", sharePath=" + sharePath + ", shareId="
                + shareId + ", hostId=" + hostId + "]";
    }
}
