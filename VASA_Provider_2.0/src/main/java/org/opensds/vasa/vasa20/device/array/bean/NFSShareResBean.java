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

package org.opensds.vasa.vasa20.device.array.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NFSShareResBean {
    @JsonProperty
    private String AUDITITEMS;

    @JsonProperty
    private String CHARACTERENCODING;

    @JsonProperty
    private String DESCRIPTION;

    @JsonProperty
    private String ENABLESHOWSNAPSHOT;

    @JsonProperty
    private String FSID;

    @JsonProperty
    private String ID;

    @JsonProperty
    private String LOCKPOLICY;

    @JsonProperty
    private String NAME;

    @JsonProperty
    private String SHAREPATH;

    @JsonProperty
    private Long TYPE;

    public String getAUDITITEMS() {
        return AUDITITEMS;
    }

    public void setAUDITITEMS(String aUDITITEMS) {
        AUDITITEMS = aUDITITEMS;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String isENABLESHOWSNAPSHOT() {
        return ENABLESHOWSNAPSHOT;
    }

    public void setENABLESHOWSNAPSHOT(String eNABLESHOWSNAPSHOT) {
        ENABLESHOWSNAPSHOT = eNABLESHOWSNAPSHOT;
    }

    public String getFSID() {
        return FSID;
    }

    public void setFSID(String fSID) {
        FSID = fSID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getLOCKPOLICY() {
        return LOCKPOLICY;
    }

    public void setLOCKPOLICY(String lOCKPOLICY) {
        LOCKPOLICY = lOCKPOLICY;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getSHAREPATH() {
        return SHAREPATH;
    }

    public void setSHAREPATH(String sHAREPATH) {
        SHAREPATH = sHAREPATH;
    }

    public String getCHARACTERENCODING() {
        return CHARACTERENCODING;
    }

    public void setCHARACTERENCODING(String cHARACTERENCODING) {
        CHARACTERENCODING = cHARACTERENCODING;
    }

    public Long getTYPE() {
        return TYPE;
    }

    public void setTYPE(Long tYPE) {
        TYPE = tYPE;
    }


}
