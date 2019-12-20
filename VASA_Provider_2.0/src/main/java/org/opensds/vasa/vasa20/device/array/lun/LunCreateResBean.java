
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

package org.opensds.vasa.vasa20.device.array.lun;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LunCreateResBean {

    @JsonProperty
    private String TYPE;
    @JsonProperty
    private String ID;
    @JsonProperty
    private String NAME;
    @JsonProperty
    private String PARENTID;
    @JsonProperty
    private String DESCRIPTION;
    @JsonProperty
    private String SUBTYPE;
    @JsonProperty
    private String ALLOCTYPE;
    @JsonProperty
    private String CAPACITY;
    @JsonProperty
    private String DATATRANSFERPOLICY;
    @JsonProperty
    private String USAGETYPE;
    @JsonProperty
    private String HEALTHSTATUS;
    @JsonProperty
    private String RUNNINGSTATUS;
    @JsonProperty
    private String ALLOCCAPACITY;
    @JsonProperty
    private String SECTORSIZE;
    @JsonProperty
    private String WWN;
    @JsonProperty
    private String OWNINGCONTROLLER;

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String pARENTID) {
        PARENTID = pARENTID;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String sUBTYPE) {
        SUBTYPE = sUBTYPE;
    }

    public String getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(String aLLOCTYPE) {
        ALLOCTYPE = aLLOCTYPE;
    }

    public String getCAPACITY() {
        return CAPACITY;
    }

    public void setCAPACITY(String cAPACITY) {
        CAPACITY = cAPACITY;
    }

    public String getDATATRANSFERPOLICY() {
        return DATATRANSFERPOLICY;
    }

    public void setDATATRANSFERPOLICY(String dATATRANSFERPOLICY) {
        DATATRANSFERPOLICY = dATATRANSFERPOLICY;
    }

    public String getUSAGETYPE() {
        return USAGETYPE;
    }

    public void setUSAGETYPE(String uSAGETYPE) {
        USAGETYPE = uSAGETYPE;
    }

    public String getHEALTHSTATUS() {
        return HEALTHSTATUS;
    }

    public void setHEALTHSTATUS(String hEALTHSTATUS) {
        HEALTHSTATUS = hEALTHSTATUS;
    }

    public String getRUNNINGSTATUS() {
        return RUNNINGSTATUS;
    }

    public void setRUNNINGSTATUS(String rUNNINGSTATUS) {
        RUNNINGSTATUS = rUNNINGSTATUS;
    }

    public String getALLOCCAPACITY() {
        return ALLOCCAPACITY;
    }

    public void setALLOCCAPACITY(String aLLOCCAPACITY) {
        ALLOCCAPACITY = aLLOCCAPACITY;
    }

    public String getSECTORSIZE() {
        return SECTORSIZE;
    }

    public void setSECTORSIZE(String sECTORSIZE) {
        SECTORSIZE = sECTORSIZE;
    }

    public String getWWN() {
        return WWN;
    }

    public void setWWN(String wWN) {
        WWN = wWN;
    }

    public String getOWNINGCONTROLLER() {
        return OWNINGCONTROLLER;
    }

    public void setOWNINGCONTROLLER(String oWNINGCONTROLLER) {
        OWNINGCONTROLLER = oWNINGCONTROLLER;
    }

    @Override
    public String toString() {
        return "LunCreateResBean [TYPE=" + TYPE + ", ID=" + ID + ", NAME="
                + NAME + ", PARENTID=" + PARENTID + ", DESCRIPTION="
                + DESCRIPTION + ", SUBTYPE=" + SUBTYPE + ", ALLOCTYPE="
                + ALLOCTYPE + ", CAPACITY=" + CAPACITY
                + ", DATATRANSFERPOLICY=" + DATATRANSFERPOLICY + ", USAGETYPE="
                + USAGETYPE + ", HEALTHSTATUS=" + HEALTHSTATUS
                + ", RUNNINGSTATUS=" + RUNNINGSTATUS + ", ALLOCCAPACITY="
                + ALLOCCAPACITY + ", SECTORSIZE=" + SECTORSIZE + ", WWN=" + WWN
                + "]";
    }


}
