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

package org.opensds.vasa.vasa;

import java.util.List;
import java.util.Map;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;
import org.opensds.vasa.vasa20.device.array.qos.QosCreateResBean;
import org.opensds.vasa.vasa20.device.array.snapshot.SnapshotCreateResBean;

import com.vmware.vim.vasa.v20.StorageFault;

public interface VasaArrayService {

    /**
     * @param vvolType
     * @param @param   profile
     * @param @param   profileId
     * @param @param   profileName
     * @param @param   thinThick
     * @param @param   generationId
     * @return void
     * @throws SDKException
     * @throws
     * @Title: createStorageProfile
     * @Description: TODO(创建storageProfile)
     */
    public void createStorageProfile(StoragePolicy storagePolicy, String policyId, String profileId, String profileName, String lunId, long generationId, String vvolId, String vvolType) throws StorageFault;


    /**
     * @param @param name
     * @param @param description
     * @param @param sizeInGB
     * @param @param sizeInMB
     * @param @param volumeType
     * @param @param vmName
     * @param @param parentId 需要创建卷的存储池id
     * @param @param allocType  0：Thick LUN  1：Thin LUN
     * @param @param dataTransferPolicy    数据迁移设置eg: no relocation
     * @return void    返回类型
     * @throws SDKException
     * @throws
     * @Title: createLun
     * @Description: TODO(创建LUN)
     */
    public SDKResult<LunCreateResBean> createLun(String name, String description, int sizeInGB, long sizeInMB, String vmName, String parentId, String allocType, String dataTransferPolicy, int ioProperty) throws StorageFault;


    public SDKErrorCode deleteLun(String lunId) throws StorageFault, SDKException;

    /**
     * @param @param  luns
     * @param @return
     * @param @throws StorageFault
     * @return List<LunCreateResBean>
     * @throws SDKException
     * @throws
     * @Title: queryLunInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public List<LunCreateResBean> queryLunsInfo(List<String> luns) throws StorageFault, SDKException;

    public SDKResult<LunCreateResBean> queryLunInfo(String lunId) throws StorageFault, SDKException;


    /**
     * @param @return
     * @param @throws StorageFault
     * @return ResBean
     * @throws SDKException 查询阵列所有pool信息
     * @throws
     * @Title: queryPoolsInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public List<S2DStoragePool> queryPoolsInfo() throws StorageFault, SDKException;

    /**
     * @param @param  qosId
     * @param @return
     * @return ResBean
     * @throws StorageFault
     * @throws SDKException
     * @throws
     * @Title: getQosInfo
     * @Description: TODO(获取Qosx信息)
     */
    public SDKResult<QosCreateResBean> getQosInfo(String qosId) throws SDKException, StorageFault;


    /**
     * @param @param  qosId
     * @param @param  lunId
     * @param @return
     * @return ResBean
     * @throws SDKException
     * @throws StorageFault
     * @throws
     * @Title: addLunToQos
     * @Description: TODO(将LUN与QOS绑定)
     */
    public void addLunToQos(String qosId, String lunId) throws StorageFault, SDKException;


    /**
     * @param @param  qosId
     * @param @param  lunId
     * @param @return
     * @return ResBean
     * @throws SDKException
     * @throws StorageFault
     * @throws
     * @Title: delLunToQos
     * @Description: TODO(LunId与Qos解绑)
     */
    public void delLunToQos(String qosId, String lunId) throws StorageFault, SDKException;


    /**
     * @param task
     * @param @param  name
     * @param @param  description
     * @param @param  sizeInGB
     * @param @param  sizeInMB
     * @param @param  volumeType
     * @param @param  volumeId
     * @param @param  vmName
     * @param @return
     * @return ResBean
     * @throws SDKException
     * @throws StorageFault
     * @throws
     * @Title: createVolumeFromSrcVolume
     * @Description: TODO(从volume克隆volume)
     */
    public SDKResult<LunCreateResBean> createVolumeFromSrcVolume(String name, String description, String parentId, int sizeInGB, long sizeInMB, String lunId, String vmName, String createVvolId, Map<String, String> taskProperties, String allocType, String dataTransferPolicy, int ioProperty) throws StorageFault, SDKException;


    public String createLuncopyAndStart(String arrayId, String description, String lunId, String createLunId) throws StorageFault, SDKException;


    /**
     * @param @param  name
     * @param @param  description
     * @param @param  sizeInGB
     * @param @param  sizeInMB
     * @param @param  qosId
     * @param @param  snapshotId
     * @param @param  vmName
     * @param @return
     * @return ResBean
     * @throws
     * @Title: createVolumeFromSrcSnapshot
     * @Description: TODO(从快照克隆volume)
     */
    public ResBean fastCloneVolumeFromSnapshotVvol(String name, String description, String parentId, int sizeInGB, long sizeInMB,
                                                   String qosId, String snapshotId, String vmName);

    public ResBean fastCloneVolumeFromVvol(String name, String description, String parentId, int sizeInGB, long sizeInMB,
                                           String qosId, String snapshotId, String vmName);

    public SDKResult<SnapshotCreateResBean> createSnapshotFromSourceVolume(String parentId, String name, String desc) throws SDKException, StorageFault;

    public SDKResult<SnapshotCreateResBean> fastCloneFromSourceVolume(String parentId, String name, String desc, String createVvolId) throws SDKException, StorageFault;

    public SDKErrorCode activeVvolLunSnapshot(String snapShotId) throws SDKException, StorageFault;

    public SDKErrorCode delVvolLunSnapshot(String snapShotId) throws SDKException, StorageFault;

    public SDKResult<SnapshotCreateResBean> querySnapshotInfo(String snapShotId) throws SDKException, StorageFault;

    public SDKErrorCode expandLun(String rawId, long sizeInMb) throws StorageFault, SDKException;

    public SDKErrorCode updateLun(String rawId, int ioProperty, String smartTier) throws StorageFault, SDKException;
}
