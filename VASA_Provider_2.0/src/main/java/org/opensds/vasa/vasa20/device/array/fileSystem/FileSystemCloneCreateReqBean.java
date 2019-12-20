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

package org.opensds.vasa.vasa20.device.array.fileSystem;

//String name, String alloctype, String parentFilesystemid, String parentSnapshotId, String vstoreId
public class FileSystemCloneCreateReqBean {

    private String NAME;

    private String ALLOCTYPE;

    private String PARENTFILESYSTEMID;

    private String PARENTSNAPSHOTID;

    private String vstoreId;

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(String ALLOCTYPE) {
        this.ALLOCTYPE = ALLOCTYPE;
    }

    public String getPARENTFILESYSTEMID() {
        return PARENTFILESYSTEMID;
    }

    public void setPARENTFILESYSTEMID(String PARENTFILESYSTEMID) {
        this.PARENTFILESYSTEMID = PARENTFILESYSTEMID;
    }

    public String getPARENTSNAPSHOTID() {
        return PARENTSNAPSHOTID;
    }

    public void setPARENTSNAPSHOTID(String PARENTSNAPSHOTID) {
        this.PARENTSNAPSHOTID = PARENTSNAPSHOTID;
    }

    public String getVstoreId() {
        return vstoreId;
    }

    public void setVstoreId(String vstoreId) {
        this.vstoreId = vstoreId;
    }

    @Override
    public String toString() {
        return "FileSystemCloneCreateReqBean{" +
                "NAME='" + NAME + '\'' +
                ", ALLOCTYPE='" + ALLOCTYPE + '\'' +
                ", PARENTFILESYSTEMID='" + PARENTFILESYSTEMID + '\'' +
                ", PARENTSNAPSHOTID='" + PARENTSNAPSHOTID + '\'' +
                ", vstoreId='" + vstoreId + '\'' +
                '}';
    }
}
