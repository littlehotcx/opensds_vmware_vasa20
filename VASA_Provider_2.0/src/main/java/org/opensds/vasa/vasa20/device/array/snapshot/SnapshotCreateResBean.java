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

package org.opensds.vasa.vasa20.device.array.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotCreateResBean {

    private String ID;
    private String WWN;
    private String TYPE;
    private String NAME;
    private String PARENTTYPE;
    private String PARENTID;
    private String PARENTNAME;
    private String HEALTHSTATUS;
    private String RUNNINGSTATUS;
    private String ROLLBACKSTARTTIME;
    private String ROLLBACKENDTIME;
    private String ROLLBACKSPEED;
    private String ROLLBACKRATE;
    private String USERCAPACITY;
    private String CONSUMEDCAPACITY;

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getPARENTTYPE() {
        return PARENTTYPE;
    }

    public void setPARENTTYPE(String pARENTTYPE) {
        PARENTTYPE = pARENTTYPE;
    }

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String pARENTID) {
        PARENTID = pARENTID;
    }

    public String getPARENTNAME() {
        return PARENTNAME;
    }

    public void setPARENTNAME(String pARENTNAME) {
        PARENTNAME = pARENTNAME;
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

    public String getROLLBACKSTARTTIME() {
        return ROLLBACKSTARTTIME;
    }

    public void setROLLBACKSTARTTIME(String rOLLBACKSTARTTIME) {
        ROLLBACKSTARTTIME = rOLLBACKSTARTTIME;
    }

    public String getROLLBACKENDTIME() {
        return ROLLBACKENDTIME;
    }

    public void setROLLBACKENDTIME(String rOLLBACKENDTIME) {
        ROLLBACKENDTIME = rOLLBACKENDTIME;
    }

    public String getROLLBACKSPEED() {
        return ROLLBACKSPEED;
    }

    public void setROLLBACKSPEED(String rOLLBACKSPEED) {
        ROLLBACKSPEED = rOLLBACKSPEED;
    }

    public String getROLLBACKRATE() {
        return ROLLBACKRATE;
    }

    public void setROLLBACKRATE(String rOLLBACKRATE) {
        ROLLBACKRATE = rOLLBACKRATE;
    }

    public String getUSERCAPACITY() {
        return USERCAPACITY;
    }

    public void setUSERCAPACITY(String uSERCAPACITY) {
        USERCAPACITY = uSERCAPACITY;
    }

    public String getCONSUMEDCAPACITY() {
        return CONSUMEDCAPACITY;
    }

    public void setCONSUMEDCAPACITY(String cONSUMEDCAPACITY) {
        CONSUMEDCAPACITY = cONSUMEDCAPACITY;
    }

    @Override
    public String toString() {
        return "SnapshotCreateResBean [ID=" + ID + ", WWN=" + WWN + ", TYPE="
                + TYPE + ", NAME=" + NAME + ", PARENTTYPE=" + PARENTTYPE
                + ", PARENTID=" + PARENTID + ", PARENTNAME=" + PARENTNAME
                + ", HEALTHSTATUS=" + HEALTHSTATUS + ", RUNNINGSTATUS="
                + RUNNINGSTATUS + ", ROLLBACKSTARTTIME=" + ROLLBACKSTARTTIME
                + ", ROLLBACKENDTIME=" + ROLLBACKENDTIME + ", ROLLBACKSPEED="
                + ROLLBACKSPEED + ", ROLLBACKRATE=" + ROLLBACKRATE
                + ", USERCAPACITY=" + USERCAPACITY + ", CONSUMEDCAPACITY="
                + CONSUMEDCAPACITY + "]";
    }

    public String getWWN() {
        return WWN;
    }

    public void setWWN(String wWN) {
        WWN = wWN;
    }

}
