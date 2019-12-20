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

package org.opensds.vasa.vasa20.device.array.logicalPort;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-02
 */
public class LogicPortQueryResBean {

    @JsonProperty
    private String ID;

    @JsonProperty
    private String NAME;

    @JsonProperty
    private String RUNNINGSTATUS;

    @JsonProperty
    private String IPV4ADDR;

    @JsonProperty
    private String IPV6ADDR;

    @JsonProperty
    private String ROLE;

    @JsonProperty
    private String VSTOREID;

    @Override
    public String toString() {
        return "LogicPortQueryResBean{" +
                "ID='" + ID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", RUNNINGSTATUS='" + RUNNINGSTATUS + '\'' +
                ", IPV4ADDR='" + IPV4ADDR + '\'' +
                ", IPV6ADDR='" + IPV6ADDR + '\'' +
                ", ROLE='" + ROLE + '\'' +
                ", VSTOREID='" + VSTOREID + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getRUNNINGSTATUS() {
        return RUNNINGSTATUS;
    }

    public void setRUNNINGSTATUS(String RUNNINGSTATUS) {
        this.RUNNINGSTATUS = RUNNINGSTATUS;
    }

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
}
