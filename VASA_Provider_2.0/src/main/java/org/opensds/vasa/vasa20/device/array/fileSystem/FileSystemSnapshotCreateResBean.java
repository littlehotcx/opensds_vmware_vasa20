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

public class FileSystemSnapshotCreateResBean {

    @JsonProperty
    private String ID;

    @JsonProperty
    private String NAME;

    @JsonProperty
    private String PARENTID;

    @JsonProperty
    private String PARENTNAME;

    @JsonProperty
    private String PARENTTYPE;

    @JsonProperty
    private String TIMESTAMP;

    @JsonProperty
    private String TYPE;

    @JsonProperty
    private String USERCAPACITY;

    @JsonProperty
    private String HEALTHSTATUS;

    @JsonProperty
    private String CONSUMEDCAPACITY;

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

    public String getPARENTNAME() {
        return PARENTNAME;
    }

    public void setPARENTNAME(String PARENTNAME) {
        this.PARENTNAME = PARENTNAME;
    }

    public String getPARENTTYPE() {
        return PARENTTYPE;
    }

    public void setPARENTTYPE(String PARENTTYPE) {
        this.PARENTTYPE = PARENTTYPE;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getUSERCAPACITY() {
        return USERCAPACITY;
    }

    public void setUSERCAPACITY(String USERCAPACITY) {
        this.USERCAPACITY = USERCAPACITY;
    }

    public String getHEALTHSTATUS() {
        return HEALTHSTATUS;
    }

    public void setHEALTHSTATUS(String HEALTHSTATUS) {
        this.HEALTHSTATUS = HEALTHSTATUS;
    }

    public String getCONSUMEDCAPACITY() {
        return CONSUMEDCAPACITY;
    }

    public void setCONSUMEDCAPACITY(String CONSUMEDCAPACITY) {
        this.CONSUMEDCAPACITY = CONSUMEDCAPACITY;
    }

    @Override
    public String toString() {
        return "FileSystemSnapshotCreateResBean{" +
                "ID='" + ID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", PARENTID='" + PARENTID + '\'' +
                ", PARENTNAME='" + PARENTNAME + '\'' +
                ", PARENTTYPE='" + PARENTTYPE + '\'' +
                ", TIMESTAMP='" + TIMESTAMP + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", USERCAPACITY='" + USERCAPACITY + '\'' +
                ", HEALTHSTATUS='" + HEALTHSTATUS + '\'' +
                ", CONSUMEDCAPACITY='" + CONSUMEDCAPACITY + '\'' +
                '}';
    }
}
