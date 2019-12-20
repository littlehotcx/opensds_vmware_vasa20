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

package org.opensds.vasa.vasa.db.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.opensds.vasa.vasa.db.dao.StorageManagerDao;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VvolType;
import org.opensds.vasa.vasa.db.service.StorageManagerService;

public class StorageManagerServiceImpl implements StorageManagerService {
    private StorageManagerDao storageManagerDao;
    private Logger LOGGER = LogManager.getLogger(StorageManagerServiceImpl.class);

    @Override
    public void setInfo(StorageInfo storageInfo) {
        storageManagerDao.setInfo(storageInfo);
    }

    @Override
    public List<StorageInfo> queryInfo() {
        return storageManagerDao.queryInfo();
    }

    @Override
    public void deleteInfo(StorageInfo storageInfo) {
        storageManagerDao.deleteInfo(storageInfo);
    }

    @Override
    public void modifyInfo(StorageInfo storageInfo) {
        storageManagerDao.modifyInfo(storageInfo);
    }

    public StorageManagerDao getStorageManagerDao() {
        return storageManagerDao;
    }

    public void setStorageManagerDao(StorageManagerDao storageManagerDao) {
        this.storageManagerDao = storageManagerDao;
    }

    @Override
    public void addExistDevice(StorageInfo storageInfo) {
        storageManagerDao.addExistDevice(storageInfo);
    }

    @Override
    public List<VvolType> queryVvolType() {
        return storageManagerDao.queryVvolType();
    }

    @Override
    public StorageInfo queryInfoByArrayId(String arrayId) {
        // TODO Auto-generated method stub
        return storageManagerDao.queryInfoByArrayId(arrayId);
    }

    @Override
    public void syncInfo(StorageInfo storageInfo) {
        storageManagerDao.syncInfo(storageInfo);
    }

    @Override
    public void updateStatus(StorageInfo storageInfo) {
        storageManagerDao.updateStatus(storageInfo);
    }

    @Override
    public List<StorageInfo> queryStorageArray(String pageSize, String pageIndex) {
        LOGGER.info("In queryStorageArray function,the pageSize " + pageSize + " pageIndex=" + pageIndex);
        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        Map<String, String> map = new HashMap<String, String>();
        map.put("offSet", String.valueOf(offSet));
        map.put("pageSize", pageSize);
        return storageManagerDao.queryStorageArray(map);
    }

    @Override
    public void updateExistDeviceInfo(StorageInfo storageInfo) {
        storageManagerDao.updateExistDeviceInfo(storageInfo);
    }

    @Override
    public StorageInfo getStorageBySn(String sn) {
        return storageManagerDao.getStorageBySn(sn);
    }

    @Override
    public long getStorageArrayCount() {
        return storageManagerDao.getStorageArrayCount();
    }

    @Override
    public List<StorageInfo> needSyncStorageInfo(String syncIp) {
        return storageManagerDao.needSyncStorageInfo(syncIp);
    }
}
