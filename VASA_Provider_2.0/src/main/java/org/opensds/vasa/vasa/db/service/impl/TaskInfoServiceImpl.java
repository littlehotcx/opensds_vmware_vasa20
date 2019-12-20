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

package org.opensds.vasa.vasa.db.service.impl;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.TaskInfoDao;
import org.opensds.vasa.vasa.db.model.NTaskInfo;
import org.opensds.vasa.vasa.db.service.TaskInfoService;

import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;

public class TaskInfoServiceImpl extends BaseServiceImpl<NTaskInfo> implements TaskInfoService {

    public TaskInfoServiceImpl(BaseDao<NTaskInfo> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void saveTaskInfo(TaskInfo taskInfo,
                             Map<String, String> extProperties) {
        // TODO Auto-generated method stub
        NTaskInfo info = createNTaskInfo(taskInfo);
        if (null != extProperties) {
            String json = new Gson().toJson(extProperties);
            info.setExtraProperties(json);
        }
        super.save(info);
    }

    private NTaskInfo createNTaskInfo(TaskInfo taskInfo) {
        NTaskInfo info = new NTaskInfo();
        info.setTaskId(taskInfo.getTaskId());
        info.setArrayid(taskInfo.getArrayId());
        info.setName(taskInfo.getName());
        info.setCancelab(taskInfo.isCancelable());
        info.setCancelled(taskInfo.isCancelled());
        if (null != taskInfo.getError()) {
            info.setErrMsg(taskInfo.getError().toString());
        }
        info.setProgress(taskInfo.getProgress());
        info.setProgressUpdateAvailable(taskInfo.isProgressUpdateAvailable());
        if (null != taskInfo.getResult()) {
            info.setResult(taskInfo.getResult().toString());
        }
        info.setTaskState(taskInfo.getTaskState());
        if (null != taskInfo.getStartTime()) {
            info.setStartTime(taskInfo.getStartTime().getTime());
        }
        return info;
    }

    @Override
    public NTaskInfo getTaskInfoByTaskId(String taskId) {
        // TODO Auto-generated method stub
        NTaskInfo taskInfo = new NTaskInfo();
        taskInfo.setTaskId(taskId);
        return super.getDataByKey(taskInfo);
    }

    @Override
    public void updateTaskInfoByTaskId(TaskInfo taskInfo, Map<String, String> extProperties) {
        // TODO Auto-generated method stub
        NTaskInfo createNTaskInfo = createNTaskInfo(taskInfo);
        if (null != extProperties && extProperties.size() != 0) {
            createNTaskInfo.setExtraProperties(new Gson().toJson(extProperties));
        }
        super.updateData(createNTaskInfo);
    }

    public List<NTaskInfo> getRunningTaskByArrayId(String arrayId) {
        return ((TaskInfoDao) baseDao).getRunningTaskByArrayId(arrayId);
    }
}
