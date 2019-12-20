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

public class SSystemDSTConfig extends SouthBaseBean {
    @JsonProperty
    private String CMO_SYS_DST_CONF_MODE;

    @JsonProperty
    private String CMO_SYS_DST_CONF_TIME_ZONE_NAME;

    @JsonProperty
    private String CMO_SYS_DST_CONF_ADJUST_TIME;

    @JsonProperty
    private String CMO_SYS_DST_CONF_DATE_TIME_BEGIN;

    @JsonProperty
    private String CMO_SYS_DST_CONF_DATE_TIME_END;

    public String getCMO_SYS_DST_CONF_MODE() {
        return CMO_SYS_DST_CONF_MODE;
    }

    public void setCMO_SYS_DST_CONF_MODE(String cMO_SYS_DST_CONF_MODE) {
        CMO_SYS_DST_CONF_MODE = cMO_SYS_DST_CONF_MODE;
    }

    public String getCMO_SYS_DST_CONF_TIME_ZONE_NAME() {
        return CMO_SYS_DST_CONF_TIME_ZONE_NAME;
    }

    public void setCMO_SYS_DST_CONF_TIME_ZONE_NAME(
            String cMO_SYS_DST_CONF_TIME_ZONE_NAME) {
        CMO_SYS_DST_CONF_TIME_ZONE_NAME = cMO_SYS_DST_CONF_TIME_ZONE_NAME;
    }

    public String getCMO_SYS_DST_CONF_ADJUST_TIME() {
        return CMO_SYS_DST_CONF_ADJUST_TIME;
    }

    public void setCMO_SYS_DST_CONF_ADJUST_TIME(String cMO_SYS_DST_CONF_ADJUST_TIME) {
        CMO_SYS_DST_CONF_ADJUST_TIME = cMO_SYS_DST_CONF_ADJUST_TIME;
    }

    public String getCMO_SYS_DST_CONF_DATE_TIME_BEGIN() {
        return CMO_SYS_DST_CONF_DATE_TIME_BEGIN;
    }

    public void setCMO_SYS_DST_CONF_DATE_TIME_BEGIN(
            String cMO_SYS_DST_CONF_DATE_TIME_BEGIN) {
        CMO_SYS_DST_CONF_DATE_TIME_BEGIN = cMO_SYS_DST_CONF_DATE_TIME_BEGIN;
    }

    public String getCMO_SYS_DST_CONF_DATE_TIME_END() {
        return CMO_SYS_DST_CONF_DATE_TIME_END;
    }

    public void setCMO_SYS_DST_CONF_DATE_TIME_END(
            String cMO_SYS_DST_CONF_DATE_TIME_END) {
        CMO_SYS_DST_CONF_DATE_TIME_END = cMO_SYS_DST_CONF_DATE_TIME_END;
    }


}
