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

package org.opensds.vasa.domain.model;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DAlarm;
import org.opensds.vasa.domain.model.bean.S2DArray;
import org.opensds.vasa.domain.model.bean.S2DController;
import org.opensds.vasa.domain.model.bean.S2DEnumInfo;
import org.opensds.vasa.domain.model.bean.S2DFCInitiator;
import org.opensds.vasa.domain.model.bean.S2DFileSystem;
import org.opensds.vasa.domain.model.bean.S2DHost;
import org.opensds.vasa.domain.model.bean.S2DHostLink;
import org.opensds.vasa.domain.model.bean.S2DISCSIInitiator;
import org.opensds.vasa.domain.model.bean.S2DLIT;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DLunCopyBean;
import org.opensds.vasa.domain.model.bean.S2DOemInfo;
import org.opensds.vasa.domain.model.bean.S2DSystem;
import org.opensds.vasa.domain.model.bean.S2DSystemDSTConfig;
import org.opensds.vasa.domain.model.bean.S2DSystemTimeZone;
import org.opensds.vasa.interfaces.device.alarm.IAlarmCapability;
import org.opensds.vasa.interfaces.device.array.IArrayCapability;
import org.opensds.vasa.interfaces.device.array.IArrayTime;
import org.opensds.vasa.interfaces.device.controller.IControllerCapability;
import org.opensds.vasa.interfaces.device.enuminfo.IEnumInfoCapability;
import org.opensds.vasa.interfaces.device.fs.IFileSystemCapability;
import org.opensds.vasa.interfaces.device.host.IHostCapability;
import org.opensds.vasa.interfaces.device.hostlink.IHostLinkCapability;
import org.opensds.vasa.interfaces.device.initiator.IFCInitiatorCapability;
import org.opensds.vasa.interfaces.device.initiator.IISCSIInitiatorCapability;
import org.opensds.vasa.interfaces.device.lit.ILITCapability;
import org.opensds.vasa.interfaces.device.lun.ILunCapability;
import org.opensds.vasa.interfaces.device.oeminfo.IOEMInfoCapability;
import org.opensds.vasa.interfaces.device.sysdstconfig.ISystemDSTConfigCapability;
import org.opensds.vasa.interfaces.device.system.ISystemCapability;
import org.opensds.vasa.interfaces.device.systimezone.ISystemTimeZoneCapability;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;

import org.opensds.vasa.vasa20.device.array.logicalPort.IlogicPortService;
import org.opensds.vasa.vasa20.device.array.logicalPort.LogicPortQueryResBean;

public class StorageModel {

    private static IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil.getBean("deviceManager");

    public static IDeviceManager getDeviceManager() {
        return deviceManager;
    }

    public static void setDeviceManager(IDeviceManager deviceManager) {
        StorageModel.deviceManager = deviceManager;
    }

    public static String IPV4_FILTER = "";

    public SDKResult<String> getLITCount(String arrayId) throws SDKException {
        ILITCapability litCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILITCapability.class);
        SDKResult<String> result = litCapability.getLITCount(arrayId);
        return result;
    }

    public SDKResult<List<S2DLIT>> getLIT(String arrayId, String count) throws SDKException {
        ILITCapability litCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILITCapability.class);
        SDKResult<List<S2DLIT>> result = litCapability.getLIT(arrayId, count);
        return result;
    }

    public SDKResult<String> getFileSystemBySharePathCount(String arrayId, String sharePath) throws SDKException {
        IFileSystemCapability fsCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IFileSystemCapability.class);
        SDKResult<String> result = fsCapability.getFileSystemBySharePathCount(arrayId, sharePath);
        return result;
    }

    public SDKResult<String> getNFSSharePathByShareName(String arrayId, String shareName) throws SDKException {
        IFileSystemCapability fsCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IFileSystemCapability.class);
        SDKResult<String> result = fsCapability.getNFSSharePathByShareName(arrayId, shareName);
        return result;
    }

    public SDKResult<List<S2DFileSystem>> getFileSystemBySharePath(String arrayId, String sharePath, String count) throws SDKException {
        IFileSystemCapability fsCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IFileSystemCapability.class);
        SDKResult<List<S2DFileSystem>> result = fsCapability.getFileSystemBySharePath(arrayId, sharePath, count);
        return result;
    }

    public SDKResult<List<S2DArray>> getAllArray() throws SDKException {
        IArrayCapability arrayCapability =
                getDeviceManager().getDeviceServiceProxy("all",
                        IArrayCapability.class);
        SDKResult<List<S2DArray>> result = arrayCapability.getAllArray();
        return result;
    }

    public SDKResult<S2DArray> getArrayById(String arrayId) throws SDKException {
        IArrayCapability arrayCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IArrayCapability.class);
        SDKResult<S2DArray> result = arrayCapability.getArrayById(arrayId);
        return result;
    }

    public SDKResult<S2DSystem> getSystemInfo(String arrayId) throws SDKException {
        ISystemCapability sysCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISystemCapability.class);
        SDKResult<S2DSystem> result = sysCapability.getSystemInfo(arrayId);
        return result;
    }

    public SDKResult<List<S2DOemInfo>> getAllOemInfo(String arrayId) throws SDKException {
        IOEMInfoCapability oemCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IOEMInfoCapability.class);
        SDKResult<List<S2DOemInfo>> result = oemCapability.getAllOemInfo(arrayId);
        return result;
    }

    public SDKResult<List<S2DController>> getAllController(String arrayId) throws SDKException {
        IControllerCapability controllerCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IControllerCapability.class);
        SDKResult<List<S2DController>> result = controllerCapability.getAllController(arrayId);
        return result;
    }

    public SDKResult<List<S2DSystemTimeZone>> queryDeviceTimeZone(String arrayId) throws SDKException {
        ISystemTimeZoneCapability sysTimeZoneCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISystemTimeZoneCapability.class);
        SDKResult<List<S2DSystemTimeZone>> result = sysTimeZoneCapability.queryDeviceTimeZone(arrayId);
        return result;
    }

    public SDKResult<S2DSystemDSTConfig> getTimeZoneDST(String arrayId, String timeZoneName) throws SDKException {
        ISystemDSTConfigCapability sysDSTConfigCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ISystemDSTConfigCapability.class);
        SDKResult<S2DSystemDSTConfig> result = sysDSTConfigCapability.getTimeZoneDST(arrayId, timeZoneName);
        return result;
    }

    public SDKResult<S2DHost> getHostByIBInitiator(String arrayId, String id) throws SDKException {
        IHostCapability hostCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IHostCapability.class);
        SDKResult<S2DHost> result = hostCapability.getHostByIBInitiator(arrayId, id);
        return result;
    }

    public SDKResult<S2DFCInitiator> getFCInitiator(String arrayId, String id) throws SDKException {
        IFCInitiatorCapability fcCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IFCInitiatorCapability.class);
        SDKResult<S2DFCInitiator> result = fcCapability.getFCInitiator(arrayId, id);
        return result;
    }

    public SDKResult<S2DISCSIInitiator> getISCSIInitiator(String arrayId, String id) throws SDKException {
        IISCSIInitiatorCapability iscsiCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IISCSIInitiatorCapability.class);
        SDKResult<S2DISCSIInitiator> result = iscsiCapability.getISCSIInitiator(arrayId, id);
        return result;
    }

    public SDKResult<List<S2DLun>> getLunByHostID(String arrayId, String hostId) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<List<S2DLun>> result = lunCapability.getLunByHostID(arrayId, hostId);
        return result;
    }

    public SDKResult<List<S2DLun>> getLunByHostAndPort(String arrayId, String hostId, String metadata) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<List<S2DLun>> result = lunCapability.getLunByHostAndPort(arrayId, hostId, metadata);
        return result;
    }

    public SDKResult<List<S2DHostLink>> getHostLinkByInitiator(String arrayId,
                                                               String iniType, String portWwn, String nodeWwn) throws SDKException {
        IHostLinkCapability hostlinkCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IHostLinkCapability.class);
        SDKResult<List<S2DHostLink>> result = hostlinkCapability.getHostLinkByInitiator(arrayId, iniType, portWwn, nodeWwn);
        return result;
    }

    public SDKResult<List<S2DEnumInfo>> getAllStorageCapabilities(String arrayId) throws SDKException {
        IEnumInfoCapability enuminfoCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IEnumInfoCapability.class);
        SDKResult<List<S2DEnumInfo>> result = enuminfoCapability.getAllStorageCapabilities(arrayId);
        return result;
    }

    public SDKResult<List<S2DAlarm>> getAllEvent(String arrayid, String start, String end) throws SDKException {
        IAlarmCapability alarmCapability =
                getDeviceManager().getDeviceServiceProxy(arrayid,
                        IAlarmCapability.class);
        SDKResult<List<S2DAlarm>> result = alarmCapability.getAllEvent(arrayid, start, end);
        return result;
    }

    public SDKResult<List<S2DAlarm>> getRecentEvent(String arrayid, String start, String end) throws SDKException {
        IAlarmCapability alarmCapability =
                getDeviceManager().getDeviceServiceProxy(arrayid,
                        IAlarmCapability.class);
        SDKResult<List<S2DAlarm>> result = alarmCapability.getRecentEvent(arrayid, start, end);
        return result;
    }

    public SDKResult<List<S2DAlarm>> getAllAlarm(String arrayid, String start, String end) throws SDKException {
        IAlarmCapability alarmCapability =
                getDeviceManager().getDeviceServiceProxy(arrayid,
                        IAlarmCapability.class);
        SDKResult<List<S2DAlarm>> result = alarmCapability.getAllAlarm(arrayid, start, end);
        return result;
    }

    public SDKResult<List<S2DLun>> getThinLun(String arrayId) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<List<S2DLun>> result = lunCapability.getThinLun(arrayId);
        return result;
    }

    public SDKResult<List<S2DLun>> getPELun(String arrayId) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<List<S2DLun>> result = lunCapability.getPELun(arrayId);
        return result;
    }

    public SDKResult<List<LogicPortQueryResBean>> getNasPE(String arrayId) throws SDKException {
        IlogicPortService ilogicPortService =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IlogicPortService.class);
        SDKResult<List<LogicPortQueryResBean>> result = ilogicPortService.queryAllLogicPort(arrayId, null, null, IPV4_FILTER);
        return result;
    }


    //public SDKResult<List<>>
    public SDKResult<S2DLun> getLun(String arrayId, String lunId) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<S2DLun> result = lunCapability.getLun(arrayId, lunId);
        return result;
    }

    public SDKResult<S2DLunCopyBean> getLunCopy(String arrayId, String lunId) throws SDKException {
        ILunCapability lunCapability =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        ILunCapability.class);
        SDKResult<S2DLunCopyBean> result = lunCapability.getLunCopy(arrayId, lunId);
        return result;
    }

    public SDKResult<String> getUTCTime(String arrayId) throws SDKException {
        IArrayTime arrayTime =
                getDeviceManager().getDeviceServiceProxy(arrayId,
                        IArrayTime.class);
        SDKResult<String> result = arrayTime.getArrayUTCTime(arrayId);
        return result;
    }

}
