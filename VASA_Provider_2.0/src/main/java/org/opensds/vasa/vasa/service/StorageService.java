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

package org.opensds.vasa.vasa.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DFileSystemInfo;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;
import org.opensds.vasa.vasa.VasaDeviceManager;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.xsd.BaseStorageEntity;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;
import com.vmware.vim.vasa.v20.data.xsd.VasaAssociationObject;

public class StorageService implements VasaDeviceManager {
    // 单例
    private static StorageService instance;

    // 日志
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(StorageService.class);

    private DiscoverService discoverService = DiscoverService.getInstance();

    private StorageService() {

    }

    /**
     * 单例
     *
     * @return DeviceManagerImpl [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static synchronized StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
        }
        return instance;
    }

    /**
     * 方法 ： queryArrays
     *
     * @param arrayId 方法参数：arrayId
     * @return StorageArray[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public List<DArray> queryArrays(UsageContext usageContext, String[] arrayId)
            throws InvalidArgument, StorageFault {
        VASAUtil.checkIsArrayIdValid(arrayId);
        if (usageContext.getHostInitiator() == null
                || usageContext.getHostInitiator().size() == 0) {
            //VASA 2.0非vvol认证, Pos056: Verify Empty Context returns no data from the VP
            LOGGER.warn("host initiator null or empty.");
            return new ArrayList<DArray>(0);
        }

        LOGGER.info("Start run queryArrays function.Request array id:" + VASAUtil.convertArrayToStr(arrayId));
        List<DArray> results = discoverService.queryStorageArrays();

        // 如果ID参数为空，则返回所有的arrays
        if (Util.isEmpty(arrayId)) {
            LOGGER.debug("Queried array Id is empty.retuen array size is " + results.size());
            return results;
        }

        List<DArray> returnArrays = new ArrayList<DArray>(0);
        for (String id : arrayId) {
            for (DArray dArray : results) {
                if (id.equals(dArray.getUniqueIdentifier())) {
                    returnArrays.add(dArray);
                }
            }
        }

        if (returnArrays.size() == 0) {
            LOGGER.error("The return Arrays size is " + returnArrays.size());
            throw FaultUtil.invalidArgument("arrayId is not exist :" + arrayId.toString());
        }

        LOGGER.info("Query array success. size is " + returnArrays.size());
        // debug info print here
        for (DArray storageArray : returnArrays) {
            LOGGER.info("uniqueIdentifier:" + storageArray.getUniqueIdentifier() + " StorageArrayName:"
                    + storageArray.getArrayName() + " Firmware:"
                    + storageArray.getFirmware() + " modelID:" + storageArray.getModelId() + " vendor:"
                    + storageArray.getVendorId() + " alternateName:"
                    + VASAUtil.convertArrayToStr(storageArray.getAlternateName()) + " supportBlock:"
                    + VASAUtil.convertArrayToStr(storageArray.getSupportedBlock()) + " supportFileSystem:"
                    + VASAUtil.convertArrayToStr(storageArray.getSupportedFileSystem()) + " supportProfile:"
                    + VASAUtil.convertArrayToStr(storageArray.getSupportedProfile()));
        }
        return returnArrays;
    }

    /**
     * 方法 ： init
     */
    @Override
    public void init() {

    }

    /**
     * 存储设备实体比较器
     *
     * @author V1R0
     * @version [版本号V001R00C00, 2011-12-14]
     */
    private static class BaseStorageEntityComparator implements Comparator<Object>, Serializable {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        /**
         * 方法 ： compare
         *
         * @param o1 方法参数：o1
         * @param o2 方法参数：o2
         * @return int 返回结果
         */
        public int compare(Object o1, Object o2) {
            BaseStorageEntity bse1 = (BaseStorageEntity) o1;
            BaseStorageEntity bse2 = (BaseStorageEntity) o2;
            return bse1.getUniqueIdentifier().compareTo(bse2.getUniqueIdentifier());
        }
    }

    /**
     * 方法 ： getNumberOfEntities
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param entityType       方法参数：entityType
     * @param usageContext     方法参数：usageContext
     * @return int 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public int getNumberOfEntities(String[] hostInitiatorIds, String entityType, UsageContext usageContext)
            throws InvalidArgument, StorageFault {
        LOGGER.info("getNumberOfEntities called, and entityType is:" + entityType);
        if (!validEntity(entityType)) {
            LOGGER.error("InvalidArgument/invalid entityType:" + entityType);
            throw FaultUtil.invalidArgument("invalid entityType:" + entityType);
        }

        try {
            int count = 0;
            if (entityType.equals(EntityTypeEnum.STORAGE_FILE_SYSTEM.value())) {
                count = discoverService.queryStorageFileSystems(usageContext.getMountPoint()).size();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_ARRAY.value())) {
                count = discoverService.queryUniqueIdentifiersForStorageArray().size();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_PROCESSOR.value())) {
                count = discoverService.queryUniqueIdentifiersForStorageProcessor().size();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_PORT.value())) {
                HostInitiatorInfo[] hostInitiatorInfos = ListUtil.list2ArrayHostInit(usageContext.getHostInitiator());
                count = discoverService.queryUniqueIdentifiersForStoragePort(hostInitiatorInfos).size();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_LUN.value())) {
                count = discoverService.queryStorageLuns(hostInitiatorIds).size();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_CAPABILITY.value())) {
                count = discoverService.queryUniqueIdentifiersForStorageCapability().size();
            } else {
                // this should not be possible due to earlier validEntity()
                // check
                throw FaultUtil.storageFault("Unknown entity : " + entityType);
            }

            LOGGER.info("getNumberOfEntities entityType is:" + entityType + " count is:" + count);
            return count;
        } catch (StorageFault sf) {
            LOGGER.error("getNumberOfEntities ::", sf);
            throw sf;
        }

    }

    /**
     * 方法 ： queryUniqueIdentifiersForEntity
     *
     * @param entityType   方法参数：entityType
     * @param usageContext 方法参数：usageContext
     * @return String[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public List<String> queryUniqueIdentifiersForEntity(String entityType, UsageContext usageContext)
            throws InvalidArgument, StorageFault {
        LOGGER.info("queryUniqueIdentifiersForEntity called. entityType:" + entityType);
        if (!entityType.equals(EntityTypeEnum.STORAGE_ARRAY.value())
                && !entityType.equals(EntityTypeEnum.STORAGE_PROCESSOR.value())
                && !entityType.equals(EntityTypeEnum.STORAGE_PORT.value())
                && !entityType.equals(EntityTypeEnum.STORAGE_CAPABILITY.value())) {
            LOGGER.error("InvalidArgument/invalid entityType:" + entityType);
            throw FaultUtil.invalidArgument("invalid entityType:" + entityType);
        }

        try {
            List<String> uuidList = null;
            if (entityType.equals(EntityTypeEnum.STORAGE_ARRAY.value())) {
                uuidList = discoverService.queryUniqueIdentifiersForStorageArray();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_PROCESSOR.value())) {
                uuidList = discoverService.queryUniqueIdentifiersForStorageProcessor();
            } else if (entityType.equals(EntityTypeEnum.STORAGE_PORT.value())) {
                HostInitiatorInfo[] hostInitiatorInfos = ListUtil.list2ArrayHostInit(usageContext.getHostInitiator());
                uuidList = discoverService.queryUniqueIdentifiersForStoragePort(hostInitiatorInfos);
            } else if (entityType.equals(EntityTypeEnum.STORAGE_CAPABILITY.value())) {
                uuidList = discoverService.queryUniqueIdentifiersForStorageCapability();
            } else {
                // this should not be possible due to earlier validEntity()
                // check
                LOGGER.error("StorageFault/Unknown entityType : " + entityType);
                throw FaultUtil.storageFault("Unknown entityType : " + entityType);
            }
            LOGGER.info("queryUniqueIdentifiersForEntity return size is " + uuidList.size());
            LOGGER.info("queryUniqueIdentifiersForEntity returned uuids :" + VASAUtil.convertArrayToStr(uuidList));

            return uuidList;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForEntity ::", sf);
            throw sf;
        }

    }

    /**
     * 方法 ： queryUniqueIdentifiersForLuns
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param arrayId          方法参数：arrayId
     * @return String[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws NotFound        异常：NotFound
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public List<String> queryUniqueIdentifiersForLuns(UsageContext uc, String[] hostInitiatorIds, String arrayId) throws InvalidArgument,
            NotFound, StorageFault, NotImplemented {
        LOGGER.info("queryUniqueIdentifiersForLuns called.request arrayID is:" + arrayId
                + " request hostinitiator ids are :" + VASAUtil.convertArrayToStr(hostInitiatorIds));
        if (null == arrayId || "".equals(arrayId)) {
            LOGGER.error("InvalidArgument/queryUniqueIdentifiersForLuns:: NULL or emputy not allowed as parameter");
            throw FaultUtil.invalidArgument();
        }

        try {
            List<String> uniqueIdentifers = discoverService.queryUniqueIdentifiersForLuns(uc, arrayId, hostInitiatorIds);
            LOGGER.info("queryUniqueIdentifiersForLuns returned uuids is "
                    + VASAUtil.convertArrayToStr(uniqueIdentifers));
            return uniqueIdentifers;
        } catch (NotFound nf) {
            LOGGER.error("NotFound", nf);
            throw nf;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForLuns sf:", sf);
            throw sf;
        }

    }

    /**
     * 方法 ： queryStorageProcessors
     *
     * @param requestedProcessorIds 方法参数：requestedProcessorIds
     * @return SDKResult<List < DProcessor>> 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public List<DProcessor> queryStorageProcessors(String[] requestedProcessorIds)
            throws InvalidArgument, StorageFault {
        try {
            LOGGER.info("queryStorageProcessors called.request processorId size is "
                    + (Util.isEmpty(requestedProcessorIds) ? "0" : requestedProcessorIds.length));
            LOGGER.info("queryStorageProcessors request processorIds are:"
                    + VASAUtil.convertArrayToStr(requestedProcessorIds));

            List<DProcessor> dProcessors = discoverService.getStorageProcessorByIds(requestedProcessorIds);
            printQueryStorageProcessors(dProcessors.toArray(new DProcessor[dProcessors.size()]));

            return dProcessors;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryStorageProcessors ::", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageProcessors ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * <打印查询的Processor信息>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printQueryStorageProcessors(DProcessor[] returnValues) {

        LOGGER.info("print queryStorageProcessors response begin-------------------");
        for (DProcessor processor : returnValues) {
            LOGGER.info("queryStorageProcessors response,processor id is:" + processor.getUniqueIdentifier()
                    + ", spIdentifier are:" + VASAUtil.convertArrayToStr(processor.getSpIdentifier()));
        }

        LOGGER.info("print queryStorageProcessors response end-------------------");
    }

    /**
     * 方法 ： queryStoragePorts
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param portIds          方法参数：portIds
     * @param usageContext     方法参数：usageContext
     * @return StoragePort[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public List<DPort> queryStoragePorts(String[] hostInitiatorIds, String[] portIds, UsageContext usageContext)
            throws InvalidArgument, StorageFault, NotImplemented {
        try {
            LOGGER.info("queryStoragePorts called. request port Id size is "
                    + (Util.isEmpty(portIds) ? "0" : portIds.length));
            LOGGER.info("queryStoragePorts request portIds are:" + VASAUtil.convertArrayToStr(portIds));
            LOGGER.info("queryStoragePorts request hostInitiatorIds are : " + VASAUtil.convertArrayToStr(hostInitiatorIds));

            HostInitiatorInfo[] hostInitiatorInfos = ListUtil.list2ArrayHostInit(usageContext.getHostInitiator());
            List<DPort> results = discoverService.queryStoragePortByPortIds(portIds, hostInitiatorInfos);
            printResponseQueryStoragePorts(results);

            return results;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryStoragePorts ::", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStoragePorts ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * <功能详细描述>
     *
     * @param storagePorts [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryStoragePorts(List<DPort> storagePorts) {

        LOGGER.info("queryStoragePorts response port Id size is " + storagePorts.size());
        LOGGER.info("print queryStoragePorts response begin------------------");
        for (DPort storagePort : storagePorts) {
            LOGGER.info("storagePort uuID is:" + storagePort.getUniqueIdentifier() + ", iscsiIdentifier is:"
                    + storagePort.getIscsiIdentifier() + ", portWWN is:" + storagePort.getPortWwn() + ", nodeWWN is:"
                    + storagePort.getNodeWwn() + ", portType is:" + storagePort.getPortType() + ", alternateName are:"
                    + VASAUtil.convertArrayToStr(storagePort.getAlternateName()));
        }
        LOGGER.info("print queryStoragePorts response end------------------");
    }

    /**
     * 方法 ： queryStorageLuns
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param requestedLunIds  方法参数：requestedLunIds
     * @return StorageLun[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public List<DLun> queryStorageLuns(String[] hostInitiatorIds, String[] requestedLunIds) throws InvalidArgument,
            StorageFault {
        try {
            printRequestStorageLunInfo(requestedLunIds);
            LOGGER.info("queryStorageLuns request hostInitiatorIds are : " + VASAUtil.convertArrayToStr(hostInitiatorIds));
            List<DLun> dLuns = discoverService.queryStorageLuns(hostInitiatorIds, requestedLunIds);
            printReturndStorageLuns(dLuns.toArray(new DLun[dLuns.size()]));

            return dLuns;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryStorageLuns ::", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageLuns ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * queryStorageLun
     *
     * @param hostInitiatorIds 启动器id
     * @return StorageLun[]
     * @throws InvalidArgument if has error
     * @throws StorageFault    if has error
     */
    public List<DLun> queryStorageLuns(String[] hostInitiatorIds) throws InvalidArgument, StorageFault {
        try {
            LOGGER.info("queryStorageLuns request hostInitiatorIds are : " + VASAUtil.convertArrayToStr(hostInitiatorIds));
            List<DLun> returnValues = discoverService.queryStorageLuns(hostInitiatorIds);
            printReturndStorageLuns(returnValues.toArray(new DLun[returnValues.size()]));
            return returnValues;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageLuns ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * 打印返回的StorageLun信息
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printReturndStorageLuns(DLun[] returnValues) {

        LOGGER.info("queryStorageLuns - response storageLun size is " + returnValues.length);
        LOGGER.info("print queryStorageLuns begin-------------------");
        for (DLun storageLun : returnValues) {
            LOGGER.info("Response Storage Lun info: " + "exsLunIdentifer is:" + storageLun.getEsxLunIdentifier()
                    + ", displayName is:" + storageLun.getDisplayName() + ", thinProvisoning is:"
                    + storageLun.isThinProvisioned() + ", thinProvisioningStatus is:"
                    + storageLun.getThinProvisioningStatus() + ", capacityInMB is:" + storageLun.getCapacityInMB()
                    + ", usedSpaceInMB is:" + storageLun.getUsedSpaceInMB() + ", alternateIdentifier are:"
                    + VASAUtil.convertArrayToStr(storageLun.getAlternateIdentifier()) + ", drsManagementPermitted is:"
                    + storageLun.isDrsManagementPermitted() + ", uniqueIdentifer is:" + storageLun.getUniqueIdentifier());
        }
        LOGGER.info("print queryStorageLuns end-------------------");
    }

    private void printReturndStorageFileSystems(List<DFileSystem> returnValues) {

        LOGGER.info("queryStorageFileSystems - response storageFileSystem size is " + returnValues.size());
        LOGGER.info("print queryStorageFileSystems begin-------------------");
        for (DFileSystem dFileSystem : returnValues) {
            for (DFileSystemInfo fileSystemInfo : dFileSystem.getFileSystemInfo()) {
                LOGGER.info("Response Storage FileSystem info: " + "FileSystem is:"
                        + dFileSystem.getFileSystem() + ", FileSystemVersion is:"
                        + dFileSystem.getFileSystemVersion() + ", ThinProvisioningStatus is:"
                        + dFileSystem.getThinProvisioningStatus() + ", UniqueIdentifier is:"
                        + dFileSystem.getUniqueIdentifier() + ", FileServerName are:"
                        + fileSystemInfo.getFileServerName() + ", FileSystemPath is:" + fileSystemInfo.getFileSystemPath()
                        + ", IpAddress is:" + fileSystemInfo.getIpAddress());
            }
        }
        LOGGER.info("print queryStorageFileSystems end-------------------");
    }

    /**
     * <打印请求的LUN信息>
     *
     * @param requestedLunIds [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printRequestStorageLunInfo(String[] requestedLunIds) {
        LOGGER.info("queryStorageLuns called - requestLunIds size is:"
                + (null == requestedLunIds ? "0" : requestedLunIds.length) + ",requestLunIds are:"
                + VASAUtil.convertArrayToStr(requestedLunIds));
    }

    /**
     * 方法 ： queryStorageCapabilities
     *
     * @param capabilityId 方法参数：capabilityId
     * @return StorageCapability[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public List<DStorageCapability> queryStorageCapabilities(String[] capabilityId)
            throws InvalidArgument, StorageFault, NotImplemented {
        LOGGER.info("queryStorageCapabilities called. response ids size is "
                + (Util.isEmpty(capabilityId) ? "0" : capabilityId.length) + ", request capabilityid are:"
                + VASAUtil.convertArrayToStr(capabilityId));
        List<DStorageCapability> returnValues = discoverService.getStorageCapabilityByIds(capabilityId);
        printResponseQueryStorageCapabilities(returnValues);
        return returnValues;
    }

    /**
     * <打印返回的capability>
     *
     * @param returnValues [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryStorageCapabilities(List<DStorageCapability> returnValues) {

        LOGGER.info("print queryStorageCapabilities response begin-----------------");
        LOGGER.info("queryStorageCapabilities,response size is:" + returnValues.size());
        for (DStorageCapability capablity : returnValues) {
            LOGGER.info("response capablity id is:" + capablity.getUniqueIdentifier() + ", capablity name is:"
                    + capablity.getCapabilityName() + ", capablity detail is:" + capablity.getCapabilityDetail());
        }
        LOGGER.info("print queryStorageCapabilities response end-----------------");
    }

    /**
     * 方法 ： queryAssociatedProcessorsForArray
     *
     * @param arrayIds 方法参数：arrayIds
     * @return VasaAssociationObject[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public VasaAssociationObject[] queryAssociatedProcessorsForArray(String[] arrayIds)
            throws InvalidArgument, StorageFault, NotImplemented {
        try {
            LOGGER.info("queryAssociatedProcessorsForArray called. arrayId:" + VASAUtil.convertArrayToStr(arrayIds));

            VasaAssociationObject[] associationObjects = discoverService.queryAssociatedProcessorsForArrayByArrayIds(arrayIds);
            printQueryAssociatedProcessorsForArray(associationObjects);
            return associationObjects;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryAssociatedProcessorsForArray sf:", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryAssociatedProcessorsForArray e:", e);
            throw FaultUtil.storageFault();
        }
    }

    /**
     * <日志打印>
     *
     * @param associationObjects [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printQueryAssociatedProcessorsForArray(VasaAssociationObject[] associationObjects) {
        LOGGER.info("queryAssociatedProcessorsForArray response size is " + associationObjects.length);
        LOGGER.info("print queryAssociatedProcessorsForArray response begin-------------------");
        for (VasaAssociationObject object : associationObjects) {
            LOGGER.info("queryAssociatedProcessorsForArray response entityId is :"
                    + object.getEntityId().get(0).getUniqueIdentifier());
            for (BaseStorageEntity entity : object.getAssociatedId()) {
                LOGGER.info("queryAssociatedProcessorsForArray response entityId ["
                        + object.getEntityId().get(0).getUniqueIdentifier() + "]'s associatedId is:"
                        + entity.getUniqueIdentifier());
            }
        }
        LOGGER.info("print queryAssociatedProcessorsForArray response end-------------------");
    }

    /**
     * 方法 ： queryAssociatedPortsForProcessor
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param processorIds     方法参数：processorIds
     * @return VasaAssociationObject[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public VasaAssociationObject[] queryAssociatedPortsForProcessor(String[] hostInitiatorIds, String[] processorIds)
            throws InvalidArgument, StorageFault, NotImplemented {
        try {
            LOGGER.info("queryAssociatedPortsForProcessor called.request processorId size is "
                    + (Util.isEmpty(processorIds) ? "0" : processorIds.length));
            LOGGER.info("queryAssociatedPortsForProcessor request processorIds are :"
                    + VASAUtil.convertArrayToStr(processorIds));
            LOGGER.info("queryAssociatedPortsForProcessor request hostInitiatorIds are :"
                    + VASAUtil.convertArrayToStr(hostInitiatorIds));

            HostInitiatorInfo[] hostInitiatorInfos = ListUtil.list2ArrayHostInit(SecureConnectionService.getInstance()
                    .getUsageContext().getHostInitiator());
            LOGGER.info("queryAssociatedPortsForProcessor request hostInitiatorInfos size is :"
                    + (Util.isEmpty(hostInitiatorInfos) ? "0" : hostInitiatorInfos.length));
            List<VasaAssociationObject> associationObjects = discoverService.queryAssociatedPortsForProcessor(
                    hostInitiatorInfos, processorIds);
            LOGGER.info("queryAssociatedPortsForProcessor response size is " + associationObjects.size());
            printResponseAssociation(associationObjects);

            return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
        } catch (StorageFault sf) {
            LOGGER.error("queryAssociatedPortsForProcessor sf:", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("queryAssociatedPortsForProcessor e:", e);
            throw FaultUtil.storageFault();
        }
    }

    /**
     * 打印回复的关联
     *
     * @param associationObjects [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseAssociation(List<VasaAssociationObject> associationObjects) {

        LOGGER.info("print queryAssociatedPortsForProcessor response begin----------------------");
        for (VasaAssociationObject ass : associationObjects) {
            LOGGER.info("queryAssociatedPortsForProcessor resonpse entityID is :"
                    + ass.getEntityId().get(0).getUniqueIdentifier());
            for (BaseStorageEntity entity : ass.getAssociatedId()) {
                LOGGER.info("queryAssociatedPortsForProcessor response entityID ["
                        + ass.getEntityId().get(0).getUniqueIdentifier() + "]'s associatedID is:"
                        + entity.getUniqueIdentifier());
            }
        }
        LOGGER.info("print queryAssociatedPortsForProcessor response end----------------------");
    }

    /**
     * 打印回复的关联
     *
     * @param associationObjects [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseAssociation(VasaAssociationObject[] associationObjects) {

        LOGGER.info("print queryAssociatedLunsForPort response begin-------------------");
        for (VasaAssociationObject ass : associationObjects) {
            LOGGER.info("queryAssociatedLunsForPort response entityID is :"
                    + ass.getEntityId().get(0).getUniqueIdentifier());
            for (BaseStorageEntity entity : ass.getAssociatedId()) {
                LOGGER.info("queryAssociatedLunsForPort entityID ["
                        + ass.getEntityId().get(0).getUniqueIdentifier() + "]'s associatedID is :"
                        + entity.getUniqueIdentifier());
            }
        }

        LOGGER.info("print queryAssociatedLunsForPort response end-------------------");
    }

    /**
     * 方法 ： queryAssociatedLunsForPort
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param portIds          方法参数：portIds
     * @param usageContext     方法参数：usageContext
     * @return VasaAssociationObject[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public VasaAssociationObject[] queryAssociatedLunsForPort(String[] hostInitiatorIds, String[] portIds,
                                                              UsageContext usageContext) throws InvalidArgument, StorageFault {
        try {
            LOGGER.debug("queryAssociatedLunsForPort called. request portId size is "
                    + (Util.isEmpty(portIds) ? "0" : portIds.length));
            LOGGER.info("queryAssociatedLunsForPort request portIds are:" + VASAUtil.convertArrayToStr(portIds));
            LOGGER.info("queryAssociatedLunsForPort request hostInitiatorIds are :"
                    + VASAUtil.convertArrayToStr(hostInitiatorIds));

            VasaAssociationObject[] returnValues = discoverService.queryAssociatedLunsForPort(
                    ListUtil.list2ArrayHostInit(usageContext.getHostInitiator()), portIds);
            LOGGER.debug("queryAssociatedLunsForPort response size is " + returnValues.length);
            printResponseAssociation(returnValues);
            return returnValues;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryAssociatedLunsForPor sf:", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryAssociatedLunsForPor e:", e);
            throw FaultUtil.storageFault();
        }
    }

    /**
     * 方法 ： queryAssociatedCapabilityForLun
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param lunIds           方法参数：lunIds
     * @return VasaAssociationObject[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public VasaAssociationObject[] queryAssociatedCapabilityForLun(String[] hostInitiatorIds, String[] lunIds)
            throws InvalidArgument, StorageFault {
        try {
            LOGGER
                    .info("queryAssociatedCapabilityForLun request lunid size is :"
                            + (null == lunIds ? "0" : lunIds.length) + ", request lunIds are:"
                            + VASAUtil.convertArrayToStr(lunIds));
            LOGGER.info("queryAssociatedCapabilityForLun request hostInitiatorIds are :"
                    + VASAUtil.convertArrayToStr(hostInitiatorIds));

            VasaAssociationObject[] returnValue = discoverService.queryAssociatedCapabilityForLunByLunIds(
                    hostInitiatorIds, lunIds);
            printResponseQueryAssociatedCapabilityForLun(returnValue);

            return returnValue;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryAssociatedCapabilityForLun ::", e);
            throw FaultUtil.storageFault();
        }
    }

    /**
     * <功能详细描述>
     *
     * @param returnValue [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void printResponseQueryAssociatedCapabilityForLun(VasaAssociationObject[] returnValue) {

        LOGGER.info("print queryAssociatedCapabilityForLun response begin-----------------------");
        LOGGER.info("queryAssociatedCapabilityForLun response size is:" + returnValue.length);
        for (VasaAssociationObject obj : returnValue) {
            LOGGER.info("Capabliity for LUN Entity id is :" + obj.getEntityId().get(0).getUniqueIdentifier()
                    + ", and it's Association id is:" + obj.getAssociatedId().get(0).getUniqueIdentifier());
        }
        LOGGER.info("print queryAssociatedCapabilityForLun response end-----------------------");
    }

    private void printResponseQueryAssociatedCapabilityForFileSystem(VasaAssociationObject[] returnValue) {

        LOGGER.info("print queryAssociatedCapabilityForFileSystem response begin-----------------------");
        LOGGER.info("queryAssociatedCapabilityForFileSystem response size is:" + returnValue.length);
        for (VasaAssociationObject obj : returnValue) {
            LOGGER.info("Capabliity for FileSystem Entity id is :"
                    + obj.getEntityId().get(0).getUniqueIdentifier() + ", and it's Association id is:"
                    + obj.getAssociatedId().get(0).getUniqueIdentifier());
        }
        LOGGER.info("print queryAssociatedCapabilityForFileSystem response end-----------------------");
    }

    /**
     * Implement the queryDRSMigrationCapabilityForPerformance() API
     * <p>
     * notes: entityType can be "StorageLun" or "StorageFileSystem"
     *
     * @param hii         方法参数：hii
     * @param mii         方法参数：mii
     * @param srcUniqueId 方法参数：srcUniqueId
     * @param dstUniqueId 方法参数：dstUniqueId
     * @param entityType  方法参数：entityType
     * @return boolean 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws NotFound        异常：NotFound
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public boolean queryDRSMigrationCapabilityForPerformance(String[] hii, List<MountInfo> mii, String srcUniqueId,
                                                             String dstUniqueId, String entityType) throws InvalidArgument, NotFound, StorageFault {
        try {
            LOGGER.debug("queryDRSMigrationCapabilityForPerformance called. srcID is :" + srcUniqueId
                    + ", dstID is :" + dstUniqueId + ", entityType is :" + entityType);
            if (entityType == null) {
                LOGGER.error("InvalidArgument/entityType is null.");
                throw FaultUtil.invalidArgument("entityType is null.");
            }

            if (Util.isEmpty(hii)) {
                LOGGER.error("InvalidArgument/hostInitiators is empty.");
                throw FaultUtil.invalidArgument("hostInitiators is empty.");
            }

            if (!entityType.equals(EntityTypeEnum.STORAGE_LUN.value())
                    && !entityType.equals(EntityTypeEnum.STORAGE_FILE_SYSTEM.value())) {
                LOGGER.error("InvalidArgument/queryDRSMigrationCapabilityForPerformance - Invalid entityType : " + entityType);
                throw FaultUtil.invalidArgument("queryDRSMigrationCapabilityForPerformance - Invalid entityType : " + entityType);
            }

            // 目前不支持StorageFileSystem
            // if
            // (entityType.equals(EntityTypeEnum.STORAGE_FILE_SYSTEM.value()))
            // {
            // throw FaultUtil
            // .notImplemented("queryDRSMigrationCapabilityForPerformance - not implemented entityType : "
            // + entityType);
            // }

            if (srcUniqueId.equals(dstUniqueId)) {
                LOGGER.error("InvalidArgument/src and dst is equals.");
                throw FaultUtil.invalidArgument("src and dst is equals.");
            }

            LOGGER.info("queryDRSMigrationCapabilityForPerformance request hostInitiatorIds are :"
                    + VASAUtil.convertArrayToStr(hii));
            LOGGER.info("queryDRSMigrationCapabilityForPerformance request mountInfos are :" +
                    VASAUtil.convertArrayToStr(VASAUtil.convertMountInfos(mii)));

            boolean result = discoverService.queryDRSMigrationCapabilityForPerformance(srcUniqueId, dstUniqueId, hii,
                    mii);

            LOGGER.info("queryDRSMigrationCapabilityForPerformance. result is " + result);
            return result;

        } catch (InvalidArgument ia) {
            LOGGER.error("InvalidArgument/queryDRSMigrationCapabilityForPerformance - queryAssociates() threw InvalidArgument");
            throw ia;
        } catch (NotFound nf) {
            LOGGER.error("NotFound/queryDRSMigrationCapabilityForPerformance - queryAssociates() threw NotFond");
            throw nf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryDRSMigrationCapabilityForPerformance: ", e);
            throw FaultUtil.storageFault();
        }
    }

    public List<DFileSystem> queryStorageFileSystems(List<MountInfo> infos) throws InvalidArgument, StorageFault {
        try {
            LOGGER.info("queryStorageFileSystems request mountInfos are :" +
                    VASAUtil.convertArrayToStr(VASAUtil.convertMountInfos(infos)));
            List<DFileSystem> returnValues = discoverService.queryStorageFileSystems(infos);
            printReturndStorageFileSystems(returnValues);
            return returnValues;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageFileSystems ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * 方法 ： queryStorageFileSystems
     *
     * @param ucMountInfo 方法参数：ucMountInfo
     * @param targetFsId  方法参数：fsId
     * @return SDKResult<List < DFileSystem>> 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    @Override
    public List<DFileSystem> queryStorageFileSystems(List<MountInfo> infos, String[] fsIds) throws InvalidArgument,
            StorageFault {
        try {
            LOGGER.info("queryStorageFileSystems request mountInfos are :" +
                    VASAUtil.convertArrayToStr(VASAUtil.convertMountInfos(infos)));
            List<DFileSystem> returnValues = discoverService.queryStorageFileSystems(infos, fsIds);
            printReturndStorageFileSystems(returnValues);
            return returnValues;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/queryStorageFileSystems ::", sf);
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageFileSystems ::", e);
            throw FaultUtil.storageFault("error : ", e);
        }
    }

    /**
     * 方法 ： queryAssociatedCapabilityForFileSystem
     *
     * @param ucMountInfo 方法参数：ucMountInfo
     * @param fsId        方法参数：fsId
     * @return VasaAssociationObject[] 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     * @throws NotImplemented  异常：NotImplemented
     */
    @Override
    public VasaAssociationObject[] queryAssociatedCapabilityForFileSystem(List<MountInfo> infos, String[] fsId)
            throws InvalidArgument, StorageFault, NotImplemented {
        try {
            LOGGER.info("queryAssociatedCapabilityForFileSystem request mountInfos are :" +
                    VASAUtil.convertArrayToStr(VASAUtil.convertMountInfos(infos)));
            VasaAssociationObject[] returnValue = discoverService.queryAssociatedCapabilityForFileSystem(infos, fsId);
            printResponseQueryAssociatedCapabilityForFileSystem(returnValue);

            return returnValue;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryAssociatedCapabilityForLun ::", e);
            throw FaultUtil.storageFault();
        }
    }

    private boolean validEntity(String entity) {
        boolean result = entity.equals(EntityTypeEnum.STORAGE_ARRAY.value())
                || entity.equals(EntityTypeEnum.STORAGE_PROCESSOR.value())
                || entity.equals(EntityTypeEnum.STORAGE_PORT.value());
        if (result || entity.equals(EntityTypeEnum.STORAGE_LUN.value())
                || entity.equals(EntityTypeEnum.STORAGE_CAPABILITY.value())
                || entity.equals(EntityTypeEnum.STORAGE_FILE_SYSTEM.value())) {
            return true;
        }
        return false;
    }

    /**
     * 获取主机启动器的ID
     *
     * @param hii 方法参数：hii
     * @return List<String> [返回类型说明]
     * @throws StorageFault [参数说明]
     * @see [类、类#方法、类#成员]
     */
    public List<String> getHostInitiatorIds(HostInitiatorInfo hii) throws StorageFault {
        List<String> hostInitiators = new ArrayList<String>(0);
        if (hii.getIscsiIdentifier() != null && hii.getIscsiIdentifier().trim().length() > 0) {
            getHostInitiatorIdsForIscsi(hii, hostInitiators);
        }

        if ((hii.getPortWwn() != null && hii.getPortWwn().trim().length() > 0)
                && (hii.getNodeWwn() != null && hii.getNodeWwn().trim().length() > 0)) {
            getHostInitiatorIdsForFC(hii, hostInitiators);
        }

        if (hostInitiators.size() < 1) {
            // unknown
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }

        return hostInitiators;
    }

    /**
     * <降低复杂度>
     *
     * @param hii
     * @param hostInitiators
     * @return void [返回类型说明]
     * @throws StorageFault [参数说明]
     * @throws throws       [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void getHostInitiatorIdsForFC(HostInitiatorInfo hii, List<String> hostInitiators) throws StorageFault {
        if (hii.getIscsiIdentifier() != null && hii.getIscsiIdentifier().trim().length() > 0) {
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }
        hostInitiators.add(hii.getPortWwn());
        hostInitiators.add(hii.getNodeWwn());
    }

    /**
     * <降低复杂度>
     *
     * @param hii
     * @param hostInitiators
     * @return void [返回类型说明]
     * @throws StorageFault [参数说明]
     * @throws throws       [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void getHostInitiatorIdsForIscsi(HostInitiatorInfo hii, List<String> hostInitiators) throws StorageFault {
        if ((hii.getPortWwn() != null && hii.getPortWwn().trim().length() > 0)
                || (hii.getNodeWwn() != null && hii.getNodeWwn().trim().length() > 0)) {
            throw FaultUtil.storageFault("Invalid Host Initiator format");
        }

        hostInitiators.add(hii.getIscsiIdentifier());
    }

    /**
     * 获取主机启动器的ID
     *
     * @param uc                 方法参数：uc
     * @param hostInitiatorIndex 方法参数：hostInitiatorIndex
     * @return String[] 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public String[] getHostInitiatorIdsFromUsageContext(UsageContext uc, int hostInitiatorIndex) throws StorageFault {
        try {
            List<String> hostIdList = new ArrayList<String>(0);
            HostInitiatorInfo[] hii = ListUtil.list2ArrayHostInit(uc.getHostInitiator());

            if (hii.length == 0) {
                throw FaultUtil.storageFault("No Host Initiators");
            }

            if (hostInitiatorIndex == -1) {
                for (int i = 0; i < hii.length; i++) {
                    hostIdList.addAll(getHostInitiatorIds(hii[i]));
                }

            } else if (hostInitiatorIndex < hii.length) {
                hostIdList.addAll(getHostInitiatorIds(hii[hostInitiatorIndex]));
            }

            return hostIdList.toArray(new String[hostIdList.size()]);

        } catch (StorageFault is) {
            LOGGER.error("StorageFault/getHostInitiatorIdsFromUsageContext ::", is);
            throw is;
        } catch (Exception e) {
            LOGGER.error("StorageFault/getHostInitiatorIdsFromUsageContext unknown exception. Converting to StorageFault.", e);
            throw FaultUtil.storageFault("could not get host initiator ids: ", e);
        }
    }

    @Override
    public String[] queryUniqueIdentifiersForFileSystems(List<MountInfo> mountInfos, String arrayId)
            throws InvalidArgument, NotFound, StorageFault, NotImplemented {
        LOGGER.info("queryUniqueIdentifiersForFileSystems called.request arrayID is:" + arrayId);
        try {
            LOGGER.info("queryUniqueIdentifiersForFileSystems request mountInfos are :" +
                    VASAUtil.convertArrayToStr(VASAUtil.convertMountInfos(mountInfos)));
            String[] uniqueIdentifers = discoverService.queryUniqueIdentifiersForFileSystems(arrayId, mountInfos);
            LOGGER.info("queryUniqueIdentifiersForFileSystems returned uuids is "
                    + VASAUtil.convertArrayToStr(uniqueIdentifers));
            return uniqueIdentifers;
        } catch (StorageFault is) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForFileSystems ::", is);
            throw is;
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForFileSystems unknown exception. Converting to StorageFault.", e);
            throw FaultUtil.storageFault("could not get unique identifiers for filesystems : ", e);
        }
    }
}
