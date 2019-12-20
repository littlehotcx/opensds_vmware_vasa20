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

public class SLunCopy extends SouthBaseBean {
    @JsonProperty
    private String COPYPROGRESS;

    @JsonProperty
    private String COPYSPEED;

    @JsonProperty
    private String COPYSTARTTIME;

    @JsonProperty
    private String COPYSTOPTIME;

    @JsonProperty
    private String LUNCOPYTYPE;

    @JsonProperty
    private String SOURCELUN;

    @JsonProperty
    private String TARGETLUN;

    @JsonProperty
    private String BASELUN;

    public String getCOPYPROGRESS() {
        return COPYPROGRESS;
    }

    public void setCOPYPROGRESS(String cOPYPROGRESS) {
        COPYPROGRESS = cOPYPROGRESS;
    }

    public String getCOPYSPEED() {
        return COPYSPEED;
    }

    public void setCOPYSPEED(String cOPYSPEED) {
        COPYSPEED = cOPYSPEED;
    }

    public String getCOPYSTARTTIME() {
        return COPYSTARTTIME;
    }

    public void setCOPYSTARTTIME(String cOPYSTARTTIME) {
        COPYSTARTTIME = cOPYSTARTTIME;
    }

    public String getCOPYSTOPTIME() {
        return COPYSTOPTIME;
    }

    public void setCOPYSTOPTIME(String cOPYSTOPTIME) {
        COPYSTOPTIME = cOPYSTOPTIME;
    }

    public String getLUNCOPYTYPE() {
        return LUNCOPYTYPE;
    }

    public void setLUNCOPYTYPE(String lUNCOPYTYPE) {
        LUNCOPYTYPE = lUNCOPYTYPE;
    }

    public String getSOURCELUN() {
        return SOURCELUN;
    }

    public void setSOURCELUN(String sOURCELUN) {
        SOURCELUN = sOURCELUN;
    }

    public String getTARGETLUN() {
        return TARGETLUN;
    }

    public void setTARGETLUN(String tARGETLUN) {
        TARGETLUN = tARGETLUN;
    }

    public String getBASELUN() {
        return BASELUN;
    }

    public void setBASELUN(String bASELUN) {
        BASELUN = bASELUN;
    }

}
