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

package org.opensds.vasa.domain.model.bean;

public class S2DLun extends DomainBaseBean {
    private String CAPABILITY;

    private String CAPACITY;

    private String ALLOCTYPE;

    private String ALLOCCAPACITY;

    private String WWN;

    private String DRS_ENABLE;

    private String CAPACITYALARMLEVEL;

    private String THINCAPACITYUSAGE;

    private String SECTORSIZE;

    private String USAGETYPE;

    private String IPV4ADDR;

    private String IPV6ADDR;

    private String ROLE;

    private String VSTOREID;

    public String getIPV4ADDR() {
        return IPV4ADDR;
    }

    public void setIPV4ADDR(String IPV4ADDR) {
        this.IPV4ADDR = IPV4ADDR;
    }

    public String getIPV6ADDR() {
        return IPV6ADDR;
    }

    public void setIPV6ADDR(String IPV6ADDR) {
        this.IPV6ADDR = IPV6ADDR;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public String getVSTOREID() {
        return VSTOREID;
    }

    public void setVSTOREID(String VSTOREID) {
        this.VSTOREID = VSTOREID;
    }

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
