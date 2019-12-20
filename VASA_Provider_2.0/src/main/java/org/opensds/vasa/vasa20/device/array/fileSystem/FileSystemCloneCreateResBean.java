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

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileSystemCloneCreateResBean {

    @JsonProperty
    private String ID;

    @JsonProperty
    private String NAME;

    @JsonProperty
    private String PARENTID;

    @JsonProperty
    private String ALLOCTYPE;

    @JsonProperty
    private String ISCLONEFS;

    @JsonProperty
    private String AVAILABLECAPCITY;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String PARENTID) {
        this.PARENTID = PARENTID;
    }

    public String getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(String ALLOCTYPE) {
        this.ALLOCTYPE = ALLOCTYPE;
    }

    public String getISCLONEFS() {
        return ISCLONEFS;
    }

    public void setISCLONEFS(String ISCLONEFS) {
        this.ISCLONEFS = ISCLONEFS;
    }

    public String getAVAILABLECAPCITY() {
        return AVAILABLECAPCITY;
    }

    public void setAVAILABLECAPCITY(String AVAILABLECAPCITY) {
        this.AVAILABLECAPCITY = AVAILABLECAPCITY;
    }

    @Override
    public String toString() {
        return "FileSystemCloneCreateResBean{" +
                "ID='" + ID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", PARENTID='" + PARENTID + '\'' +
                ", ALLOCTYPE='" + ALLOCTYPE + '\'' +
                ", ISCLONEFS='" + ISCLONEFS + '\'' +
                ", AVAILABLECAPCITY='" + AVAILABLECAPCITY + '\'' +
                '}';
    }
}
