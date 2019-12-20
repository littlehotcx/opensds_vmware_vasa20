
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

import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidSession;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;
import com.vmware.vim.vasa.v20.data.xsd.VasaAssociationObject;

/**
 * 设备管理器
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public interface VasaDeviceManager {
    /**
     * 初始化
     */
    void init();

    /**
     * 获取实体对象的数量
     *
     * @param hostInitiatorIds 参数
     * @param entityType       参数
     * @param usageContext     参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    int getNumberOfEntities(String[] hostInitiatorIds, String entityType, UsageContext usageContext)
            throws InvalidArgument, StorageFault;

    /**
     * 查询对象的唯一标识
     *
     * @param entityType   参数
     * @param usageContext 参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    List<String> queryUniqueIdentifiersForEntity(String entityType, UsageContext usageContext) throws InvalidArgument,
            StorageFault;

    /**
     * 查询LUN的唯一标识
     *
     * @param hostInitiatorIds 参数
     * @param param            参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws NotFound        异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    List<String> queryUniqueIdentifiersForLuns(UsageContext uc, String[] hostInitiatorIds, String param) throws InvalidArgument, NotFound,
            StorageFault, NotImplemented;

    /**
     * 查询主机启动器的ID
     *
     * @param uc                 参数
     * @param hostInitiatorIndex 参数
     * @return 结果
     * @throws InvalidSession 异常
     * @throws StorageFault   异常
     */
    String[] getHostInitiatorIdsFromUsageContext(UsageContext uc, int hostInitiatorIndex) throws InvalidSession,
            StorageFault;

    /**
     * 查询阵列
     *
     * @param arrayId 参数
     * @return StorageArray
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    List<DArray> queryArrays(UsageContext usageContext, String[] arrayId)
            throws InvalidArgument, StorageFault;

    /**
     * 获取StorageProcessors
     *
     * @param processorId 参数
     * @return StoragePort
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    List<DProcessor> queryStorageProcessors(String[] processorId) throws InvalidArgument,
            StorageFault;

    /**
     * 查询端口
     *
     * @param hostInitiatorIds 参数
     * @param processorId      参数
     * @param usageContext     参数
     * @return StoragePort
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    List<DPort> queryStoragePorts(String[] hostInitiatorIds, String[] processorId, UsageContext usageContext)
            throws InvalidArgument, StorageFault, NotImplemented;

    /**
     * 查询LUNs
     *
     * @param hostInitiatorIds 参数
     * @param lunId            参数
     * @return StorageLun
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    List<DLun> queryStorageLuns(String[] hostInitiatorIds, String[] lunId) throws InvalidArgument, StorageFault;

    /**
     * FC/iSCSI/FCOE association
     *
     * @param arrayId 参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    VasaAssociationObject[] queryAssociatedProcessorsForArray(String[] arrayId)
            throws InvalidArgument, StorageFault, NotImplemented;

    /**
     * 方法
     *
     * @param hostInitiatorIds 参数
     * @param processorId      参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    VasaAssociationObject[] queryAssociatedPortsForProcessor(String[] hostInitiatorIds, String[] processorId)
            throws InvalidArgument, StorageFault, NotImplemented;

    /**
     * 方法
     *
     * @param hostInitiatorIds 参数
     * @param portId           参数
     * @param usageContext     参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    VasaAssociationObject[] queryAssociatedLunsForPort(String[] hostInitiatorIds, String[] portId,
                                                       UsageContext usageContext) throws InvalidArgument, StorageFault;

    /**
     * 查询LUN容量
     *
     * @param hostInitiatorIds 参数
     * @param lunId            参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    VasaAssociationObject[] queryAssociatedCapabilityForLun(String[] hostInitiatorIds, String[] lunId)
            throws InvalidArgument, StorageFault;

    /**
     * 查询远程复制的唯一标识
     *
     * @param hostInitiatorIds 参数
     * @param mi 参数
     * @param arrayId 参数
     * @param entityType 参数
     * @throws InvalidArgument 异常
     * @throws NotFound 异常
     * @throws StorageFault 异常
     * @throws NotImplemented 异常
     * @return 结果
     *//*
    String[] queryUniqueIdentifiersForRemoteReplication(String[] hostInitiatorIds, List<MountInfo> mi, String arrayId,
        String entityType) throws InvalidArgument, NotFound, StorageFault, NotImplemented;

    */

    /**
     * 查询文件系统的唯一标识
     *
     * @param ucMountInfo 参数
     * @param fsId        参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    String[] queryUniqueIdentifiersForFileSystems(List<MountInfo> mountInfos, String param) throws InvalidArgument,
            NotFound, StorageFault, NotImplemented;

    /**
     * 查询文件系统
     *
     * @param ucMountInfo 参数
     * @param fileSystem  参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     */
    List<DFileSystem> queryStorageFileSystems(List<MountInfo> infos, String[] fsIds) throws InvalidArgument,
            StorageFault;

    /**
     * 查询文件系统的容量
     *
     * @param ucMountInfo 参数
     * @param fsId        参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    VasaAssociationObject[] queryAssociatedCapabilityForFileSystem(List<MountInfo> infos, String[] fsId) throws InvalidArgument, StorageFault,
            NotImplemented;

    /**
     * 查询Storage的容量
     *
     * @param capabilityId 参数
     * @return 返回结果
     * @throws InvalidArgument 异常
     * @throws StorageFault    异常
     * @throws NotImplemented  异常
     */
    List<DStorageCapability> queryStorageCapabilities(String[] capabilityId)
            throws InvalidArgument, StorageFault, NotImplemented;

    /**
     * 查询DRS
     *
     * @param hii         参数
     * @param mii         参数
     * @param srcUniqueId 参数
     * @param dstUniqueId 参数
     * @param entityType  参数
     * @return 结果
     * @throws InvalidArgument 异常
     * @throws NotFound        异常
     * @throws StorageFault    异常
     */
    boolean queryDRSMigrationCapabilityForPerformance(String[] hii, List<MountInfo> mii, String srcUniqueId,
                                                      String dstUniqueId, String entityType) throws InvalidArgument, NotFound, StorageFault;

}
