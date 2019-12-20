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

package org.opensds.vasa.vasa20.device;

import org.opensds.platform.common.bean.commu.RestReqMessage;

public class VASARestReqMessage extends RestReqMessage {
    private boolean isPaging = Boolean.FALSE;

    private boolean hasRange = Boolean.FALSE;

    private int pageSize = 100;

    private long requestStartTime = System.currentTimeMillis();

    private String arrayId;

    private Integer switchControlCount = null;

    public boolean isPaging() {
        return isPaging;
    }

    public void setPaging(boolean isPaging) {
        this.isPaging = isPaging;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasRange() {
        return hasRange;
    }

    public void setHasRange(boolean hasRange) {
        this.hasRange = hasRange;
    }

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public Integer getSwitchControlCount() {
        return switchControlCount;
    }

    public void setSwitchControlCount(Integer switchControlCount) {
        this.switchControlCount = switchControlCount;
    }

}
