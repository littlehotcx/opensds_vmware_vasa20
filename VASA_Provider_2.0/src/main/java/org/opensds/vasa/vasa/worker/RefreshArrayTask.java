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

package org.opensds.vasa.vasa.worker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.service.SecureConnectionService;
import org.opensds.vasa.vasa20.device.array.db.service.StorageArrayService;
import org.opensds.vasa.vasa20.device.array.login.DeviceSwitchMetaData;
import org.opensds.vasa.vasa20.device.array.login.DeviceSwitchUtil;

import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.DeviceManager;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.VASAUtil;

public class RefreshArrayTask extends TimerTask {

    private static Logger LOGGER = LogManager.getLogger(RefreshArrayTask.class);

    private DataUtil dataUtil;
    private DiscoverService discoverService;
    private SecureConnectionService secureConnectionService;
    private StorageArrayService storageArrayService = ApplicationContextUtil.getBean("storageArrayService");
    private DeviceManager deviceManager = ApplicationContextUtil.getBean("deviceManager");

    public void run() {
        while (true) {
            try {
                // 从缓存中拿到阵列ID
                Set<String> cachedArrays = dataUtil.getArrayId();

                List<StorageInfo> arrs = discoverService.queryAllArrays();
                Set<String> arrIds = new HashSet<String>();
                refreshArray(arrs, arrIds);
                dataUtil.setArrayId(arrIds);

                if (secureConnectionService.checkCurrentNodeIsMaster()) {
                    // 产生阵列添加删除事件,返回给所有订阅了Config事件的vcenter server
                    List<String> ucUUIDs = VASAUtil.getConfigEventUcUUIDs();
                    discoverService.appendConfigStorageArrayEvent(ucUUIDs, VASAUtil.convertSet2List(cachedArrays),
                            VASAUtil.convertSet2List(arrIds));

                    // 刷新完缓存后将VasaEvent表中不存在的记录删除
                    discoverService.updateVasaEventTable(VASAUtil.convertSet2List(arrIds));
                }

                Thread.sleep(MagicNumber.INT10 * MagicNumber.INT60 * MagicNumber.INT1000);
            } catch (Exception e) {
                LOGGER.error("getAllArray error!", e);
                try {
                    Thread.sleep(MagicNumber.INT60 * MagicNumber.INT1000);
                } catch (InterruptedException e1) {
                    LOGGER.error("InterruptedException error " + e1.getMessage());
                }
            }
        }
    }

    private void refreshArray(List<StorageInfo> arrs, Set<String> arrIds)
            throws SDKException {
        for (StorageInfo arr : arrs) {
            //校验阵列是否在线
            //1.begin
            if (null == arr.getIps()) {
                LOGGER.error("the array ips is null. arrayId=" + arr.getId());
                continue;
            }
            boolean arrayOnline = false;
            LOGGER.debug("check array is online or offline, arrayId = " + arr.getId());
            String[] ips = arr.getIps().split(",");

            //to prevent refresh device to "online" status which is in"syncError" status
            if (arr.getDevicestatus() == "SyncError") {
                continue;
            }

            for (String ip : ips) {
                DeviceSwitchMetaData deviceSwitchMetaData = new DeviceSwitchMetaData(ip, arr.getPort(), arr.getUsername(), arr.getPassword());
                DeviceSwitchUtil deviceSwitchUtil = new DeviceSwitchUtil(deviceSwitchMetaData.getServerUrl());
                if (deviceSwitchUtil.loginDevice(deviceSwitchMetaData.getUname(), deviceSwitchMetaData.getUpass())) {
                    deviceSwitchUtil.logoutDevice();
                    arrIds.add(arr.getId());
                    storageArrayService.updateArrayControllerIP(arr.getId(), "", "ONLINE");
                    arrayOnline = true;
                    LOGGER.debug("array is online, arrayId = " + arr.getId());
                    break;
                }
            }
            if (!arrayOnline) {
                LOGGER.debug("array is offline, arrayId = " + arr.getId());
                storageArrayService.updateArrayControllerIP(arr.getId(), "", "OFFLINE");
            }
        }
        deviceManager.refreshDevices();
    }

}
