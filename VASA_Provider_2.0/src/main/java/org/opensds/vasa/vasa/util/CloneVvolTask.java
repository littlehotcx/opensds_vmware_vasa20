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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensds.vasa.domain.model.bean.S2DVolumeType;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;

import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;

public class CloneVvolTask extends VasaTask {
    public static String LUN_COPY_ID = "luncopyId";
    public static String LUN_COPY_SNAPSHOT_ID = "lunCopySnapshotId";

    public CloneVvolTask(TaskInfo taskInfo) {
        super(taskInfo);
    }

    private long sizeInMB;

    private S2DVolumeType volType;

    private StorageProfile storageProfile;

    private List<NameValuePair> metadata;

    private String containerId;

    private String vvolType;

    private Boolean removeThin;

    private StorageProfile insertedProfile;

    private String thinValue;

    private NVirtualVolume vvol;

    private boolean isRunning = false; //该task是否正在被执行getTaskUpdate，解决第一次getTaskUpdate超时导致第二次getTaskUpdate重入问题

    private boolean myCanceled = false;

    //目标卷是否创建成功
    private boolean volumeAvaliable;

    //luncopy任务是否已经执行
    private boolean luncopyAvaliable;

    private Map<String, String> extraProperties = new HashMap<String, String>();

    @Override
    protected void refreshTask() {
        //如果在任务的List中，认为克隆任务就是运行状态
    }

    public void setMyCancel(boolean cancel) {
        this.myCanceled = cancel;
    }

    public boolean getMyCancel() {
        return myCanceled;
    }

    public long getSizeInMB() {
        return sizeInMB;
    }

    public void setSizeInMB(long sizeInMB) {
        this.sizeInMB = sizeInMB;
    }

    public S2DVolumeType getVolType() {
        return volType;
    }

    public void setVolType(S2DVolumeType volType) {
        this.volType = volType;
    }

    public StorageProfile getStorageProfile() {
        return storageProfile;
    }

    public void setStorageProfile(StorageProfile storageProfile) {
        this.storageProfile = storageProfile;
    }

    public List<NameValuePair> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<NameValuePair> metadata) {
        this.metadata = metadata;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getVvolType() {
        return vvolType;
    }

    public void setVvolType(String vvolType) {
        this.vvolType = vvolType;
    }

    public Boolean getRemoveThin() {
        return removeThin;
    }

    public void setRemoveThin(Boolean removeThin) {
        this.removeThin = removeThin;
    }

    public StorageProfile getInsertedProfile() {
        return insertedProfile;
    }

    public void setInsertedProfile(StorageProfile insertedProfile) {
        this.insertedProfile = insertedProfile;
    }

    public String getThinValue() {
        return thinValue;
    }

    public void setThinValue(String thinValue) {
        this.thinValue = thinValue;
    }

    public NVirtualVolume getVvol() {
        return vvol;
    }

    public void setVvol(NVirtualVolume vvol) {
        this.vvol = vvol;
    }

    public boolean isVolumeAvaliable() {
        return volumeAvaliable;
    }

    public void setVolumeAvaliable(boolean volumeAvaliable) {
        this.volumeAvaliable = volumeAvaliable;
    }

    public boolean isLuncopyAvaliable() {
        return luncopyAvaliable;
    }

    public void setLuncopyAvaliable(boolean luncopyAvaliable) {
        this.luncopyAvaliable = luncopyAvaliable;
    }

    public Map<String, String> getExtraProperties() {
        return extraProperties;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

}
