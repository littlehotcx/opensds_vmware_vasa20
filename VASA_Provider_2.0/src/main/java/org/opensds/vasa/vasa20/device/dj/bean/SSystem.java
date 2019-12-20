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

public class SSystem extends SouthBaseBean {
    @JsonProperty
    private String VASA_SUPPORT_BLOCK;

    @JsonProperty
    private String VASA_SUPPORT_FILESYSTEM;

    @JsonProperty
    private String VASA_SUPPORT_PROFILE;

    @JsonProperty
    private String VASA_ALTERNATE_NAME;

    @JsonProperty
    private String PRODUCTMODE;

    @JsonProperty
    private String PRODUCTVERSION;

    public String getVASA_SUPPORT_BLOCK() {
        return VASA_SUPPORT_BLOCK;
    }

    public void setVASA_SUPPORT_BLOCK(String vASA_SUPPORT_BLOCK) {
        VASA_SUPPORT_BLOCK = vASA_SUPPORT_BLOCK;
    }

    public String getVASA_SUPPORT_FILESYSTEM() {
        return VASA_SUPPORT_FILESYSTEM;
    }

    public void setVASA_SUPPORT_FILESYSTEM(String vASA_SUPPORT_FILESYSTEM) {
        VASA_SUPPORT_FILESYSTEM = vASA_SUPPORT_FILESYSTEM;
    }

    public String getVASA_SUPPORT_PROFILE() {
        return VASA_SUPPORT_PROFILE;
    }

    public void setVASA_SUPPORT_PROFILE(String vASA_SUPPORT_PROFILE) {
        VASA_SUPPORT_PROFILE = vASA_SUPPORT_PROFILE;
    }

    public String getVASA_ALTERNATE_NAME() {
        return VASA_ALTERNATE_NAME;
    }

    public void setVASA_ALTERNATE_NAME(String vASA_ALTERNATE_NAME) {
        VASA_ALTERNATE_NAME = vASA_ALTERNATE_NAME;
    }

    public String getPRODUCTMODE() {
        return PRODUCTMODE;
    }

    public void setPRODUCTMODE(String pRODUCTMODE) {
        PRODUCTMODE = pRODUCTMODE;
    }

    public String getPRODUCTVERSION() {
        return PRODUCTVERSION;
    }

    public void setPRODUCTVERSION(String pRODUCTVERSION) {
        PRODUCTVERSION = pRODUCTVERSION;
    }

}
