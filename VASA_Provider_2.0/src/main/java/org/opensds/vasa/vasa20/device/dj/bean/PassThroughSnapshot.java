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

public class PassThroughSnapshot extends SouthBaseBean {
    @JsonProperty
    private String ROLLBACKSTARTTIME;

    @JsonProperty
    private String ROLLBACKENDTIME;

    @JsonProperty
    private String ROLLBACKSPEED;

    @JsonProperty
    private String ROLLBACKRATE;

    @JsonProperty
    private String USERCAPACITY;

    @JsonProperty
    private String CONSUMEDCAPACITY;

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

}
