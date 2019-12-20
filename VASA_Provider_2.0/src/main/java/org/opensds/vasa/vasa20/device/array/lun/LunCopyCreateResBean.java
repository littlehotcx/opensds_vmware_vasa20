
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

public class LunCopyCreateResBean {

    @JsonProperty
    private String ID;
    @JsonProperty
    private String NAME;
    @JsonProperty
    private String HEALTHSTATUS;
    @JsonProperty
    private String RUNNINGSTATUS;
    @JsonProperty
    private String DESCRIPTION;
    @JsonProperty
    private String SUBTYPE;
    @JsonProperty
    private String COPYSPEED;
    @JsonProperty
    private String WWN;

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

    public String getCOPYSPEED() {
        return COPYSPEED;
    }

    public void setCOPYSPEED(String cOPYSPEED) {
        COPYSPEED = cOPYSPEED;
    }

    public String getWWN() {
        return WWN;
    }

    public void setWWN(String wWN) {
        WWN = wWN;
    }

    @Override
    public String toString() {
        return "LunCopyCreateResBean [ID=" + ID + ", NAME=" + NAME
                + ", HEALTHSTATUS=" + HEALTHSTATUS + ", RUNNINGSTATUS="
                + RUNNINGSTATUS + ", DESCRIPTION=" + DESCRIPTION + ", SUBTYPE="
                + SUBTYPE + ", COPYSPEED=" + COPYSPEED + ", WWN=" + WWN + "]";
    }

}
