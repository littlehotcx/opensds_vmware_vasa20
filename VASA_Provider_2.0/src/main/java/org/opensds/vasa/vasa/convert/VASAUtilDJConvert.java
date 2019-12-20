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

package org.opensds.vasa.vasa.convert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.common.EnumDefine;
import org.opensds.vasa.common.HealthState;
import org.opensds.vasa.common.MOType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.DFileSystemInfo;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.IsmStoageLun;
import org.opensds.vasa.domain.model.bean.IsmStorageCapability;
import org.opensds.vasa.domain.model.bean.IsmStorageFileSystem;
import org.opensds.vasa.domain.model.bean.IsmStoragePort;
import org.opensds.vasa.domain.model.bean.S2DAlarm;
import org.opensds.vasa.domain.model.bean.S2DEnumInfo;
import org.opensds.vasa.domain.model.bean.S2DFileSystem;
import org.opensds.vasa.domain.model.bean.S2DHostLink;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DVolumeType;
import org.opensds.vasa.vasa.common.VvolConstant;
import org.opensds.vasa.vasa.internal.Event;

import org.opensds.vasa.base.common.VASAArrayUtil;

import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.StringUtils;

import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.DiscreteSet;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.Range;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityId;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.ConstraintInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyInstance;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.CapabilityConstraints;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.SubProfile;
import com.vmware.vim.vasa.v20.data.vvol.xsd.InBandBindCapabilityEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointInbandId;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.AlarmStatusEnum;
import com.vmware.vim.vasa.v20.data.xsd.BlockEnum;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.StorageContainer;

public final class VASAUtilDJConvert {
    private static Logger LOGGER = LogManager
            .getLogger(VASAUtilDJConvert.class);

    private VASAUtilDJConvert() {

    }

    public static IsmStorageFileSystem convert2DFileSystem(String arrayId, Map<S2DFileSystem, MountInfo> map) {
        Iterator<Entry<S2DFileSystem, MountInfo>> iterator = map.entrySet().iterator();
        Entry<S2DFileSystem, MountInfo> entry = iterator.next();
        S2DFileSystem s2dfs = entry.getKey();
        MountInfo mountInfo = entry.getValue();
        IsmStorageFileSystem dfs = new IsmStorageFileSystem();
        String id = s2dfs.getID() + ":" + mountInfo.getServerName() + ":" + mountInfo.getFilePath();//解决同一文件系统通过两个逻辑端口挂载问题
        String sharePath = s2dfs.getNAME();
        long capability = s2dfs.getCAPABILITY();
        dfs.setStorageFileSystemCapabilityID((int) capability);
        dfs.setUniqueIdentifier(VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_FILE_SYSTEM.value(), id));
        dfs.setFileSystemVersion("NFSV3_0");
        dfs.setFileSystem("NFS");
        dfs.setNativeSnapshotSupported(true);
        dfs.setThinProvisioningStatus("Green");
        dfs.setDrsManagementPermitted(false);
        DFileSystemInfo fsInfo = new DFileSystemInfo();
        MountInfo info = entry.getValue();
        String path = info.getFilePath();   //为共享名称
//		if (sharePath.equals(path.replace("/", ""))) sharePath为文件系统名称 不需要判断了之前已判断
//        {
        fsInfo.setFileSystemPath(path);
        fsInfo.setIpAddress(info.getServerName());
        fsInfo.setFileServerName(info.getServerName());
//        }
        dfs.getFileSystemInfo().add(fsInfo);
        return dfs;
    }

    public static IsmStoageLun convert2DLun(String arrayId, S2DLun obj) {
        IsmStoageLun storageLun = new IsmStoageLun();
        String lunId = obj.getID();

        long capability = Long.valueOf(obj.getCAPABILITY());
//        long capacity = Long.valueOf(obj.getCAPACITY());
        storageLun.setStorageLunCapabilityID((int) capability);
//        storageLun.setCapacityInMB(capacity * MagicNumber.INT512 / MagicNumber.INT1024 / MagicNumber.INT1024);
        storageLun.setDisplayName(obj.getNAME());
        // 判断LUN是否是ThinLUN
        int i = Integer.valueOf(obj.getALLOCTYPE());
        EnumDefine.LUN_ALLOC_TYPE_E typeE = EnumDefine.LUN_ALLOC_TYPE_E.valueOf(i);
        if (typeE == EnumDefine.LUN_ALLOC_TYPE_E.THIN) {
            storageLun.setThinProvisioned(true);
            // 获取Lun健康状态
            // 修改LUN状态获取[LUN故障 显示2] 确认在THIN LUN告警下是否OK
            if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage.toString())) {
                //FusionStorage
                String healthstatus = obj.getHEALTHSTATUS();
                LOGGER.info("=======Lun Status=========" + healthstatus);
                if ("DST_USELESS".equals(healthstatus)) {
                    healthstatus = "FAULTED";//FusionStorage的Lun状态DST_USELESS被认为是故障
                }
                switch (healthstatus) {
                    case "NORMAL":
                        storageLun.setThinProvisioningStatus(AlarmStatusEnum.GREEN.value());
                        break;
                    case "FAULTED":
                        storageLun.setThinProvisioningStatus(AlarmStatusEnum.RED.value());
                        break;
                    default:
                        storageLun.setThinProvisioningStatus(AlarmStatusEnum.YELLOW.value());
                        break;
                }

                //Fs返回单位Mb
                //总容量
                long capacityFS = Long.valueOf(obj.getCAPACITY());
                storageLun.setCapacityInMB(capacityFS);
                //已使用容量
                long usedSpace = Long.valueOf(obj.getALLOCCAPACITY());

                storageLun.setUsedSpaceInMB(usedSpace);
            } else {
                String lunStatusString = EnumDefine.HEALTH_STATUS_E.valueOf(Integer.valueOf(obj.getHEALTHSTATUS())).name();
                if (lunStatusString.equals(EnumDefine.HEALTH_STATUS_E.FAULT.name())) {
                    lunStatusString = HealthState.FAULTED.name();
                }
                HealthState lunStatus = HealthState.valueOf(lunStatusString);

                storageLun.setThinProvisioningStatus(VASAUtil.getStorageLunStatus(lunStatus));


                //总容量
                long capacity = Long.valueOf(obj.getCAPACITY());
                storageLun.setCapacityInMB(capacity * MagicNumber.INT512 / MagicNumber.INT1024 / MagicNumber.INT1024);
                //已使用容量
                long usedSpace = Long.valueOf(obj.getALLOCCAPACITY());
                storageLun.setUsedSpaceInMB(usedSpace * MagicNumber.INT512 / MagicNumber.INT1024 / MagicNumber.INT1024);
            }

//            long usedSpace = Long.valueOf(obj.getALLOCCAPACITY());
//
//            storageLun.setUsedSpaceInMB(usedSpace * MagicNumber.INT512 / MagicNumber.INT1024 / MagicNumber.INT1024);
        } else {
            storageLun.setThinProvisioned(false);
            storageLun.setThinProvisioningStatus("");
            storageLun.setUsedSpaceInMB(Long.MIN_VALUE);
        }
        storageLun.setUniqueIdentifier(VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_LUN.value(),
                String.valueOf(lunId)));

        String esxLunIdentifer = obj.getWWN();
        if (null != esxLunIdentifer && !esxLunIdentifer.contains(".")) {
            esxLunIdentifer = VASAUtil.ESX_LUN_IDENTIFER_NAMESPACE + "." + esxLunIdentifer;
        }
        storageLun.setEsxLunIdentifier(esxLunIdentifer);
        LOGGER.debug("Storage LUN exsLunIdentifer is " + storageLun.getEsxLunIdentifier());
        LOGGER.debug("storageLun CapabilityID is " + storageLun.getStorageLunCapabilityID());
        boolean lunDrs = Boolean.valueOf(obj.getDRS_ENABLE());
        storageLun.setDrsManagementPermitted(lunDrs);
        return storageLun;
    }

    public static DPort convert2DPort(String arrayId, S2DHostLink obj) {
        String portWWN = "";
        String iscsiInitator = "";
        int portType = Integer.valueOf(obj.getTARGET_TYPE());
        if (portType == MOType.ETH_PORT.getValue()) {
            iscsiInitator = obj.getTARGET_PORT_WWN();
        } else {
            portWWN = obj.getTARGET_PORT_WWN();
        }

        String nodeWWN = obj.getTARGET_NODE_WWN();
        //String portMixID = obj.getID();
        //String portID = VASAUtilDJConvert.getPortId(portMixID);
        String portID = getPortId(obj.getID(), obj.getTARGET_ID(), obj.getTARGET_PORT_WWN());
        String controllerID = obj.getCTRL_ID();
        String vasaPortType = VASAUtil.getVASAPortType(MOType.getType(portType));

        IsmStoragePort port = new IsmStoragePort();
        String uuid = VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_PORT.value(), portID);
        port.setUniqueIdentifier(uuid);

        if (vasaPortType.equals(BlockEnum.FC.value()) || vasaPortType.equals(BlockEnum.F_CO_E.value())) {
            ListUtil.clearAndAdd(port.getAlternateName(), new String[]
                    {portWWN, nodeWWN});
        } else {
            ListUtil.clearAndAdd(port.getAlternateName(), new String[]
                    {iscsiInitator});
        }

        port.setIscsiIdentifier(iscsiInitator);
        port.setPortWwn(portWWN);
        port.setNodeWwn(nodeWWN);
        port.setPortType(vasaPortType);
        port.setProcessorUniquIdentifier(VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_PROCESSOR.value(),
                controllerID));
        LOGGER.debug("IscsiIdentifier :" + port.getIscsiIdentifier() + " nodeWWN:" + port.getNodeWwn()
                + " portType:" + port.getPortType() + " portWWN:" + port.getPortWwn() + " portuniqueid:"
                + port.getUniqueIdentifier());
        StringBuffer printSb = new StringBuffer("Storage alternateName is:");
        for (String name : port.getAlternateName()) {
            printSb.append(name);
            printSb.append(',');
        }

        if (printSb.toString().endsWith(",")) {
            printSb.setLength(printSb.length() - 1);
        }
        LOGGER.debug("jsonObject2StoragePort :" + printSb.toString());

        return port;
    }

    /**
     * 根据hostlink 中的ID 属性，返回端口ID "ID":"0fb0f20de608760d-0000000001020102",
     * 返回0000000001020102的10进制的字符串
     *
     * @param idString ID属性
     * @return 端口id
     */
    public static String getPortId(String idString, String portTargetId, String portWWN) {
        String portId = null;
        int index = idString.indexOf("-");
        if (index != -1) {
            try {
                portId = String.valueOf(Long.valueOf(idString.substring(index + 1), MagicNumber.INT116));
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                LOGGER.warn("getPortId/NumberFormatException ID=" + idString);
                String portIdByPortWWN = getPortIdByPortWWN(portWWN);
                if (null != portIdByPortWWN) {
                    portId = portIdByPortWWN;
                } else {
                    LOGGER.warn("getPortId from portWWN is null. PORTWWN=" + portWWN);
                    portId = portTargetId;
                }
            }
        }
        return portId;
    }

    private static String getPortIdByPortWWN(String portwwn) {
        String[] split = portwwn.split("::");
        if (split.length == 2) {
            String[] portAndIP = split[1].split(":");
            if (split.length == 2) {
                return portAndIP[0];
            }
        }
        return null;
    }

    public static String removeZero(String idString) {
        char[] charArray = idString.toCharArray();
        char[] resultArray = new char[charArray.length];
        boolean isFirst = true;
        int index = 0;
        for (char c : charArray) {
            if (isFirst && c == '0') {
                continue;
            }
            isFirst = false;
            resultArray[index] = c;
            index++;
        }
        return charArray.toString();
    }


    /**
     * getPortType
     *
     * @param t porttype [int]
     * @return String 端口类型
     * @throws SDKException
     */
    public static String getPortType(int t) throws SDKException {
        if (t == MOType.ETH_PORT.getValue()) {
            return "iscsi";
        } else if (t == MOType.FC_PORT.getValue() || t == MOType.FCOE_PORT.getValue()) {
            return "fc";
        }
        throw new SDKException("invalid port type " + t);
    }

    /**
     * convert2DStorageCapability
     *
     * @param arrayId String
     * @param object  S2DEnumInfo
     * @return IsmStorageCapability
     * @throws JSONException if has error
     */
    public static IsmStorageCapability convert2DStorageCapability(String arrayId, S2DEnumInfo object) {
        IsmStorageCapability capability = new IsmStorageCapability();
        String capabilityName = null;
        String capabilityDetail = null;
        capabilityName = object.getENUM_NAME();
        capabilityDetail = object.getENUM_DESCRIPTION_EN();
        String capabilityID = object.getENUM_INDEX();

        // 封装storageCapability
        capability.setUniqueIdentifier(VASAUtil.buildCapabilityUUID(EntityTypeEnum.STORAGE_CAPABILITY.value(),
                capabilityID));
        capability.setStorageCapabilityTypeID(Integer.valueOf(capabilityID));
        capability.setCapabilityName(VASAUtil.buildCapabilityName(capabilityName));
        capability.setCapabilityDetail(capabilityDetail.trim());
        return capability;
    }

    /**
     * convert2Event
     *
     * @param arrayid String
     * @param obj     S2DAlarm
     * @return Event 事件
     */
    public static Event convert2Event(String arrayid, S2DAlarm obj) {

        int type = (int) obj.getType();
        long eventID = obj.getEventID();
        int levelVal = (int) obj.getLevel();
        Event.Level level = Event.Level.valueOf(levelVal);
        long sn = obj.getSequence();
        long startTime = obj.getStartTime();
        long resumeTime = obj.getRecoverTime();
        String param = obj.getEventParam();

        Event.Identifier identifier = new Event.Identifier(arrayid, sn);
        Event event = new Event(identifier, level, eventID, param, Util.getOSLocaleDefaultEn());
        //阵列返回的是秒数
        event.setStartTime(startTime * MagicNumber.LONG1000);
        event.setRecoverTime(resumeTime * MagicNumber.LONG1000);
        event.setType(Event.Type.valueOf(type));
        //for json serialize
        event.setDeviceId(identifier.getDeviceID());
        event.setDeviceSN(identifier.getEventSN());
        return event;
    }

    public static StorageProfile convert2StorageProfile(NStorageProfile nStorageProfile, NStorageQos storageQos, NProfileLevel profileLevel) {
        // TODO Auto-generated method stub
        LOGGER.info("Convert volume type to StorageProfile. nStorageProfile:" + nStorageProfile + ", storageQos:" + storageQos);
        StorageProfile storageProfile = new StorageProfile();
        String name = nStorageProfile.getProfileName();
        if (name.startsWith("_om_")) {
            name = name.substring(4);
        }
        String profileId = nStorageProfile.getProfileId();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nStorageProfile.getCreatedTime());
        CapabilityConstraints constraints = new CapabilityConstraints();
        SubProfile subProfile = new SubProfile();
        reportCapabilityProfile(nStorageProfile, storageQos, profileLevel, subProfile);
        subProfile.setName(name);
        constraints.getSubProfiles().add(subProfile);
        storageProfile.setConstraints(constraints);
        storageProfile.setCreatedBy("Opensds");
        storageProfile.setCreationTime(calendar);
        storageProfile.setDescription("something about " + name + " profile");
        storageProfile.setGenerationId((long) 0);
        storageProfile.setLastUpdatedBy("Opensds");
        storageProfile.setLastUpdatedTime(calendar);
        storageProfile.setName(name);
        storageProfile.setProfileId(profileId);
        VASAUtil.printVcenterProfile(storageProfile);
        return storageProfile;
    }


    /**
     * TODO 添加方法注释
     *
     * @param volType
     * @return
     */
    public static boolean isControlLowerBound(S2DVolumeType volType) {

        if (volType.getExtra_specs().getMinIOPS() > VvolConstant.MIN_VALUE || volType.getExtra_specs().getMinBandWidth() > VvolConstant.MIN_VALUE
                || volType.getExtra_specs().getMaxLatency() > VvolConstant.MIN_VALUE) {
            return true;
        }
        return false;
    }

    /**
     * TODO 添加方法注释
     *
     * @param volType
     * @return
     */
    public static boolean isControlUpperBound(S2DVolumeType volType) {
        if ((volType.getExtra_specs().getMaxIOPS() > VvolConstant.MIN_VALUE && volType.getExtra_specs().getMaxIOPS() <= VvolConstant.MAX_VALUE)
                || (volType.getExtra_specs().getMaxBandWidth() > VvolConstant.MIN_VALUE && volType.getExtra_specs().getMaxBandWidth() <= VvolConstant.MAX_VALUE)) {
            return true;
        }
        return false;
    }

    private static void reportCapabilityProfile(
            NStorageProfile nStorageProfile, NStorageQos storageQos, NProfileLevel profileLevel,
            SubProfile subProfile) {
        // TODO Auto-generated method stub
        String namespace = "";
        if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
            namespace = "org.opensds.vasaprovider.level";
        } else if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
            namespace = "org.opensds.vasaprovider.capability";
        } else {
            namespace = "org.opensds.vasaprovider";
        }

        if (nStorageProfile.getIsSmartTier()) {
            CapabilityId capaId = new CapabilityId();
            capaId.setId("SmartTier");
            capaId.setNamespace(namespace);

            PropertyInstance propertyInstance = new PropertyInstance();
            propertyInstance.setId("SmartTier");

            DiscreteSet objValue = new DiscreteSet();
            objValue.getValues().add("No relocation");
            objValue.getValues().add("Auto relocation");
            objValue.getValues().add("Relocation to low-performance");
            objValue.getValues().add("Relocation to high-performance");
            propertyInstance.setValue(objValue);

            ConstraintInstance constraint = new ConstraintInstance();
            constraint.getPropertyInstance().add(propertyInstance);

            CapabilityInstance capability = new CapabilityInstance();
            capability.setCapabilityId(capaId);
            capability.getConstraint().add(constraint);

            subProfile.getCapability().add(capability);
        } else {
            //该分支的唯一目的是防止上报的profile里面capability为空导致vcenter抛出异常.
            CapabilityId capaId = new CapabilityId();
            capaId.setId("SmartTier");
            capaId.setNamespace(namespace);

            PropertyInstance propertyInstance = new PropertyInstance();
            propertyInstance.setId("SmartTier");

            DiscreteSet objValue = new DiscreteSet();
            objValue.getValues().add("No relocation");
            propertyInstance.setValue(objValue);

            ConstraintInstance constraint = new ConstraintInstance();
            constraint.getPropertyInstance().add(propertyInstance);

            CapabilityInstance capability = new CapabilityInstance();
            capability.setCapabilityId(capaId);
            capability.getConstraint().add(constraint);

            subProfile.getCapability().add(capability);
        }


        if (nStorageProfile.getIsStorageMedium()) {
            CapabilityId diskTypeCapaId = new CapabilityId();
            diskTypeCapaId.setId("DiskType");
            diskTypeCapaId.setNamespace(namespace);

            PropertyInstance diskTypePropertyInstance = new PropertyInstance();
            diskTypePropertyInstance.setId("DiskType");
            diskTypePropertyInstance.setValue(nStorageProfile.getDiskTypeValue());

            ConstraintInstance diskTypeConstraint = new ConstraintInstance();
            diskTypeConstraint.getPropertyInstance().add(diskTypePropertyInstance);

            CapabilityInstance diskTypeCapability = new CapabilityInstance();
            diskTypeCapability.setCapabilityId(diskTypeCapaId);
            diskTypeCapability.getConstraint().add(diskTypeConstraint);


            CapabilityId raidLevelCapaId = new CapabilityId();
            raidLevelCapaId.setId("RaidLevel");
            raidLevelCapaId.setNamespace(namespace);

            PropertyInstance raidLevelPropertyInstance = new PropertyInstance();
            raidLevelPropertyInstance.setId("RaidLevel");
            if ("ALL".equalsIgnoreCase(nStorageProfile.getRaidLevelValue())) {
                DiscreteSet objValue = new DiscreteSet();
                objValue.getValues().add("RAID 10");
                objValue.getValues().add("RAID 5");
                objValue.getValues().add("RAID 0");
                objValue.getValues().add("RAID 1");
                objValue.getValues().add("RAID 6");
                objValue.getValues().add("RAID 50");
                objValue.getValues().add("RAID 3");
                raidLevelPropertyInstance.setValue(objValue);
            } else {
                raidLevelPropertyInstance.setValue(nStorageProfile.getRaidLevelValue());
            }
            ConstraintInstance raidLevelConstraint = new ConstraintInstance();
            raidLevelConstraint.getPropertyInstance().add(raidLevelPropertyInstance);

            CapabilityInstance raidlevelCapability = new CapabilityInstance();
            raidlevelCapability.setCapabilityId(raidLevelCapaId);
            raidlevelCapability.getConstraint().add(raidLevelConstraint);

            subProfile.getCapability().add(diskTypeCapability);
            subProfile.getCapability().add(raidlevelCapability);


        } else {
            CapabilityId diskTypecapaId = new CapabilityId();
            diskTypecapaId.setId("DiskType");
            diskTypecapaId.setNamespace(namespace);

            PropertyInstance diskTypePropertyInstance = new PropertyInstance();
            diskTypePropertyInstance.setId("DiskType");

            DiscreteSet diskTypeObjValue = new DiscreteSet();
            diskTypeObjValue.getValues().add("SSD");
            //diskTypeObjValue.getValues().add("SSD_SED");
            diskTypeObjValue.getValues().add("SAS");
            //diskTypeObjValue.getValues().add("SAS_SED");
            //diskTypeObjValue.getValues().add("SATA");
            diskTypeObjValue.getValues().add("NL_SAS");
            //diskTypeObjValue.getValues().add("NL_SAS_SED");

            diskTypePropertyInstance.setValue(diskTypeObjValue);

            ConstraintInstance diskTypeConstraint = new ConstraintInstance();
            diskTypeConstraint.getPropertyInstance().add(diskTypePropertyInstance);

            CapabilityInstance diskTypeCapability = new CapabilityInstance();
            diskTypeCapability.setCapabilityId(diskTypecapaId);
            diskTypeCapability.getConstraint().add(diskTypeConstraint);

            CapabilityId raidLevelCapaId = new CapabilityId();
            raidLevelCapaId.setId("RaidLevel");
            raidLevelCapaId.setNamespace(namespace);

            PropertyInstance raidLevelPropertyInstance = new PropertyInstance();
            raidLevelPropertyInstance.setId("RaidLevel");

            DiscreteSet objValue = new DiscreteSet();
            objValue.getValues().add("RAID 10");
            objValue.getValues().add("RAID 5");
            objValue.getValues().add("RAID 0");
            objValue.getValues().add("RAID 1");
            objValue.getValues().add("RAID 6");
            objValue.getValues().add("RAID 50");
            objValue.getValues().add("RAID 3");
            raidLevelPropertyInstance.setValue(objValue);

            ConstraintInstance raidLevelConstraint = new ConstraintInstance();
            raidLevelConstraint.getPropertyInstance().add(raidLevelPropertyInstance);

            CapabilityInstance raidlevelCapability = new CapabilityInstance();
            raidlevelCapability.setCapabilityId(raidLevelCapaId);
            raidlevelCapability.getConstraint().add(raidLevelConstraint);

            subProfile.getCapability().add(diskTypeCapability);
            subProfile.getCapability().add(raidlevelCapability);

        }
        boolean isUpperBound = true;

        if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
            if (storageQos.getControlPolicy() != null) {
                CapabilityId capaId = new CapabilityId();
                capaId.setId("FlowControlPolicy");
                capaId.setNamespace(namespace);

                PropertyInstance propertyInstance = new PropertyInstance();
                propertyInstance.setId("FlowControlPolicy");
                propertyInstance.setValue(storageQos.getControlPolicy());
                if (storageQos.getControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)) {
                    isUpperBound = false;
                }
                ConstraintInstance constraint = new ConstraintInstance();
                constraint.getPropertyInstance().add(propertyInstance);

                CapabilityInstance capability = new CapabilityInstance();
                capability.setCapabilityId(capaId);
                capability.getConstraint().add(constraint);

                subProfile.getCapability().add(capability);

            }

            if (storageQos.getControlType() != null) {
                CapabilityId capaId1 = new CapabilityId();
                capaId1.setId("FlowControlType");
                capaId1.setNamespace(namespace);

                PropertyInstance propertyInstance1 = new PropertyInstance();
                propertyInstance1.setId("FlowControlType");
                propertyInstance1.setValue(storageQos.getControlType());

                ConstraintInstance constraint1 = new ConstraintInstance();
                constraint1.getPropertyInstance().add(propertyInstance1);

                CapabilityInstance capability1 = new CapabilityInstance();
                capability1.setCapabilityId(capaId1);
                capability1.getConstraint().add(constraint1);

                subProfile.getCapability().add(capability1);
            }

            if (storageQos.getIops() != null && storageQos.getIops() != 0) {
                CapabilityId capaId2 = new CapabilityId();
                capaId2.setId("IOPS");
                capaId2.setNamespace(namespace);

                Range range2 = new Range();
                if (isUpperBound) {
                    range2.setMax((long) storageQos.getIops());
                    range2.setMin(0l);
                } else {
                    range2.setMin((long) storageQos.getIops());
                    range2.setMax(MagicNumber.LONG999999999);

                }

                PropertyInstance propertyInstance2 = new PropertyInstance();
                propertyInstance2.setId("IOPS");
                propertyInstance2.setValue(range2);

                ConstraintInstance constraint2 = new ConstraintInstance();
                constraint2.getPropertyInstance().add(propertyInstance2);

                CapabilityInstance capability2 = new CapabilityInstance();
                capability2.setCapabilityId(capaId2);
                capability2.getConstraint().add(constraint2);

                subProfile.getCapability().add(capability2);
            }

            if (storageQos.getBandWidth() != null && storageQos.getBandWidth() != 0) {
                CapabilityId capaId3 = new CapabilityId();
                capaId3.setId("Bandwidth");
                capaId3.setNamespace(namespace);

                Range range3 = new Range();
                if (isUpperBound) {
                    range3.setMax((long) storageQos.getBandWidth());
                    range3.setMin(0l);
                } else {
                    range3.setMin((long) storageQos.getBandWidth());
                    range3.setMax(MagicNumber.LONG999999999);
                }

                PropertyInstance propertyInstance3 = new PropertyInstance();
                propertyInstance3.setId("Bandwidth");
                propertyInstance3.setValue(range3);

                ConstraintInstance constraint3 = new ConstraintInstance();
                constraint3.getPropertyInstance().add(propertyInstance3);

                CapabilityInstance capability3 = new CapabilityInstance();
                capability3.setCapabilityId(capaId3);
                capability3.getConstraint().add(constraint3);

                subProfile.getCapability().add(capability3);
            }

            if (storageQos.getLatency() != null && storageQos.getLatency() != 0) {
                CapabilityInstance capability4 = new CapabilityInstance();
                CapabilityId capaId4 = new CapabilityId();
                capaId4.setId("Latency");
                capaId4.setNamespace(namespace);

                Range range4 = new Range();
                if (!isUpperBound) {
                    range4.setMax((long) storageQos.getLatency());
                    range4.setMin(0l);
                }

                ConstraintInstance constraint4 = new ConstraintInstance();
                PropertyInstance propertyInstance4 = new PropertyInstance();
                propertyInstance4.setId("Latency");
                propertyInstance4.setValue(range4);

                constraint4.getPropertyInstance().add(propertyInstance4);
                capability4.setCapabilityId(capaId4);
                capability4.getConstraint().add(constraint4);

                subProfile.getCapability().add(capability4);
            }
        } else if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
            if (StringUtils.isNotEmpty(profileLevel.getUserLevel())) {
                CapabilityId capaId = new CapabilityId();
                capaId.setId("UserLevel");
                capaId.setNamespace(namespace);
                PropertyInstance propertyInstance = new PropertyInstance();
                propertyInstance.setId("UserLevel");
                propertyInstance.setValue(profileLevel.getUserLevel());
                ConstraintInstance constraint = new ConstraintInstance();
                constraint.getPropertyInstance().add(propertyInstance);
                CapabilityInstance capability = new CapabilityInstance();
                capability.setCapabilityId(capaId);
                capability.getConstraint().add(constraint);
                subProfile.getCapability().add(capability);
            }
            if (StringUtils.isNotEmpty(profileLevel.getServiceType())) {
                CapabilityId capaId = new CapabilityId();
                capaId.setId("ServiceType");
                capaId.setNamespace(namespace);
                PropertyInstance propertyInstance = new PropertyInstance();
                propertyInstance.setId("ServiceType");
                propertyInstance.setValue(profileLevel.getServiceType());
                ConstraintInstance constraint = new ConstraintInstance();
                constraint.getPropertyInstance().add(propertyInstance);
                CapabilityInstance capability = new CapabilityInstance();
                capability.setCapabilityId(capaId);
                capability.getConstraint().add(constraint);
                subProfile.getCapability().add(capability);
            }
        } else if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control)) {
            CapabilityId capaId = new CapabilityId();
            capaId.setId("UseCapabilityControl");
            capaId.setNamespace(namespace);

            PropertyInstance propertyInstance = new PropertyInstance();
            propertyInstance.setId("UseCapabilityControl");

            DiscreteSet objValue = new DiscreteSet();
            objValue.getValues().add("True");
            objValue.getValues().add("False");
            propertyInstance.setValue(objValue);

            ConstraintInstance constraint = new ConstraintInstance();
            constraint.getPropertyInstance().add(propertyInstance);

            CapabilityInstance capability = new CapabilityInstance();
            capability.setCapabilityId(capaId);
            capability.getConstraint().add(constraint);

            subProfile.getCapability().add(capability);
        }

    }


    /**
     * convert2StorageContainer
     *
     * @param vPool S2DVirtualPool
     * @return StorageContainer
     */
    public static StorageContainer convert2StorageContainer(NStorageContainer nStorageContainer) {
        StorageContainer container = new StorageContainer();
        container.setUniqueIdentifier(nStorageContainer.getContainerId());
        container.setName(nStorageContainer.getContainerName());
//    	container.setMaxVvolSizeMB(vPool.getAvailable_capacity()/1024/1024);
        //阵列支持创建的vvol最大规格
        container.setMaxVvolSizeMB(VASAUtil.MAX_VVOL_SIZE_MB);
        return container;
    }

    /**
     * 获取ID 引擎ID admin:100.133.189.6设置网口属性（引擎 ENG0，ISCSI接口模块 B1，端口号 P2，最大传输速率
     * 2048）成功。
     *
     * @param arrayuuid   阵列UUID
     * @param engineId    ENG0格式
     * @param interfaceId B1板卡格式
     * @param portId      P2端口格式
     * @return 返回端口port uuid
     */
    public static String getPortIdFromCache(String arrayuuid, String engineId, String interfaceId, String portId) {

        DPort port = DataUtil.getInstance().getPortByEngAndCard(arrayuuid,
                VASAUtil.getLastCharNoLengthLimit(engineId) + VASAUtil.getFirstCharNoLengthLimit(interfaceId),
                VASAUtil.getLastCharNoLengthLimit(interfaceId), VASAUtil.getLastCharNoLengthLimit(portId));
        if (port == null) {
            return "";
        }
        return port.getUniqueIdentifier();
    }

    /**
     * 根据端口的ID返回槽位号，不带控制器编号 槽位好位于 BYTE3 BYTE2 BYTE1 BYTE0 5bit
     *
     * @param portid portid
     * @return 获取板卡ID
     */
    public static int getCardIdByPortId(String portid) {
        String s = Long.toBinaryString(Long.parseLong(portid));
        // System.out.println("src:\n" + s);
        s = s.substring(s.length() - MagicNumber.INT13, s.length() - MagicNumber.INT8);
        return Integer.valueOf(s, MagicNumber.INT2);
    }

    /**
     * 输入十进制的portid
     *
     * @param portid portid
     * @return 返回portid
     */
    public static int getPortNo(String portid) {
        // System.out.println("portid:L"+portid);
        String s = Integer.toBinaryString(Integer.valueOf(portid));
        s = s.substring(s.length() - MagicNumber.INT8);
        return Integer.valueOf(s, MagicNumber.INT2);
    }

    public static List<ProtocolEndpoint> convert2ProtocolEndpoint(String arrayId, List<S2DLun> luns) {
        List<ProtocolEndpoint> pes = new ArrayList<ProtocolEndpoint>();
        if (null == luns || luns.size() == 0) {
            return pes;
        }

        for (S2DLun lun : luns) {
            if (lun.getIPV4ADDR() != null) {
                ProtocolEndpoint nasPE = new ProtocolEndpoint();
                ProtocolEndpointInbandId nasInbindId = new ProtocolEndpointInbandId();
                nasInbindId.setProtocolEndpointType(ProtocolEndpointTypeEnum.NFS.value());
                nasInbindId.setIpAddress(lun.getIPV4ADDR());
                nasInbindId.setServerMount("/");
                nasPE.setUniqueIdentifier(arrayId + ":" + EntityTypeEnum.PROTOCOL_ENDPOINT.value() + ":" + lun.getID());
                nasPE.setInBandId(nasInbindId);
                nasPE.setInBandBindCapability(InBandBindCapabilityEnum.NONE.value());
                pes.add(nasPE);
                continue;
            }
            ProtocolEndpoint pe = new ProtocolEndpoint();
            ProtocolEndpointInbandId inbindId = new ProtocolEndpointInbandId();
            inbindId.setProtocolEndpointType(ProtocolEndpointTypeEnum.SCSI.value());
            inbindId.setLunId("naa." + lun.getWWN());
            pe.setInBandId(inbindId);
            pe.setUniqueIdentifier(arrayId + ":" + EntityTypeEnum.PROTOCOL_ENDPOINT.value() + ":" + lun.getID());
            pe.setInBandBindCapability(InBandBindCapabilityEnum.NONE.value());
            pes.add(pe);
        }

        return pes;
    }
}
