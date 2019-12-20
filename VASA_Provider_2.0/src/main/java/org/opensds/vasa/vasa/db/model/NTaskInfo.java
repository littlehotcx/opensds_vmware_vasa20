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

package org.opensds.vasa.vasa.db.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;

public class NTaskInfo extends BaseData {

    public static String LUN_COPY_ID = "luncopyId";
    public static String DET_RAW_ID = "dstRawId";
    public static String SRC_RAW_ID = "srcRawId";
    public static String STORAGE_PROFILE = "storageProfile";
    public static String STORAGE_NO_QOS = "storageNoQos";

    private String taskId;
    private String name;
    private String arrayid;
    private String errMsg;
    private String result;
    private String taskState;
    private String extraProperties;
    private Boolean cancelab;
    private Boolean cancelled;
    private Boolean progressUpdateAvailable;
    private Integer progress;
    private Date startTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArrayid() {
        return arrayid;
    }

    public void setArrayid(String arrayid) {
        this.arrayid = arrayid;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getExtraProperties() {
        return extraProperties;
    }

    public void setExtraProperties(String extraProperties) {
        this.extraProperties = extraProperties;
    }

    public Boolean getCancelab() {
        return cancelab;
    }

    public void setCancelab(Boolean cancelab) {
        this.cancelab = cancelab;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Boolean getProgressUpdateAvailable() {
        return progressUpdateAvailable;
    }

    public void setProgressUpdateAvailable(Boolean progressUpdateAvailable) {
        this.progressUpdateAvailable = progressUpdateAvailable;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "NTaskInfo [taskId=" + taskId + ", name=" + name + ", arrayid="
                + arrayid + ", errMsg=" + errMsg + ", result=" + result
                + ", taskState=" + taskState + ", extraProperties="
                + extraProperties + ", cancelab=" + cancelab + ", cancelled="
                + cancelled + ", progressUpdateAvailable="
                + progressUpdateAvailable + ", progress=" + progress
                + ", startTime=" + startTime + "]";
    }

    public TaskInfo getTaskInfo() {
        TaskInfo info = new TaskInfo();
        info.setArrayId(getArrayid());
        info.setTaskId(getTaskId());
        info.setName(getName());
        info.setCancelable(getCancelab());
        info.setCancelled(getCancelled());
        info.setError(getErrMsg());
        info.setProgress(getProgress());
        info.setProgressUpdateAvailable(getProgressUpdateAvailable());
        info.setResult(getResult());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartTime());
        info.setStartTime(calendar);
        info.setTaskState(getTaskState());
        return info;
    }

    public String getExtProperties(String key) {
        String result = null;
        if (null != extraProperties) {
            Map<String, String> propMap = new Gson().fromJson(extraProperties, new TypeToken<HashMap<String, String>>() {
            }.getType());
            result = propMap.get(key);
        }
        return result;
    }

}
