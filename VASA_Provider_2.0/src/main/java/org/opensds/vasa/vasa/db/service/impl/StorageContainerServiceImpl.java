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


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.StorageContainerDao;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.service.StorageContainerService;

import com.vmware.vim.vasa.v20.StorageFault;

public class StorageContainerServiceImpl extends BaseServiceImpl<NStorageContainer> implements StorageContainerService {
    public StorageContainerServiceImpl(BaseDao<NStorageContainer> baseDao) {
        super(baseDao);
    }

    private static Logger LOGGER = LogManager
            .getLogger(StorageContainerServiceImpl.class);

    private StorageContainerDao storageContainerDao;


    @Override
    public void delStorageContainerByContainerId(String containerId) throws StorageFault {
        try {
            NStorageContainer t = new NStorageContainer();
            t.setContainerId(containerId);
            t.setDeleted(true);
            t.setDeletedTime(new Timestamp(System.currentTimeMillis()));
            storageContainerDao.updateData(t);
            //storageContainerDao.delStorageContainerByContainerId(containerId);
        } catch (Exception e) {
            LOGGER.error("DelStorageContainerByContainerId error. vvolid : " + e);
            throw FaultUtil.storageFault("DelStorageContainerByContainerId error.");
        }
    }

    @Override
    public List<NStorageContainer> getStorageContainerByContainerName(String containerName) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            return storageContainerDao.getStorageContainerByContainerName(containerName);
        } catch (Exception e) {
            LOGGER.error("GetStorageContainerByContainerName error. vvolid : " + e);
            throw FaultUtil.storageFault("GetStorageContainerByContainerName error.");
        }
    }

    public StorageContainerDao getStorageContainerDao() {
        return storageContainerDao;
    }

    @Override
    public List<NStorageContainer> searchContainers(String[] containerIds) {
        // TODO Auto-generated method stub
        StorageContainerDao containerDao = (StorageContainerDao) baseDao;
        return containerDao.searchContainers(containerIds);
    }

    public void setStorageContainerDao(StorageContainerDao storageContainerDao) {
        this.storageContainerDao = storageContainerDao;
        setBaseDao(storageContainerDao);
    }

    @Override
    public NStorageContainer getStorageContainerByContainerId(String containerId) throws StorageFault {
        // TODO Auto-generated method stub
        return storageContainerDao.getStorageContainerByContainerId(containerId);
    }

    @Override
    public void deleteStorageContainerByContainerId(NStorageContainer nStorageContainer) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            storageContainerDao.deleteStorageContainerByContainerId(nStorageContainer);
            ;
        } catch (Exception e) {
            LOGGER.error("deleteStorageContainerByContainerId error. vvolid : " + e);
            throw FaultUtil.storageFault("GetStorageContainerByContainerName error.");
        }
    }

    @Override
    public List<NStorageContainer> getStorageContainerByPageSize(String offset, String pageSize, String pageIndex) throws StorageFault {
        try {
            LOGGER.info("In getStorageContainerByPageSize function,the offset = " + offset + " pageSize " + pageSize + " pageIndex=" + pageIndex);
            int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
            Map<String, String> map = new HashMap<String, String>();
            map.put("offSet", String.valueOf(offSet));
            map.put("pageSize", pageSize);
            return storageContainerDao.getStorageContainerByPageSize(map);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("deleteStorageContainerByContainerId error. vvolid : " + e);
            throw FaultUtil.storageFault("GetStorageContainerByContainerName error.");
        }
    }

    @Override
    public Long getStorageContainerCount() throws StorageFault {
        try {
            LOGGER.info("In getStorageContainerCount");
            return storageContainerDao.getStorageContainerCount();
        } catch (Exception e) {
            LOGGER.error("getStorageContainerCount error. vvolid : ", e);
            throw FaultUtil.storageFault("getStorageContainerCount error.");
        }
    }
}
