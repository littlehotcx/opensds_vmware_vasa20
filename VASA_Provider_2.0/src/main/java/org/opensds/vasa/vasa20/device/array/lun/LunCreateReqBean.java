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

public class LunCreateReqBean {

    private String ID;
    private String NAME;
    private String PARENTID;
    private String DESCRIPTION;
    private int SUBTYPE;
    private int ALLOCTYPE;
    private long CAPACITY;
    private int DATATRANSFERPOLICY;
    private int USAGETYPE;
    private int IOPRIORITY = 1;

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

    public int getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(int sUBTYPE) {
        SUBTYPE = sUBTYPE;
    }

    public int getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(int aLLOCTYPE) {
        ALLOCTYPE = aLLOCTYPE;
    }

    public long getCAPACITY() {
        return CAPACITY;
    }

    public void setCAPACITY(long cAPACITY) {
        CAPACITY = cAPACITY;
    }

    public int getDATATRANSFERPOLICY() {
        return DATATRANSFERPOLICY;
    }

    public void setDATATRANSFERPOLICY(int dATATRANSFERPOLICY) {
        DATATRANSFERPOLICY = dATATRANSFERPOLICY;
    }

    public int getUSAGETYPE() {
        return USAGETYPE;
    }

    public void setUSAGETYPE(int uSAGETYPE) {
        USAGETYPE = uSAGETYPE;
    }

    @Override
    public String toString() {
        return "LunCreateReqBean [ID=" + ID + ", NAME=" + NAME + ", PARENTID="
                + PARENTID + ", DESCRIPTION=" + DESCRIPTION + ", SUBTYPE="
                + SUBTYPE + ", ALLOCTYPE=" + ALLOCTYPE + ", CAPACITY="
                + CAPACITY + ", DATATRANSFERPOLICY=" + DATATRANSFERPOLICY
                + ", USAGETYPE=" + USAGETYPE + "]";
    }

    public int getIOPRIORITY() {
        return IOPRIORITY;
    }

    public void setIOPRIORITY(int iOPRIORITY) {
        IOPRIORITY = iOPRIORITY;
    }


}
