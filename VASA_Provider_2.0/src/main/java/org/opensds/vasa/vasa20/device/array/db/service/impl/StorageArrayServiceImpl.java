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

package org.opensds.vasa.vasa20.device.array.db.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa20.device.array.db.dao.StorageArrayDao;
import org.opensds.vasa.vasa20.device.array.db.model.NStorageArrayBean;
import org.opensds.vasa.vasa20.device.array.db.service.StorageArrayService;

import org.opensds.platform.common.exception.SDKException;
import org.opensds.vasa.vasa.service.DiscoverService;


public class StorageArrayServiceImpl implements StorageArrayService {
    private static Logger LOGGER = LogManager
            .getLogger(StorageArrayServiceImpl.class);

    private StorageArrayDao storageArrayDao;

    private DiscoverService discoverService = DiscoverService.getInstance();

    public StorageArrayDao getStorageArrayDao() {
        return storageArrayDao;
    }

    public void setStorageArrayDao(StorageArrayDao storageArrayDao) {
        this.storageArrayDao = storageArrayDao;
    }


    @Override
    public List<NStorageArrayBean> getAllStorageArray() throws SDKException {
        try {
            return storageArrayDao.getAllStorageArray();
        } catch (Exception e) {
            LOGGER.error("getAllStorageArray error.");
            throw new SDKException("getAllStorageArray error.");
        }
    }

    @Override
    public NStorageArrayBean getArrayControllIPS(String arrayId) throws SDKException {
        try {
            return storageArrayDao.getArrayControllIPS(arrayId);
        } catch (Exception e) {
            LOGGER.error("getArrayControllIPS error.");
            throw new SDKException("getArrayControllIPS error.");
        }
    }

    @Override
    public void updateArrayControllerIP(String arrayId, String ip, String status) throws SDKException {
        try {
            Map<String, String> params = new HashMap<String, String>();

            params.put("arrayId", arrayId);
            params.put("ip", ip);
            params.put("DeviceStatus", status);

            storageArrayDao.updateArrayControllerIP(params);
            discoverService.updateArrayInfoMapDeviceStatus(arrayId, status);
            List<String> arrayIds = new ArrayList<String>();
            arrayIds.add(arrayId);
            //阵列状态改变不上报阵列的更新事件，而是上报告警事件
            discoverService.addStorageArrayUpdateEvent(arrayIds);
        } catch (Exception e) {
            LOGGER.error("updateArrayControllerIP error.", e);
            throw new SDKException("updateArrayControllerIP error.");
        }
    }

}
