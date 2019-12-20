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


public class SLun extends SouthBaseBean {
    @JsonProperty
    private String CAPABILITY;

    @JsonProperty
    private String CAPACITY;

    @JsonProperty
    private String ALLOCTYPE;

    @JsonProperty
    private String ALLOCCAPACITY;

    @JsonProperty
    private String WWN;

    @JsonProperty
    private String DRS_ENABLE;

    @JsonProperty
    private String CAPACITYALARMLEVEL;

    @JsonProperty
    private String THINCAPACITYUSAGE;

    @JsonProperty
    private String SECTORSIZE;

    @JsonProperty
    private String USAGETYPE;

    public String getUSAGETYPE() {
        return USAGETYPE;
    }

    public void setUSAGETYPE(String uSAGETYPE) {
        USAGETYPE = uSAGETYPE;
    }

    public String getSECTORSIZE() {
        return SECTORSIZE;
    }

    public void setSECTORSIZE(String sECTORSIZE) {
        SECTORSIZE = sECTORSIZE;
    }

    public String getCAPABILITY() {
        return CAPABILITY;
    }

    public void setCAPABILITY(String cAPABILITY) {
        CAPABILITY = cAPABILITY;
    }

    public String getCAPACITY() {
        return CAPACITY;
    }

    public void setCAPACITY(String cAPACITY) {
        CAPACITY = cAPACITY;
    }

    public String getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(String aLLOCTYPE) {
        ALLOCTYPE = aLLOCTYPE;
    }

    public String getALLOCCAPACITY() {
        return ALLOCCAPACITY;
    }

    public void setALLOCCAPACITY(String aLLOCCAPACITY) {
        ALLOCCAPACITY = aLLOCCAPACITY;
    }

    public String getWWN() {
        return WWN;
    }

    public void setWWN(String wWN) {
        WWN = wWN;
    }

    public String getDRS_ENABLE() {
        return DRS_ENABLE;
    }

    public void setDRS_ENABLE(String dRS_ENABLE) {
        DRS_ENABLE = dRS_ENABLE;
    }

    public String getCAPACITYALARMLEVEL() {
        return CAPACITYALARMLEVEL;
    }

    public void setCAPACITYALARMLEVEL(String cAPACITYALARMLEVEL) {
        CAPACITYALARMLEVEL = cAPACITYALARMLEVEL;
    }

    public String getTHINCAPACITYUSAGE() {
        return THINCAPACITYUSAGE;
    }

    public void setTHINCAPACITYUSAGE(String tHINCAPACITYUSAGE) {
        THINCAPACITYUSAGE = tHINCAPACITYUSAGE;
    }

}
