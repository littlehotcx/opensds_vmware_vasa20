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

package org.opensds.vasa.vasa20.device.array.qos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author j00405142
 */
public class QosCreateResBean {

    private String ID;
    private String NAME;
    private String DESCRIPTION;
    private String HEALTHSTATUS;
    private String RUNNINGSTATUS;
    private String IOTYPE;
    private String ENABLESTATUS;
    private String LUNLIST;

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

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
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

    public String getIOTYPE() {
        return IOTYPE;
    }

    public void setIOTYPE(String iOTYPE) {
        IOTYPE = iOTYPE;
    }

    public String getENABLESTATUS() {
        return ENABLESTATUS;
    }

    public void setENABLESTATUS(String eNABLESTATUS) {
        ENABLESTATUS = eNABLESTATUS;
    }

    public String getLUNLIST() {
        return LUNLIST;
    }

    public void setLUNLIST(String lUNLIST) {
        LUNLIST = lUNLIST;
    }

    @Override
    public String toString() {
        return "QosCreateResBean [ID=" + ID + ", NAME=" + NAME
                + ", DESCRIPTION=" + DESCRIPTION + ", HEALTHSTATUS="
                + HEALTHSTATUS + ", RUNNINGSTATUS=" + RUNNINGSTATUS
                + ", IOTYPE=" + IOTYPE + ", ENABLESTATUS=" + ENABLESTATUS
                + ", LUNLIST=" + LUNLIST + "]";
    }


}
