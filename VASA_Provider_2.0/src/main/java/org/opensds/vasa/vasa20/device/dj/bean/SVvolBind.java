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

package org.opensds.vasa.vasa20.device.dj.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SVvolBind extends SouthBaseBean {
    @JsonProperty
    private String HOSTID;

    @JsonProperty
    private String PELUNID;

    @JsonProperty
    private String VVOLID;

    @JsonProperty
    private String VVOLSECONDARYID;

    @JsonProperty
    private int BINDTYPE;

    public int getBINDTYPE() {
        return BINDTYPE;
    }

    public void setBINDTYPE(int bINDTYPE) {
        BINDTYPE = bINDTYPE;
    }

    public String getHOSTID() {
        return HOSTID;
    }

    public void setHOSTID(String hOSTID) {
        HOSTID = hOSTID;
    }

    public String getPELUNID() {
        return PELUNID;
    }

    public void setPELUNID(String pELUNID) {
        PELUNID = pELUNID;
    }

    public String getVVOLID() {
        return VVOLID;
    }

    public void setVVOLID(String vVOLID) {
        VVOLID = vVOLID;
    }

    public String getVVOLSECONDARYID() {
        return VVOLSECONDARYID;
    }

    public void setVVOLSECONDARYID(String vVOLSECONDARYID) {
        VVOLSECONDARYID = vVOLSECONDARYID;
    }

    @Override
    public String toString() {
        return "SVvolBind [HOSTID=" + HOSTID + ", PELUNID=" + PELUNID
                + ", VVOLID=" + VVOLID + ", VVOLSECONDARYID=" + VVOLSECONDARYID
                + ", BINDTYPE=" + BINDTYPE + "]";
    }


}
