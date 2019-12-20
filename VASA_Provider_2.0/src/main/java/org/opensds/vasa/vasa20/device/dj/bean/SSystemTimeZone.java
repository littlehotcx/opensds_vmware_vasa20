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

public class SSystemTimeZone extends SouthBaseBean {
    @JsonProperty
    private String CMO_SYS_TIME_ZONE;

    @JsonProperty
    private String CMO_SYS_TIME_ZONE_NAME;

    @JsonProperty
    private String CMO_SYS_TIME_ZONE_NAME_STYLE;

    @JsonProperty
    private String CMO_SYS_TIME_ZONE_USE_DST;

    public String getCMO_SYS_TIME_ZONE() {
        return CMO_SYS_TIME_ZONE;
    }

    public void setCMO_SYS_TIME_ZONE(String cMO_SYS_TIME_ZONE) {
        CMO_SYS_TIME_ZONE = cMO_SYS_TIME_ZONE;
    }

    public String getCMO_SYS_TIME_ZONE_NAME() {
        return CMO_SYS_TIME_ZONE_NAME;
    }

    public void setCMO_SYS_TIME_ZONE_NAME(String cMO_SYS_TIME_ZONE_NAME) {
        CMO_SYS_TIME_ZONE_NAME = cMO_SYS_TIME_ZONE_NAME;
    }

    public String getCMO_SYS_TIME_ZONE_NAME_STYLE() {
        return CMO_SYS_TIME_ZONE_NAME_STYLE;
    }

    public void setCMO_SYS_TIME_ZONE_NAME_STYLE(String cMO_SYS_TIME_ZONE_NAME_STYLE) {
        CMO_SYS_TIME_ZONE_NAME_STYLE = cMO_SYS_TIME_ZONE_NAME_STYLE;
    }

    public String getCMO_SYS_TIME_ZONE_USE_DST() {
        return CMO_SYS_TIME_ZONE_USE_DST;
    }

    public void setCMO_SYS_TIME_ZONE_USE_DST(String cMO_SYS_TIME_ZONE_USE_DST) {
        CMO_SYS_TIME_ZONE_USE_DST = cMO_SYS_TIME_ZONE_USE_DST;
    }

}
