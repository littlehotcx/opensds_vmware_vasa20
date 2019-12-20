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

/**
 * 用于清理线程判断lun上面是否存在lun copy增值业务
 */
public class SLunCopyBean extends SouthBaseBean {
    @JsonProperty
    private String LUNCOPYIDS;

    public String getLUNCOPYIDS() {
        return LUNCOPYIDS;
    }

    public void setLUNCOPYIDS(String lUNCOPYIDS) {
        LUNCOPYIDS = lUNCOPYIDS;
    }

}
