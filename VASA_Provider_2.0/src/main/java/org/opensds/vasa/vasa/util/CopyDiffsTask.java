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

package org.opensds.vasa.vasa.util;

import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.S2DLunCopy;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.vasa.common.EnumDefine;

import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskStateEnum;

public class CopyDiffsTask extends VasaTask {
    public CopyDiffsTask(TaskInfo taskInfo) {
        super(taskInfo);
    }

    @Override
    protected void refreshTask() {
        try {
            String rawId = taskId.split(":")[2];
            SDKResult<S2DLunCopy> queryResult = new VVolModel().getLuncopyById(taskId.split(":")[1], rawId);
            if (0 != queryResult.getErrCode()) {
                LOGGER.error("getLuncopyById error. arrayId:" + arrayId + ", rawId:" + rawId + ", errCode:" + queryResult.getErrCode() +
                        ", description:" + queryResult.getDescription());
                return;
            }

            int rs = Integer.valueOf(queryResult.getResult().getRUNNINGSTATUS());
            int hs = Integer.valueOf(queryResult.getResult().getHEALTHSTATUS());


            if (hs != EnumDefine.HEALTH_STATUS_E.NORMAL.getValue()) {
                this.taskState = TaskStateEnum.ERROR.value();
                this.error = "healthStatus:" + hs;
                return;
            }

            if (rs == EnumDefine.RUNNING_STATUS_E.COPYING.getValue() || rs == EnumDefine.RUNNING_STATUS_E.QUEUING.getValue()) {
                this.taskState = TaskStateEnum.RUNNING.value();
                this.progress = Integer.valueOf(queryResult.getResult().getCOPYPROGRESS());
            } else if (rs == EnumDefine.RUNNING_STATUS_E.COMPLETED.getValue()) {
                this.taskState = TaskStateEnum.SUCCESS.value();
                this.progress = 100;
            } else if (rs == EnumDefine.RUNNING_STATUS_E.STOP.getValue()) {
                this.cancelled = true;
                this.taskState = TaskStateEnum.SUCCESS.value();
            } else {
                this.taskState = TaskStateEnum.ERROR.value();
                this.error = "runningStatus:" + rs;
            }
        } catch (SDKException e) {
            LOGGER.error("getLuncopyById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        }
    }
}
