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

public class SHostLink extends SouthBaseBean {
    @JsonProperty
    private String CTRL_ID;

    @JsonProperty
    private String INITIATOR_ID;

    @JsonProperty
    private String INITIATOR_NODE_WWN;

    @JsonProperty
    private String INITIATOR_PORT_WWN;

    @JsonProperty
    private String INITIATOR_TYPE;

    @JsonProperty
    private String TARGET_ID;

    @JsonProperty
    private String TARGET_NODE_WWN;

    @JsonProperty
    private String TARGET_PORT_WWN;

    @JsonProperty
    private String TARGET_TYPE;

    public String getCTRL_ID() {
        return CTRL_ID;
    }

    public void setCTRL_ID(String cTRL_ID) {
        CTRL_ID = cTRL_ID;
    }

    public String getINITIATOR_ID() {
        return INITIATOR_ID;
    }

    public void setINITIATOR_ID(String iNITIATOR_ID) {
        INITIATOR_ID = iNITIATOR_ID;
    }

    public String getINITIATOR_NODE_WWN() {
        return INITIATOR_NODE_WWN;
    }

    public void setINITIATOR_NODE_WWN(String iNITIATOR_NODE_WWN) {
        INITIATOR_NODE_WWN = iNITIATOR_NODE_WWN;
    }

    public String getINITIATOR_PORT_WWN() {
        return INITIATOR_PORT_WWN;
    }

    public void setINITIATOR_PORT_WWN(String iNITIATOR_PORT_WWN) {
        INITIATOR_PORT_WWN = iNITIATOR_PORT_WWN;
    }

    public String getINITIATOR_TYPE() {
        return INITIATOR_TYPE;
    }

    public void setINITIATOR_TYPE(String iNITIATOR_TYPE) {
        INITIATOR_TYPE = iNITIATOR_TYPE;
    }

    public String getTARGET_ID() {
        return TARGET_ID;
    }

    public void setTARGET_ID(String tARGET_ID) {
        TARGET_ID = tARGET_ID;
    }

    public String getTARGET_NODE_WWN() {
        return TARGET_NODE_WWN;
    }

    public void setTARGET_NODE_WWN(String tARGET_NODE_WWN) {
        TARGET_NODE_WWN = tARGET_NODE_WWN;
    }

    public String getTARGET_PORT_WWN() {
        return TARGET_PORT_WWN;
    }

    public void setTARGET_PORT_WWN(String tARGET_PORT_WWN) {
        TARGET_PORT_WWN = tARGET_PORT_WWN;
    }

    public String getTARGET_TYPE() {
        return TARGET_TYPE;
    }

    public void setTARGET_TYPE(String tARGET_TYPE) {
        TARGET_TYPE = tARGET_TYPE;
    }

}
