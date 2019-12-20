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

package org.opensds.vasa.vasa20.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.alarm.IAlarmCapability;
import org.opensds.vasa.interfaces.device.array.IArrayCapability;
import org.opensds.vasa.interfaces.device.array.IArrayTime;
import org.opensds.vasa.interfaces.device.bitmap.IBitmapCapability;
import org.opensds.vasa.interfaces.device.controller.IControllerCapability;
import org.opensds.vasa.interfaces.device.enuminfo.IEnumInfoCapability;
import org.opensds.vasa.interfaces.device.fs.IFileSystemCapability;
import org.opensds.vasa.interfaces.device.host.IHostCapability;
import org.opensds.vasa.interfaces.device.hostlink.IHostLinkCapability;
import org.opensds.vasa.interfaces.device.initiator.IFCInitiatorCapability;
import org.opensds.vasa.interfaces.device.initiator.IISCSIInitiatorCapability;
import org.opensds.vasa.interfaces.device.lit.ILITCapability;
import org.opensds.vasa.interfaces.device.lun.ILunCapability;
import org.opensds.vasa.interfaces.device.luncopy.ILunCopyCapability;
import org.opensds.vasa.interfaces.device.oeminfo.IOEMInfoCapability;
import org.opensds.vasa.interfaces.device.snapshot.ISnapshotCapability;
import org.opensds.vasa.interfaces.device.snapshot.ISnapshotextendCapability;
import org.opensds.vasa.interfaces.device.storagepool.IStoragePoolCapability;
import org.opensds.vasa.interfaces.device.sysdstconfig.ISystemDSTConfigCapability;
import org.opensds.vasa.interfaces.device.system.ISystemCapability;
import org.opensds.vasa.interfaces.device.systimezone.ISystemTimeZoneCapability;
import org.opensds.vasa.interfaces.device.virtualpool.IVirtualPoolCapability;
import org.opensds.vasa.interfaces.device.volume.IVolumeCapability;
import org.opensds.vasa.interfaces.device.volumetype.IVolumeTypeCapability;
import org.opensds.vasa.interfaces.device.vvolbind.IVvolBindCapability;
import org.opensds.vasa.vasa20.device.array.NFSshare.INFSshareService;
import org.opensds.vasa.vasa20.device.array.NFSshare.NFSshareService;
import org.opensds.vasa.vasa20.device.array.NFSvvol.INFSvvolService;
import org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolService;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemService;
import org.opensds.vasa.vasa20.device.array.fileSystem.IFileSystemService;
import org.opensds.vasa.vasa20.device.array.logicalPort.IlogicPortService;
import org.opensds.vasa.vasa20.device.array.logicalPort.LogicPortService;
import org.opensds.vasa.vasa20.device.array.lun.DeviceLunService;
import org.opensds.vasa.vasa20.device.array.lun.IDeviceLunService;
import org.opensds.vasa.vasa20.device.array.qos.DeviceQosService;
import org.opensds.vasa.vasa20.device.array.qos.IDeviceQosService;
import org.opensds.vasa.vasa20.device.array.snapshot.DeviceSnapshotService;
import org.opensds.vasa.vasa20.device.array.snapshot.IDeviceSnapshotService;
import org.opensds.vasa.vasa20.device.dj.alarm.AlarmCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.array.ArrayCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.array.ArrayTimeImpl;
import org.opensds.vasa.vasa20.device.dj.bitmap.BitmapCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.controller.ControllerCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.enuminfo.EnumInfoCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.fs.FileSystemCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.host.HostCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.hostlink.HostLinkCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.initiator.FCInitiatorCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.initiator.ISCSIInitiatorCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.lit.LITCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.lun.LunCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.luncopy.LunCopyCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.oeminfo.OEMInfoCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.snapshot.SnapshotCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.snapshot.SnapshotextendCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.sysdstconfig.SystemDSTConfigCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.system.SystemCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.systimezone.SystemTimeZoneCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.virtualpool.VirtualPoolCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.volume.VolumeCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.volumetype.VolumeTypeCapabilityImpl;
import org.opensds.vasa.vasa20.device.dj.vvolbind.VvolBindCapabilityImpl;
import org.opensds.vasa.vasa20.device.storage.storagepool.StoragePoolCapabilityImpl;

import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.commu.itf.IProtocolAdapterManager;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.nemgr.base.MultiConnDeviceBase;
import org.opensds.platform.nemgr.itf.IDeviceConnection;
import org.opensds.platform.nemgr.itf.IDeviceManager;

public class VASADevice extends MultiConnDeviceBase {

    private static final Logger LOGGER = LogManager
            .getLogger(MultiConnDeviceBase.class);

    private IDeviceManager deviceManager = ApplicationContextUtil
            .getBean("deviceManager");

    protected IProtocolAdapterManager protocolAdapterManager = ApplicationContextUtil
            .getBean("protocolAdapterManager");

    protected List<Class<?>> listCapaClass = new ArrayList<Class<?>>();

    //vasa 连接
    private VASAConnection vasaConn = null;

    /*
     * Service Access Point (URL)
     */
    private String sap;

    protected ISDKProtocolAdapter protocolAdapter;

    public ISDKProtocolAdapter getProtocolAdapter() {
        return protocolAdapter;
    }

    protected String getSap() {
        return sap;
    }

    protected void setSap(String sap) {
        this.sap = sap;
    }

    public VASADevice(String sap) {
        this.sap = sap;
        this.protocolAdapter = new RestfulAdapterImplForVasa(sap);
        protocolAdapter.setSdkProtocolAdatperCustProvider(new VASAJsonOverHttpCustProvider());
        prepareDeviceCapability(sap);
    }

    protected void prepareDeviceCapability(String sap) {
        LOGGER.debug("The SAP = " + sap);
        addServiceObjectMap(IArrayCapability.class, new ArrayCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IControllerCapability.class, new ControllerCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IEnumInfoCapability.class, new EnumInfoCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IFileSystemCapability.class, new FileSystemCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IHostCapability.class, new HostCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IHostLinkCapability.class, new HostLinkCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IFCInitiatorCapability.class, new FCInitiatorCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IISCSIInitiatorCapability.class, new ISCSIInitiatorCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ILITCapability.class, new LITCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ILunCapability.class, new LunCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IOEMInfoCapability.class, new OEMInfoCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ISystemDSTConfigCapability.class, new SystemDSTConfigCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ISystemCapability.class, new SystemCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ISystemTimeZoneCapability.class, new SystemTimeZoneCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IAlarmCapability.class, new AlarmCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IVolumeTypeCapability.class, new VolumeTypeCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IVirtualPoolCapability.class, new VirtualPoolCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IVolumeCapability.class, new VolumeCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IVvolBindCapability.class, new VvolBindCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ISnapshotCapability.class, new SnapshotCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ISnapshotextendCapability.class, new SnapshotextendCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IBitmapCapability.class, new BitmapCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(ILunCopyCapability.class, new LunCopyCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IStoragePoolCapability.class, new StoragePoolCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IArrayTime.class, new ArrayTimeImpl(
                this.protocolAdapter));

        // 新增4类操作阵列接口，用作创建卷使用
        // begin
        addServiceObjectMap(IDeviceLunService.class, new DeviceLunService(
                this.protocolAdapter));
        addServiceObjectMap(IDeviceQosService.class, new DeviceQosService(
                this.protocolAdapter));
        addServiceObjectMap(IDeviceSnapshotService.class, new DeviceSnapshotService(
                this.protocolAdapter));

        // add for nas
        addServiceObjectMap(IFileSystemService.class, new FileSystemService(
                this.protocolAdapter));
        addServiceObjectMap(INFSvvolService.class, new NFSvvolService(
                this.protocolAdapter));
        addServiceObjectMap(INFSshareService.class, new NFSshareService(
                this.protocolAdapter));
        addServiceObjectMap(IlogicPortService.class, new LogicPortService(
                this.protocolAdapter));

        listCapaClass.add(IFileSystemService.class);
        listCapaClass.add(IDeviceLunService.class);
        listCapaClass.add(IDeviceQosService.class);
        listCapaClass.add(IDeviceSnapshotService.class);
        listCapaClass.add(INFSvvolService.class);
        listCapaClass.add(INFSshareService.class);
        listCapaClass.add(IlogicPortService.class);
        // end

        listCapaClass.add(IArrayCapability.class);
        listCapaClass.add(IControllerCapability.class);
        listCapaClass.add(IEnumInfoCapability.class);
        listCapaClass.add(IFileSystemCapability.class);
        listCapaClass.add(IHostCapability.class);
        listCapaClass.add(IHostLinkCapability.class);
        listCapaClass.add(IFCInitiatorCapability.class);
        listCapaClass.add(IISCSIInitiatorCapability.class);
        listCapaClass.add(ILITCapability.class);
        listCapaClass.add(ILunCapability.class);
        listCapaClass.add(IOEMInfoCapability.class);
        listCapaClass.add(ISystemDSTConfigCapability.class);
        listCapaClass.add(ISystemCapability.class);
        listCapaClass.add(ISystemTimeZoneCapability.class);
        listCapaClass.add(IAlarmCapability.class);
        listCapaClass.add(IVolumeTypeCapability.class);
        listCapaClass.add(IVirtualPoolCapability.class);
        listCapaClass.add(IVolumeCapability.class);
        listCapaClass.add(IVvolBindCapability.class);
        listCapaClass.add(ISnapshotCapability.class);
        listCapaClass.add(ISnapshotextendCapability.class);
        listCapaClass.add(IBitmapCapability.class);
        listCapaClass.add(ILunCopyCapability.class);
        listCapaClass.add(IStoragePoolCapability.class);
        listCapaClass.add(IArrayTime.class);
    }


    // 修改问题单DTS2016121905489,之前代码
    @Override
    public IDeviceConnection createConnection(String appId, String sap,
                                              String user, String pwd) {
        if (null == vasaConn) {
            return new VASAConnection(protocolAdapter, this, user, pwd);
        } else {
            LOGGER.info("VASAConnection has been initialized.");
            return vasaConn;
        }
    }

    @Override
    public void prepareAuthInfo(String user, String pwd) {
        prepareDevAuthInfo(user, pwd);
    }

    protected void prepareDevAuthInfo(String user, String pwd) {
        AccountInfo acctInfo = authorizePolicy.getDeviceAccountInfo(user, pwd);
        MessageContext mc = ThreadLocalHolder.get();
        mc.getEntities().put(ESDKConstant.DEVICE_USER_ID, acctInfo.getUserId());
        mc.getEntities().put(ESDKConstant.DEVICE_PLAIN_PWD, acctInfo.getPassword());
    }

    @Override
    public String getConnIdFromContext() {
        String id = "";
        MessageContext mc = ThreadLocalHolder.get();
        if (null != mc) {
            AccountInfo acctInfo = (AccountInfo) mc.getEntities().get(ESDKConstant.ACCT_INFO_ESDK);
            if (null != acctInfo) {
                AccountInfo devAcctInfo = authorizePolicy.getDeviceAccountInfo(acctInfo.getUserId(), acctInfo.getPassword());
                id = devAcctInfo.getUserId();// + acctInfo.getPassword();
            }
        }
        return id;
    }

    @Override
    public Boolean releaseConns() {
        for (String key : id2Connection.keySet()) {
            IDeviceConnection conn = id2Connection.get(key);
            deviceManager.releaseConn(conn);
        }
        return true;
    }

    public synchronized void updateCapabilityProtocolAdapter(ISDKProtocolAdapter protocolAdapter) {
        for (Class<?> capaClass : listCapaClass) {
            AbstractVASACapability capability = (AbstractVASACapability) getService(capaClass);
            capability.setProtocolAdapter(protocolAdapter);
        }
    }

}
