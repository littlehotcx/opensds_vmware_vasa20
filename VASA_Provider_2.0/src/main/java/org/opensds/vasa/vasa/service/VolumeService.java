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

import java.util.ArrayList;
import java.util.List;

import org.opensds.vasa.common.MagicNumber;
import org.opensds.platform.common.utils.ListUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.vasa.vasa.db.model.NTaskInfo;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.JsonUtil;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointTypeEnum;
import com.vmware.vim.vasa.v20.IncompatibleVolume;
import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidProfile;
import com.vmware.vim.vasa.v20.NotCancellable;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.NotSupported;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.SnapshotTooMany;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.VasaProviderBusy;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.DiscreteSet;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityId;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.ConstraintInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyInstance;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.SubProfile;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchReturnStatus;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ContainerSpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.QueryConstraint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.SpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeBitmapResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeHandle;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeTypeEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeUnsharedChunksResult;
import com.vmware.vim.vasa.v20.data.xsd.EventTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;
import com.vmware.vim.vasa.v20.data.xsd.StorageContainer;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;

public class VolumeService {
    // 单例
    private static VolumeService instance;

    // 日志
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VolumeService.class);

    private DiscoverService discoverService = DiscoverService.getInstance();

    private VolumeService() {

    }

    /**
     * 单例
     *
     * @return VirtualVolumeService [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static synchronized VolumeService getInstance() {
        if (instance == null) {
            instance = new VolumeService();
        }
        return instance;
    }

    /**
     * 方法 ： queryStorageContainer
     *
     * @param containerIds 方法参数：containerIds
     * @return List<StorageContainer> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<StorageContainer> queryStorageContainer(String[] containerIds) throws StorageFault {
        LOGGER.info(
                "queryStorageContainer called. request containerIds are:" + VASAUtil.convertArrayToStr(containerIds));
        List<StorageContainer> returnValues = discoverService.queryStorageContainer(containerIds);
        printResponseQueryStorageContainer(returnValues);
        return returnValues;
    }

    /**
     * 方法 ： queryStorageContainerForArray
     *
     * @param arrayId 方法参数：arrayId
     * @return List<String> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<String> queryStorageContainerForArray(String arrayId) throws StorageFault {
        LOGGER.info("queryStorageContainerForArray called. arrrayId:" + arrayId);
        List<String> returnValues = discoverService.queryStorageContainerForArray(arrayId);
        printResponseQueryStorageContainerForArray(returnValues);
        return returnValues;
    }

    private void printResponseQueryStorageContainer(List<StorageContainer> returnValues) {
        LOGGER.info("printResponseQueryStorageContainer,response size is:" + returnValues.size());
        for (StorageContainer container : returnValues) {
            LOGGER.info("container id is:" + container.getUniqueIdentifier() + ", container name is:"
                    + container.getName() + ", container maxVvolSize is:" + container.getMaxVvolSizeMB());
        }
    }

    private void printResponseQueryStorageContainerForArray(List<String> returnValues) {
        LOGGER.info("containerIds:" + VASAUtil.convertArrayToStr(returnValues));
    }

    /**
     * 方法 ： createVirtualVolume
     *
     * @param parameters 方法参数：parameters
     * @return TaskInfo 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws NotFound        异常：NotFound
     * @throws InvalidArgument
     * @throws OutOfResource
     * @throws InvalidProfile
     */
    public TaskInfo createVirtualVolume(String containerId, String vvolType, StorageProfile storageProfile,
                                        long sizeInMB, List<NameValuePair> metadata)
            throws InvalidArgument, StorageFault, NotFound, OutOfResource, InvalidProfile {
        VASAUtil.printVcenterProfile(storageProfile);
        LOGGER.info("createVirtualVolume called. containerId:" + containerId + ", vvolType:" + vvolType + ", sizeInMB:"
                + sizeInMB + ", storageProfile:\n" + VASAUtil.convertProfile2String(storageProfile));

        LOGGER.info("createVirtualVolume: kv\n" + VASAUtil.convertNameValuePair2Str(metadata));

        if (sizeInMB <= 0 || sizeInMB > MagicNumber.INT_MAX) {
            LOGGER.error("InvalidArgument/invlid sizeInMB : " + sizeInMB);
            throw FaultUtil.invalidArgument("invlid sizeInMB : " + sizeInMB);
        }

        if (!VASAUtil.checkContainerIdValid(containerId)) {
            LOGGER.error("InvalidArgument/invalid containerId : " + containerId);
            throw FaultUtil.invalidArgument("invalid containerId : " + containerId);
        }

        /** 认证用例crtVVol.Neg007 */
        VASAUtil.checkContainerIdExist(containerId);
        // 验证policy的有效性
        VASAUtil.checkProfileVaild(storageProfile);

        TaskInfo result = new TaskInfo();
        VASAUtil.saveContainerType(containerId);
        VASAUtil.parseMetaDateForVmID(metadata);
        VASAUtil.saveNameSpace(metadata);
        VASAUtil.updateArrayIdByContainerId(containerId);
        if (VASAUtil.checkProfileNull(storageProfile)) {
            LOGGER.info("the storageProfile is empty or null, createVirtualVolume With DefaultProfile");
            result = discoverService.createVirtualVolumeWithDefaultProfile(containerId, vvolType, sizeInMB, metadata,
                    storageProfile);
        } else {
            // 校验是否包含id是null或者""的capability,解决updStorProForVVol.Neg007,
            // 应该把是否支持也校验到位。
            // 校验是否包含id是null或者""的namespace,解决updStorProForVVol.Neg008，
            // 应该把是否支持也校验到位。
            List<SubProfile> subProfiles = storageProfile.getConstraints().getSubProfiles();
            for (SubProfile subProfile : subProfiles) {
                List<CapabilityInstance> capbility = subProfile.getCapability();
                for (CapabilityInstance capabilityInstance : capbility) {
                    if (VASAUtil.checkCapabilityNull(capabilityInstance)) {
                        LOGGER.error("InvalidProfile/capability id or namespace null");
                        throw FaultUtil.invalidProfile("capability id or namespace null");
                    }
                    if (capabilityInstance.getConstraint().size() != 1
                            || capabilityInstance.getConstraint().get(0).getPropertyInstance().size() != 1) {
                        LOGGER.error("InvalidProfile/ConstraintInstance or PropertyInstance more than one.");
                        throw FaultUtil.invalidProfile("ConstraintInstance or PropertyInstance more than one.");
                    }
                }
            }

            Boolean isThinEmpty = checkCreateVirtualVolumeParameters(vvolType, storageProfile);
            if (isThinEmpty) {
                String thinValue = VASAUtil.getPreferredThinValue(vvolType);
                LOGGER.info("the thinProvision value in storageProfile is empty or null, add thin value[" + thinValue
                        + "] for vvolType[" + vvolType + "]");
                CapabilityInstance capability1 = new CapabilityInstance();
                CapabilityId capaId1 = new CapabilityId();
                capaId1.setId(VASAUtil.VMW_STD_CAPABILITY);
                capaId1.setNamespace(VASAUtil.VMW_NAMESPACE);
                ConstraintInstance constraint1 = new ConstraintInstance();
                PropertyInstance propertyInstance1 = new PropertyInstance();
                propertyInstance1.setId(VASAUtil.VMW_STD_CAPABILITY);
                DiscreteSet setValue = new DiscreteSet();
                setValue.getValues().add(thinValue);
                propertyInstance1.setValue(setValue);
                constraint1.getPropertyInstance().add(propertyInstance1);
                capability1.setCapabilityId(capaId1);
                capability1.getConstraint().add(constraint1);

                storageProfile.getConstraints().getSubProfiles().get(0).getCapability().add(capability1);
                result = discoverService.createVirtualVolumeWithStorageProfile(containerId, vvolType, storageProfile,
                        sizeInMB, metadata, true);
            } else {
                LOGGER.info("create virtual volume with storageProfile passed in");
                result = discoverService.createVirtualVolumeWithStorageProfile(containerId, vvolType, storageProfile,
                        sizeInMB, metadata, false);
            }
        }

        printResponseTaskInfo(result);
        return result;
    }

    private void printResponseTaskInfo(TaskInfo result) {
        LOGGER.info("---- print taskInfo ----\n" + "Task Id = " + result.getTaskId() + "\n" + "Task Name = "
                + result.getName() + "\n" + "Task State = " + result.getTaskState() + "\n" + "result = "
                + result.getResult() + "\n" + "Task Progress = " + result.getProgress() + "\n" + "Array Id = "
                + result.getArrayId() + "\n" + "Task error = " + result.getError() + "\n" + "Task cancelable = "
                + result.isCancelable() + "\n" + "Task cancelled = " + result.isCancelled() + "\n"
                + "Task start time = "
                + ((result.getStartTime() == null) ? null : result.getStartTime().getTimeInMillis()) + "\n"
                + "progressUpdateAvailable = " + result.isProgressUpdateAvailable());
    }

    /**
     * return true represents no constraint about thin return false represents
     * there is no conflict with vvolType and storageProfile throw
     * InvalidProfile when conflict
     *
     * @throws InvalidProfile
     */
    private Boolean checkCreateVirtualVolumeParameters(String vvolType, StorageProfile storageProfile)
            throws InvalidProfile {
        List<CapabilityInstance> capaInstances = storageProfile.getConstraints().getSubProfiles().get(0)
                .getCapability();

        String isThin = null;
        for (CapabilityInstance capaInstance : capaInstances) {
            if (capaInstance.getCapabilityId().getNamespace().equalsIgnoreCase(VASAUtil.VMW_NAMESPACE)
                    && capaInstance.getConstraint().get(0).getPropertyInstance().get(0).getId()
                    .equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
                DiscreteSet value = (DiscreteSet) capaInstance.getConstraint().get(0).getPropertyInstance().get(0)
                        .getValue();
                String thinValue = (String) value.getValues().get(0);
                if (thinValue.equalsIgnoreCase("Thin")) {
                    isThin = "true";
                    break;
                } else {
                    isThin = "false";
                    break;
                }
            }
        }

        if (null == isThin) {
            return true;
        }

        Boolean needThick = isThickNeeded(vvolType);
        if (needThick && isThin.equalsIgnoreCase("true")) {
            LOGGER.error("InvalidProfile/the vvolType:" + vvolType + " and the storageProfile conflict!");
            throw FaultUtil.invalidProfile("the vvolType:" + vvolType + " and the storageProfile conflict!");
        }

        return false;
    }

    // 这个判断是为啥，为啥如此判断
    private Boolean isThickNeeded(String vvolType) {

        if (vvolType.equalsIgnoreCase(VirtualVolumeTypeEnum.MEMORY.value())) {
            return true;
        } else {
            return false;
        }
    }

    public void setPEContext(String sessionId, UsageContext uc, List<ProtocolEndpoint> listOfHostPE) {
        try {
            printListProtocolEndpoint(listOfHostPE);
            // if(null == listOfHostPE || listOfHostPE.size() == 0)
            // {
            // return;
            // }
            List<ProtocolEndpoint> cachedPes = DataUtil.getInstance().getPEsBySessionId(sessionId);
            if (cachedPes == null || cachedPes.size() == 0) {
                LOGGER.info("no PE cached, setPEContext called firstly, sessionId:" + VASAUtil.replaceSessionId(sessionId));
                // 将带外PE uuid填入缓存PE的uuid
                // discoverService.updateInbandPE(listOfHostPE);
                // DataUtil.getInstance().setPEsForSessionId(sessionId,
                // listOfHostPE);
                // return;
            }

            String ucUUID = VASAUtil.getUcUUID(uc);

            // 只有当esxi主机订阅了事件并且不包括配置PE事件时，才不给esxi主机上报配置PE的事件
            List<String> subscribeEvent = uc.getSubscribeEvent();
            if (!ListUtils.isEmptyList(subscribeEvent)
                    && !subscribeEvent.contains(EventTypeEnum.CONFIG_PROTOCOL_ENDPOINT.value())) {
                LOGGER.warn("ucuuid : " + VASAUtil.getUcUUID(uc) + " doesn't subscribe PE config event.");
                // 将带外PE uuid填入缓存PE的uuid
                discoverService.updateInbandPE(listOfHostPE);
                DataUtil.getInstance().setPEsForSessionId(sessionId, listOfHostPE);
                return;
            }
            DataUtil.getInstance().setPEsForSessionId(sessionId, listOfHostPE);
            VASAUtil.printPeWWN(cachedPes);

            List<StorageEvent> storageEvent = new ArrayList<StorageEvent>();

            storageEvent.addAll(discoverService.generatePEAddEvent(cachedPes, listOfHostPE));
            storageEvent.addAll(discoverService.generatePEDeleteEvent(cachedPes, listOfHostPE));

            EventService.getInstance().appendPeConfigEvent(ucUUID, storageEvent);
        } catch (Exception e) {
            LOGGER.error("setPeContext fail !!" + e);
            throw e;
        }
    }

    /**
     * 方法 ： queryProtocolEndpointForArray
     *
     * @param arrayId 方法参数：arrayId
     * @return List<String> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<String> queryProtocolEndpointForArray(List<ProtocolEndpoint> listOfPE, String arrayId)
            throws StorageFault {
        LOGGER.info("queryProtocolEndpointForArray called. arrayId:" + arrayId);
        printProtocolEndpointContext(listOfPE);
        List<String> returnValues = discoverService.queryProtocolEndpointForArray(listOfPE, arrayId);
        LOGGER.info("queryProtocolEndpointForArray response:" + VASAUtil.convertArrayToStr(returnValues));
        return returnValues;
    }

    private void printProtocolEndpointContext(List<ProtocolEndpoint> listOfPE) {
        LOGGER.debug("print ProtocolEndpointContext begin-----------------");
        if (null != listOfPE && listOfPE.size() != 0) {
            for (ProtocolEndpoint pe : listOfPE) {
                LOGGER.debug("PE Lun WWN:" + pe.getInBandId().getLunId());
            }
        }

        LOGGER.debug("print ProtocolEndpointContext end-----------------");
    }

    /**
     * 方法 ： queryProtocolEndpoint
     *
     * @param arrayId  方法参数：arrayId
     * @param listOfPE 方法参数：listOfPE
     * @return List<ProtocolEndpoint> 返回结果
     * @throws StorageFault 异常：StorageFault
     * @throws NotFound     异常：NotFound
     */
    public List<ProtocolEndpoint> queryProtocolEndpoint(List<ProtocolEndpoint> listOfPE, List<String> peIds)
            throws StorageFault {
        LOGGER.info("queryProtocolEndpoint called. peIds:" + VASAUtil.convertArrayToStr(peIds));
        printProtocolEndpointContext(listOfPE);
        List<ProtocolEndpoint> returnValues = discoverService.queryProtocolEndpoint(peIds);
        printListProtocolEndpoint(returnValues);
        // if(null == peIds || peIds.size() == 0 || null == listOfPE ||
        // listOfPE.size() == 0)
        // {
        // return returnValues;
        // }
        //
        // for(String peId : peIds)
        // {
        // Boolean isFind = false;
        // for(ProtocolEndpoint pe : listOfPE)
        // {
        // if(peId.equalsIgnoreCase(pe.getUniqueIdentifier()))
        // {
        // returnValues.add(pe);
        // isFind = true;
        // break;
        // }
        // }
        // if(!isFind)
        // {
        // returnValues.add(null);
        // }
        // }

        return returnValues;
    }

    private void printListProtocolEndpoint(List<ProtocolEndpoint> pes) {
        LOGGER.info("ProtocolEndpoint size is:" + pes.size());
        for (int i = 0; i < pes.size(); i++) {
            LOGGER.info(
                    "ProtocolEndpoint[" + i + "]:\n" + "uniqueIdentifier = " + pes.get(i).getUniqueIdentifier() + "\n"
                            + "protocolEndpointType = "
                            + ((pes.get(i).getInBandId() == null) ? null
                            : pes.get(i).getInBandId().getProtocolEndpointType()));
            if (pes.get(i).getInBandId() != null) {
                String peType = pes.get(i).getInBandId().getProtocolEndpointType();
                if (peType != null) {
                    if (peType.equals(ProtocolEndpointTypeEnum.NFS.value())) {
                        LOGGER.info("\n" + "IpAddress = "
                                + ((pes.get(i).getInBandId() == null) ? null : pes.get(i).getInBandId().getIpAddress()));
                        LOGGER.info("\n" + "Mount = "
                                + ((pes.get(i).getInBandId() == null) ? null : pes.get(i).getInBandId().getServerMount()));
                    } else {
                        LOGGER.info("\n" + "lunId = "
                                + ((pes.get(i).getInBandId() == null) ? null : pes.get(i).getInBandId().getLunId()));
                    }
                }
            }

        }
    }

    /**
     * 方法 ： queryVirtualVolume
     *
     * @param constraints 方法参数：constraints
     * @return List<String> 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public List<String> queryVirtualVolume(List<QueryConstraint> constraints) throws StorageFault {
        LOGGER.info("queryVirtualVolume called. constraints:\n" + VASAUtil.convertQueryConstraints2Str(constraints));
        List<String> returnValues = discoverService.queryVirtualVolume(constraints);
        LOGGER.info("print queryVirtualVolume response:" + VASAUtil.convertArrayToStr(returnValues));
        return returnValues;
    }

    /**
     * 方法 ： queryVirtualVolumeInfo
     *
     * @param vvolIds 方法参数：vvolIds
     * @return List<VirtualVolumeInfo> 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public List<VirtualVolumeInfo> queryVirtualVolumeInfo(List<String> vvolIds) throws StorageFault {
        LOGGER.info("queryVirtualVolumeInfo called. vvolIds:" + VASAUtil.convertArrayToStr(vvolIds));
        if (null == vvolIds || vvolIds.size() == 0) {
            return new ArrayList<VirtualVolumeInfo>(0);
        }

        List<VirtualVolumeInfo> returnValues = discoverService.queryVirtualVolumeInfo(vvolIds);
        printResponseQueryVirtualVolumeInfo(returnValues);
        return returnValues;
    }

    private void printResponseQueryVirtualVolumeInfo(List<VirtualVolumeInfo> returnValues) {
        LOGGER.info("---- print QueryVirtualVolumeInfo result ----");
        for (int i = 0; i < returnValues.size(); i++) {
            LOGGER.info("vvolInfo[" + i + "]:\n" + VASAUtil.convertVirtualVolumeInfo2Str(returnValues.get(i)));
        }
    }

    public void deleteVirtualVolume(String vvolId) throws StorageFault, VasaProviderBusy, NotFound, ResourceInUse {
        LOGGER.info("deleteVirtualVolume called. vvolId:" + vvolId);
        discoverService.deleteVirtualVolume(vvolId);
    }

    /**
     * 方法 ： resizeVirtualVolume
     *
     * @param vvolid  方法参数：vvolid
     * @param newSize 方法参数：newSize
     * @return TaskInfo 返回结果
     * @throws StorageFault  异常：StorageFault
     * @throws NotFound
     * @throws NotSupported
     * @throws OutOfResource
     */
    public TaskInfo resizeVirtualVolume(String vvolid, long newSize)
            throws NotSupported, StorageFault, NotFound, OutOfResource {
        LOGGER.info("resizeVirtualVolume called. vvolid:" + vvolid + ", newSize:" + newSize);
        TaskInfo result = discoverService.resizeVirtualVolume(vvolid, newSize);
        printResponseTaskInfo(result);
        return result;
    }

    /**
     * 方法 ： bindVirtualVolume
     *
     * @param usageContext 方法参数：usageContext
     * @param vvolIds      方法参数：vvolIds
     * @param sessionId    方法参数：sessionId
     * @return List<BatchVirtualVolumeHandleResult> 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public List<BatchVirtualVolumeHandleResult> bindVirtualVolume(String sessionId, UsageContext usageContext,
                                                                  List<String> vvolIds, String bindContext) throws StorageFault, InvalidArgument {
        LOGGER.info("bindVirtualVolume called. vvolIds:" + VASAUtil.convertArrayToStr(vvolIds) + ", bindContext:"
                + bindContext);
        LOGGER.info("hostInitiatorInfo:"
                + VASAUtil.convertArrayToStr(VASAUtil.convertHostInitiators(usageContext.getHostInitiator())));
        List<ProtocolEndpoint> pEs = DataUtil.getInstance().getPEsBySessionId(sessionId);
        if (null == pEs || 0 == pEs.size()) {
            LOGGER.error("StorageFault/no PE found for this session:" + VASAUtil.replaceSessionId(sessionId));
            throw FaultUtil.storageFault("no PE found for this session:" + VASAUtil.replaceSessionId(sessionId));
            // List<BatchVirtualVolumeHandleResult> returnResults = new
            // ArrayList<BatchVirtualVolumeHandleResult>();
            // for(String vvolId : vvolIds)
            // {
            // BatchVirtualVolumeHandleResult batchVvolHandleResult = new
            // BatchVirtualVolumeHandleResult();
            // batchVvolHandleResult.setVvolId(vvolId);
            // com.vmware.vim.vasa.v20.fault.xsd.StorageFault sf = new
            // com.vmware.vim.vasa.v20.fault.xsd.StorageFault();
            // sf.setFaultMessageId(vvolId);
            // batchVvolHandleResult.setFault(sf);
            // returnResults.add(batchVvolHandleResult);
            // }
            // return returnResults;

        }
        VASAUtil.printPeWWN(pEs);
        int bindType = 0;
        if (null != bindContext && bindContext.equalsIgnoreCase("RebindStart")) {
            bindType = 1;
        }

        List<BatchVirtualVolumeHandleResult> result = discoverService.bindVirtualVolume(pEs, usageContext, vvolIds,
                bindType);

        printResponseBindVirtualVolume(result);
        return result;
    }


    private void printResponseBindVirtualVolume(List<BatchVirtualVolumeHandleResult> results) {
        LOGGER.info("---- print bindVirtualVolume result----");
        for (BatchVirtualVolumeHandleResult result : results) {
            if (null != result.getVvolHandle()) {
                LOGGER.info("vvolid:" + result.getVvolId() + ", vvolSecondaryId:"
                        + result.getVvolHandle().getVvolSecondaryId());

            } else {
                LOGGER.error(
                        "vvolid:" + result.getVvolId() + ", fault: " + JsonUtil.parse2JsonString(result.getFault()));
            }
        }
    }

    /**
     * 方法 ： unbindVirtualVolumeFromAllHost
     *
     * @param vvolIds 方法参数：vvolIds
     * @return List<BatchReturnStatus> 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public List<BatchReturnStatus> unbindVirtualVolumeFromAllHost(List<String> vvolIds) throws StorageFault {
        LOGGER.info("unbindVirtualVolumeFromAllHost called. vvolIds:" + VASAUtil.convertArrayToStr(vvolIds));
        List<BatchReturnStatus> result = discoverService.unbindVirtualVolumeFromAllHost(vvolIds);
        printResponseUnbindVirtualVolumefromAllHost(result);
        return result;
    }

    private void printResponseUnbindVirtualVolumefromAllHost(List<BatchReturnStatus> returnValues) {
        LOGGER.info("---- UnbindVirtualVolumefromAllHost result ----");
        for (BatchReturnStatus status : returnValues) {
            LOGGER.info("uniqueId:" + status.getUniqueId() + ", errResult:"
                    + JsonUtil.parse2JsonString(status.getErrorResult()));
        }
    }

    /**
     * 方法 ： unbindAllVirtualVolumesFromHost
     *
     * @param usageContext 方法参数：usageContext
     * @return void 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public void unbindAllVirtualVolumesFromHost(UsageContext usageContext) throws StorageFault {
        LOGGER.info("unbindAllVirtualVolumesFromHost called. hostInitiatorInfo:"
                + VASAUtil.convertArrayToStr(VASAUtil.convertHostInitiators(usageContext.getHostInitiator())));
        discoverService.unbindAllVirtualVolumesFromHost(usageContext);
    }

    /**
     * 方法 ： unbindVirtualVolume
     *
     * @param vvolHandles 方法参数：vvolHandles
     * @return List<BatchReturnStatus> 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public List<BatchReturnStatus> unbindVirtualVolume(List<VirtualVolumeHandle> vvolHandles, String unbindContext,
                                                       UsageContext usageContext) throws StorageFault {
        LOGGER.info("unbindVirtualVolume called. unbindContext:" + unbindContext + ", vvolHandle size:"
                + vvolHandles.size());
        for (VirtualVolumeHandle vvolHandle : vvolHandles) {
            if (vvolHandle.getPeInBandId().getLunId() != null) {
                LOGGER.info("unbindVirtualVolume vvolHandle uniqueIdentifier:" + vvolHandle.getUniqueIdentifier()
                        + ", PE Lun WWN:" + vvolHandle.getPeInBandId().getLunId() + ", vvolSecondaryId:"
                        + vvolHandle.getVvolSecondaryId());
            }

            if (vvolHandle.getPeInBandId().getIpAddress() != null) {
                LOGGER.info("unbindVirtualVolume vvolHandle uniqueIdentifier:" + vvolHandle.getUniqueIdentifier()
                        + ", PE IpAddress WWN:" + vvolHandle.getPeInBandId().getIpAddress() + ", vvolSecondaryId:"
                        + vvolHandle.getVvolSecondaryId());
            }
        }

        if (null != unbindContext && unbindContext.equalsIgnoreCase("RebindStart")) {
            List<BatchReturnStatus> allResults = new ArrayList<BatchReturnStatus>();
            for (VirtualVolumeHandle handle : vvolHandles) {
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                returnStatus.setUniqueId(handle.getUniqueIdentifier());
                allResults.add(returnStatus);
            }
            return allResults;
        }

        int bindType = 0;
        if (null != unbindContext && unbindContext.equalsIgnoreCase("RebindEnd")) {
            bindType = 2;
        }

        List<BatchReturnStatus> returnValues = discoverService.unbindVirtualVolume(vvolHandles, bindType, usageContext);
        printResponseUnbindVirtualVolume(returnValues);
        return returnValues;
    }

    private void printResponseUnbindVirtualVolume(List<BatchReturnStatus> returnValues) {
        LOGGER.info("---- print unbindVirtualVolume result ----");
        for (BatchReturnStatus status : returnValues) {
            LOGGER.info("uniqueId:" + status.getUniqueId() + ", errResult:"
                    + JsonUtil.parse2JsonString(status.getErrorResult()));
        }
    }

    /**
     * 方法 ： updateVirtualVolumeMetaData
     *
     * @param vvolId        方法参数：vvolId
     * @param keyValuePairs 方法参数：keyValuePairs
     * @return void 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws NotFound
     * @throws InvalidArgument
     */
    public void updateVirtualVolumeMetaData(String vvolId, List<NameValuePair> keyValuePairs)
            throws StorageFault, NotFound, InvalidArgument {
        LOGGER.info("updateVirtualVolumeMetaData called. vvolId:" + vvolId);
        for (NameValuePair pair : keyValuePairs) {
            LOGGER.info("[" + pair.getParameterName() + "] = [" + pair.getParameterValue() + "]");
            if (StringUtils.isEmpty(pair.getParameterName())) {
                LOGGER.error("InvalidArgument/invalid argument. VvolId: " + vvolId);
                throw FaultUtil.invalidArgument("invalid argument. VvolId: " + vvolId);
            }
        }

        discoverService.updateVirtualVolumeMetaData(vvolId, keyValuePairs);

    }

    /**
     * 方法 ： spaceStatsForVirtualVolume
     *
     * @param vvolIds 方法参数：vvolIds
     * @return List<SpaceStats> 返回结果
     * @throws StorageFault 异常：StorageFault
     */
    public List<SpaceStats> spaceStatsForVirtualVolume(List<String> vvolIds) throws StorageFault {
        LOGGER.info("spaceStatsForVirtualVolume called. vvolIds:" + VASAUtil.convertArrayToStr(vvolIds));
        if (null == vvolIds || vvolIds.size() == 0) {
            return new ArrayList<SpaceStats>();
        }

        List<SpaceStats> returnValues = discoverService.spaceStatsForVirtualVolume(vvolIds);
        printResponseSpaceStatsForVirtualVolume(returnValues);
        return returnValues;
    }

    private void printResponseSpaceStatsForVirtualVolume(List<SpaceStats> returnValues) {
        LOGGER.info("---- print SpaceStatsForVirtualVolume result ----");
        for (SpaceStats spaceInfo : returnValues) {
            LOGGER.info("objectId:" + spaceInfo.getObjectId() + ", logical:" + spaceInfo.getLogical() + ", committed:"
                    + spaceInfo.getCommitted() + ", unsharedValid:" + spaceInfo.isUnsharedValid() + ", unshared:"
                    + spaceInfo.getUnshared());
        }
    }

    /**
     * 方法 ： spaceStatsForStorageContainer
     *
     * @param containerId 方法参数：containerId
     * @return List<ContainerSpaceStats> 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws InvalidArgument
     * @throws NotFound
     */
    public List<ContainerSpaceStats> spaceStatsForStorageContainer(String containerId,
                                                                   List<String> capabilityProfileIds) throws InvalidArgument, StorageFault, NotFound {
        LOGGER.info("spaceStatsForStorageContainer called. containerId:" + containerId + ", capabilityProfileIds:"
                + VASAUtil.convertArrayToStr(capabilityProfileIds));
        if (!VASAUtil.checkContainerIdValid(containerId)) {
            LOGGER.error("InvalidArgument/invalid containerId:" + containerId);
            throw FaultUtil.invalidArgument("invalid containerId:" + containerId);
        }

        VASAUtil.checkContainerIdExist(containerId);

        List<ContainerSpaceStats> returnValues = discoverService.spaceStatsForStorageContainer(containerId,
                capabilityProfileIds);
        printResponseSpaceStatsForStorageContainer(returnValues);
        return returnValues;
    }

    private void printResponseSpaceStatsForStorageContainer(List<ContainerSpaceStats> returnValues) {
        LOGGER.info("---- print container space stats ----");
        for (ContainerSpaceStats spaceStats : returnValues) {
            LOGGER.info("objectId:" + spaceStats.getObjectId() + ", physicalTotal:" + spaceStats.getPhysicalTotal()
                    + ", physicalFree:" + spaceStats.getPhysicalFree() + ", physicalUsed:"
                    + spaceStats.getPhysicalUsed());
        }
    }

    /**
     * 方法 ： spaceStatsForStorageContainer
     *
     * @param containerId 方法参数：containerId
     * @return List<ContainerSpaceStats> 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws InvalidArgument
     * @throws NotFound
     */
    public TaskInfo prepareToSnapshotVirtualVolume(String vvolId, StorageProfile storageProfile)
            throws InvalidArgument, StorageFault, NotFound {
        LOGGER.info("prepareToSnapshotVirtualVolume called. vvolId:" + vvolId + ", storageProfile:\n"
                + VASAUtil.convertProfile2String(storageProfile));
        if (!VASAUtil.checkVvolIdValid(vvolId)) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + vvolId);
            throw FaultUtil.invalidArgument("invalid vvolId:" + vvolId);
        }

        TaskInfo result = discoverService.prepareToSnapshotVirtualVolume(vvolId, storageProfile);
        printResponseTaskInfo(result);
        return result;
    }

    /**
     * 方法 ： snapshotVirtualVolume
     *
     * @param snapshotInfos 方法参数：snapshotInfos
     * @param timeoutMS     方法参数：timeoutMS
     * @return List<BatchReturnStatus> 返回结果
     * @throws StorageFault    异常：StorageFault
     * @throws InvalidArgument
     * @throws StorageFault
     */
    public List<BatchReturnStatus> snapshotVirtualVolume(List<VirtualVolumeInfo> snapshotInfos, long timeoutMS)
            throws InvalidArgument, StorageFault {
        LOGGER.info("snapshotVirtualVolume called. timeoutMS:" + timeoutMS + ", snapshotInfos:");
        for (int i = 0; i < snapshotInfos.size(); i++) {
            LOGGER.info("snapshotInfo[" + i + "]:\n" + VASAUtil.convertVirtualVolumeInfo2Str(snapshotInfos.get(i)));
        }

        List<BatchReturnStatus> returnValues = discoverService.snapshotVirtualVolume(snapshotInfos, timeoutMS);
        printResponseSnapshotVirtualVolume(returnValues);
        return returnValues;
    }

    private void printResponseSnapshotVirtualVolume(List<BatchReturnStatus> returnValues) {
        LOGGER.info("---- print snapshotVirtualVolume result ----");
        for (BatchReturnStatus status : returnValues) {
            LOGGER.info("uniqueId:" + status.getUniqueId() + ", errResult:"
                    + JsonUtil.parse2JsonString(status.getErrorResult()));
        }
    }

    /**
     * 方法 ： revertVirtualVolume
     *
     * @param vvolId         方法参数：vvolId
     * @param snapshotVvolId 方法参数：snapshotVvolId
     * @return TaskInfo 返回结果
     * @throws IncompatibleVolume
     * @throws NotFound
     * @throws StorageFault
     */
    public TaskInfo revertVirtualVolume(String vvolId, String snapshotVvolId)
            throws StorageFault, NotFound, IncompatibleVolume {
        LOGGER.info("revertVirtualVolume called. vvolId:" + vvolId + ", snapshotVvolId:" + snapshotVvolId);
        TaskInfo result = discoverService.revertVirtualVolume(vvolId, snapshotVvolId);
        printResponseTaskInfo(result);
        return result;
    }

    /**
     * 方法 ： cloneVirtualVolume
     *
     * @param vvolId         方法参数：vvolId
     * @param newContainerId 方法参数：newContainerId
     * @param storageProfile 方法参数：storageProfile
     * @param medatada       方法参数：medatada
     * @return TaskInfo 返回结果
     * @throws StorageFault
     * @throws NotSupported
     * @throws NotFound
     * @throws OutOfResource
     * @throws InvalidProfile
     * @throws InvalidArgument
     * @throws ResourceInUse
     * @throws VasaProviderBusy
     */
    public TaskInfo cloneVirtualVolume(String vvolId, String newContainerId, StorageProfile storageProfile,
                                       List<NameValuePair> metadata) throws NotFound, NotSupported, StorageFault, OutOfResource, InvalidProfile,
            VasaProviderBusy, ResourceInUse, InvalidArgument {
        LOGGER.info("cloneVirtualVolume called. vvolId:" + vvolId + ", newContainerId:" + newContainerId
                + ", storageProfile:\n" + VASAUtil.convertProfile2String(storageProfile));
        VASAUtil.printVcenterProfile(storageProfile);
        VASAUtil.updateArrayIdByContainerId(newContainerId);
        LOGGER.info("cloneVirtualVolume: kv\n" + VASAUtil.convertNameValuePair2Str(metadata));

        TaskInfo result = null;

        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = discoverService.queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }

        NVirtualVolume vvol = vvols.get(0);
        if (null == newContainerId) {
            LOGGER.info("newcontainerId is null, use src vvol container");
            newContainerId = vvol.getContainerId();
        }
        VASAUtil.saveNameSpace(metadata);
        if (VASAUtil.checkProfileNull(storageProfile)) {
            LOGGER.info("the storageProfile is empty or null, createVirtualVolume With DefaultProfile");
            result = discoverService.cloneVirtualVolumeWithDefaultProfile(newContainerId, vvol.getVvolType(),
                    vvol.getSize(), metadata, storageProfile, vvol);
        } else {
            Boolean isThinEmpty = checkCreateVirtualVolumeParameters(vvol.getVvolType(), storageProfile);
            if (isThinEmpty) {
                String thinValue = VASAUtil.getPreferredThinValue(vvol.getVvolType());
                LOGGER.info("the thinProvision value in storageProfile is empty or null, add thin value[" + thinValue
                        + "] for vvolType[" + vvol.getVvolType() + "]");
                CapabilityInstance capability1 = new CapabilityInstance();
                CapabilityId capaId1 = new CapabilityId();
                capaId1.setId(VASAUtil.VMW_STD_CAPABILITY);
                capaId1.setNamespace(VASAUtil.VMW_NAMESPACE);
                ConstraintInstance constraint1 = new ConstraintInstance();
                PropertyInstance propertyInstance1 = new PropertyInstance();
                propertyInstance1.setId(VASAUtil.VMW_STD_CAPABILITY);
                DiscreteSet setValue = new DiscreteSet();
                setValue.getValues().add(thinValue);
                propertyInstance1.setValue(setValue);
                constraint1.getPropertyInstance().add(propertyInstance1);
                capability1.setCapabilityId(capaId1);
                capability1.getConstraint().add(constraint1);

                storageProfile.getConstraints().getSubProfiles().get(0).getCapability().add(capability1);
                result = discoverService.cloneVirtualVolumeWithStorageProfile(newContainerId, vvol.getVvolType(),
                        storageProfile, vvol.getSize(), metadata, true, vvol);
            } else {
                LOGGER.info("create virtual volume with storageProfile passed in");
                result = discoverService.cloneVirtualVolumeWithStorageProfile(newContainerId, vvol.getVvolType(),
                        storageProfile, vvol.getSize(), metadata, false, vvol);
            }
        }

        printResponseTaskInfo(result);
        return result;
    }

    /**
     * 方法 ： fastCloneVirtualVolume
     *
     * @param vvolId         方法参数：vvolId
     * @param storageProfile 方法参数：storageProfile
     * @param medatada       方法参数：medatada
     * @return TaskInfo 返回结果
     * @throws StorageFault
     * @throws NotSupported
     * @throws NotFound
     * @throws InvalidProfile
     * @throws SnapshotTooMany
     */
    public TaskInfo fastCloneVirtualVolume(String vvolId, StorageProfile storageProfile, List<NameValuePair> metadata)
            throws NotFound, StorageFault, InvalidProfile, SnapshotTooMany {
        LOGGER.info("fastCloneVirtualVolume called. vvolId:" + vvolId + ", storageProfile:\n"
                + VASAUtil.convertProfile2String(storageProfile));

        LOGGER.info("fastCloneVirtualVolume: kv\n" + VASAUtil.convertNameValuePair2Str(metadata));

        if (!VASAUtil.checkProfileNull(storageProfile)) {
            for (CapabilityInstance capaIns : storageProfile.getConstraints().getSubProfiles().get(0).getCapability()) {
                if (StringUtils.isEmpty(capaIns.getCapabilityId().getId())) {
                    LOGGER.error("InvalidProfile/invalid profile, capability is null or empty.");
                    throw FaultUtil.invalidProfile("invalid profile, capability is null or empty.");
                }
            }
        }
        VASAUtil.saveNameSpace(metadata);
        TaskInfo result = discoverService.fastCloneVirtualVolume(vvolId, storageProfile, metadata);
        printResponseTaskInfo(result);
        return result;
    }

    /**
     * 方法 ： unsharedChunksVirtualVolume
     *
     * @param vvolId                  方法参数：vvolId
     * @param baseVvolId              方法参数：baseVvolId
     * @param segmentStartOffsetBytes 方法参数：segmentStartOffsetBytes
     * @param segmentLengthBytes      方法参数：segmentLengthBytes
     * @return VirtualVolumeUnsharedChunksResult 返回结果
     * @throws StorageFault
     * @throws NotFound
     * @throws IncompatibleVolume
     * @throws InvalidArgument
     */
    public VirtualVolumeUnsharedChunksResult unsharedChunksVirtualVolume(String vvolId, String baseVvolId,
                                                                         long segmentStartOffsetBytes, long segmentLengthBytes)
            throws StorageFault, NotFound, IncompatibleVolume, NotImplemented, InvalidArgument {
        LOGGER.info("unsharedChunksVirtualVolume called. vvolId:" + vvolId + ", baseVvolId:" + baseVvolId
                + ", segmentStartOffsetBytes:" + segmentStartOffsetBytes + ", segmentLengthBytes:"
                + segmentLengthBytes);
        VirtualVolumeUnsharedChunksResult result = discoverService.unsharedChunksVirtualVolume(vvolId, baseVvolId,
                segmentStartOffsetBytes, segmentLengthBytes);
        printResponseUnsharedChunksVirtualVolume(result);
        return result;
    }

    private void printResponseUnsharedChunksVirtualVolume(VirtualVolumeUnsharedChunksResult result) {
        LOGGER.info("print response. chunkSizeBytes:" + result.getChunkSizeBytes() + ", unsharedChunks:"
                + result.getUnsharedChunks() + ", scannedChunks:" + result.getScannedChunks());
    }

    /**
     * 方法 ： unsharedBitmapVirtualVolume
     *
     * @param vvolId                  方法参数：vvolId
     * @param baseVvolId              方法参数：baseVvolId
     * @param segmentStartOffsetBytes 方法参数：segmentStartOffsetBytes
     * @param segmentLengthBytes      方法参数：segmentLengthBytes
     * @param chunkSizeBytes          方法参数：chunkSizeBytes
     * @return VirtualVolumeBitmapResult 返回结果
     * @throws StorageFault
     * @throws NotFound
     * @throws IncompatibleVolume
     * @throws InvalidArgument
     */
    public VirtualVolumeBitmapResult unsharedBitmapVirtualVolume(String vvolId, String baseVvolId,
                                                                 long segmentStartOffsetBytes, long segmentLengthBytes, long chunkSizeBytes)
            throws NotFound, NotImplemented, InvalidArgument, StorageFault, IncompatibleVolume {
        LOGGER.info("unsharedBitmapVirtualVolume called. vvolId:" + vvolId + ", baseVvolId:" + baseVvolId
                + ", segmentStartOffsetBytes:" + segmentStartOffsetBytes + ", segmentLengthBytes:" + segmentLengthBytes
                + ", chunkSizeBytes:" + chunkSizeBytes);
        VirtualVolumeBitmapResult result = discoverService.unsharedBitmapVirtualVolume(vvolId, baseVvolId,
                segmentStartOffsetBytes, segmentLengthBytes, chunkSizeBytes);
        printResponseUnsharedBiemapVirtualVolume(result);
        return result;
    }

    private void printResponseUnsharedBiemapVirtualVolume(VirtualVolumeBitmapResult result) {
        LOGGER.debug("chunkBitmap:\n" + result.getChunkBitmap());
    }

    /**
     * 方法 ： allocatedBitmapVirtualVolume
     *
     * @param vvolId                  方法参数：vvolId
     * @param segmentStartOffsetBytes 方法参数：segmentStartOffsetBytes
     * @param segmentLengthBytes      方法参数：segmentLengthBytes
     * @param chunkSizeBytes          方法参数：chunkSizeBytes
     * @return VirtualVolumeBitmapResult 返回结果
     * @throws StorageFault
     * @throws NotFound
     * @throws InvalidArgument
     */
    public VirtualVolumeBitmapResult allocatedBitmapVirtualVolume(String vvolId, long segmentStartOffsetBytes,
                                                                  long segmentLengthBytes, long chunkSizeBytes) throws NotFound, NotImplemented, InvalidArgument, StorageFault {
        LOGGER.info("allocatedBitmapVirtualVolume called. vvolId:" + vvolId + ", segmentStartOffsetBytes:"
                + segmentStartOffsetBytes + ", segmentLengthBytes:" + segmentLengthBytes + ", chunkSizeBytes:"
                + chunkSizeBytes);
        VirtualVolumeBitmapResult result = discoverService.allocatedBitmapVirtualVolume(vvolId, segmentStartOffsetBytes,
                segmentLengthBytes, chunkSizeBytes);
        printResponseAllocatedBiemapVirtualVolume(result);
        return result;
    }

    private void printResponseAllocatedBiemapVirtualVolume(VirtualVolumeBitmapResult result) {
        LOGGER.debug("chunkBitmap:\n" + result.getChunkBitmap());
    }

    /**
     * 方法 ： copyDiffsToVirtualVolume
     *
     * @param srcVvolId     方法参数：srcVvolId
     * @param srcBaseVvolId 方法参数：srcBaseVvolId
     * @param dstVvolId     方法参数：dstVvolId
     * @return TaskInfo 返回结果
     * @throws StorageFault
     * @throws NotFound
     * @throws InvalidArgument
     * @throws VasaProviderBusy
     * @throws ResourceInUse
     * @throws IncompatibleVolume
     */
    public TaskInfo copyDiffsToVirtualVolume(String srcVvolId, String srcBaseVvolId, String dstVvolId)
            throws InvalidArgument, NotFound, StorageFault, IncompatibleVolume, NotImplemented, ResourceInUse, VasaProviderBusy {
        LOGGER.info("copyDiffsToVirtualVolume called. srcVvolId:" + srcVvolId + ", srcBaseVvolId:" + srcBaseVvolId
                + ", dstVvolId:" + dstVvolId);
        if (!VASAUtil.checkVvolIdValid(srcVvolId) || !VASAUtil.checkVvolIdValid(srcBaseVvolId)
                || !VASAUtil.checkVvolIdValid(dstVvolId)) {
            LOGGER.error("InvalidArgument/invalid vvolid");
            throw FaultUtil.invalidArgument();
        }
        TaskInfo result = discoverService.copyDiffsToVirtualVolume(srcVvolId, srcBaseVvolId, dstVvolId);
        printResponseTaskInfo(result);
        return result;
    }

    public List<TaskInfo> getCurrentTask(String arrayId) {
        LOGGER.info("getCurrentTask called. arrayId:" + arrayId);
        List<TaskInfo> returnValues = new ArrayList<>();
        List<NTaskInfo> runningTasks = discoverService.getRunningTaskByArrayId(arrayId);
        for (NTaskInfo nTaskInfo : runningTasks) {
            returnValues.add(nTaskInfo.getTaskInfo());
        }
        printResponseTaskInfoArray(returnValues);
        return returnValues;
    }

    private void printResponseTaskInfoArray(List<TaskInfo> returnValues) {
        for (TaskInfo result : returnValues) {
            printResponseTaskInfo(result);
        }
    }

    public void cancelTask(String taskId) throws NotCancellable, InvalidArgument, StorageFault, NotFound {
        LOGGER.info("cancelTask called. taskId:" + taskId);
        if (taskId == null) {
            LOGGER.error("InvalidArgument/taskId is null.");
            throw FaultUtil.invalidArgument("taskId is null.");
        }

        discoverService.cancelTask(taskId);
    }

    public TaskInfo getTaskUpdate(String taskId) throws InvalidArgument, StorageFault, NotFound, VasaProviderBusy {
        LOGGER.info("getTaskUpdate called. taskId:" + taskId);
        if (!VASAUtil.checkTaskIdValid(taskId)) {
            LOGGER.error("InvalidArgument/invalid taskId:" + taskId);
            throw FaultUtil.invalidArgument("invalid taskId:" + taskId);
        }

        TaskInfo result = discoverService.getTaskUpdate(taskId);
        printResponseTaskInfo(result);
        return result;
    }
}
