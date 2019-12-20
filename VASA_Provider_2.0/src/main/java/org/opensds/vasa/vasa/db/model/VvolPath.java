

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

/**
 * 功能描述
 * VVol path model
 *
 * @author h00451513
 * @since 2019-02-27
 */
public class VvolPath {

    private String vvolid;

    private String fileSystemName;

    private String path;

    private String sharePath;

    private String shareId;

    private String fileSystemId;

    private String snapshotId;

    private Boolean isBind;

    public VvolPath() {
        this.isBind = false;
    }

    @Override
    public String toString() {
        return "VvolPath{" +
                "vvolid='" + vvolid + '\'' +
                ", fileSystemName='" + fileSystemName + '\'' +
                ", path='" + path + '\'' +
                ", sharePath='" + sharePath + '\'' +
                ", shareId='" + shareId + '\'' +
                ", fileSystemId='" + fileSystemId + '\'' +
                ", snapshotId='" + snapshotId + '\'' +
                ", isBind=" + isBind +
                '}';
    }

    public Boolean getBind() {
        return isBind;
    }

    public void setBind(Boolean bind) {
        isBind = bind;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFileSystemId() {
        return fileSystemId;
    }

    public void setFileSystemId(String fileSystemId) {
        this.fileSystemId = fileSystemId;
    }

    public String getVvolid() {
        return vvolid;
    }

    public void setVvolid(String vvolid) {
        this.vvolid = vvolid;
    }

    public String getFileSystemName() {
        return fileSystemName;
    }

    public void setFileSystemName(String fileSystemName) {
        this.fileSystemName = fileSystemName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSharePath() {
        return sharePath;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }


}
