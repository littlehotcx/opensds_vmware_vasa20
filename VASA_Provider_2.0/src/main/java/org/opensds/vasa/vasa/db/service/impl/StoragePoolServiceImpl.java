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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa.util.FaultUtil;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.StoragePoolDao;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.service.StoragePoolService;

import com.vmware.vim.vasa.v20.StorageFault;

public class StoragePoolServiceImpl extends BaseServiceImpl<NStoragePool> implements StoragePoolService {
    public StoragePoolServiceImpl(BaseDao<NStoragePool> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    private StoragePoolDao storagePoolDao;
    private Logger LOGGER = LogManager.getLogger(StoragePoolServiceImpl.class);

    public StoragePoolDao getStoragePoolDao() {
        return storagePoolDao;
    }

    public void setStoragePoolDao(StoragePoolDao storagePoolDao) {
        this.storagePoolDao = storagePoolDao;
        this.baseDao = storagePoolDao;
    }

    @Override
    public List<NStoragePool> queryStoragePoolByContainerId(String containerId) {
        // TODO Auto-generated method stub
        return storagePoolDao.queryStoragePoolByContainerId(containerId);
    }

    @Override
    public List<NStoragePool> queryStoragePoolByArrayId(String arrayId) {
        // TODO Auto-generated method stub
        return storagePoolDao.queryStoragePoolByArrayId(arrayId);
    }

    @Override
    public List<NStoragePool> getUnbindStoragePoolPageByArrayId(String arrayId, String pageIndex, String pageSize) {
        // TODO Auto-generated method stub

        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        Map<String, String> map = new HashMap<String, String>();
        map.put("arrayId", arrayId);
        map.put("offSet", String.valueOf(offSet));
        map.put("pageSize", pageSize);

        List<NStoragePool> unbindStoragePoolByArrayId = storagePoolDao.getUnbindStoragePoolPageByArrayId(map);

        return unbindStoragePoolByArrayId;
    }

    @Override
    public void updatePool(S2DStoragePool s2dStoragePool, String arrayId) {
        // TODO Auto-generated method stub

        NStoragePool t = new NStoragePool();
        t.setArrayId(arrayId);
        t.setRawPoolId(s2dStoragePool.getID());

        Long freeCapacity = Long.valueOf(s2dStoragePool.getFreeCapacity()) / 2 * 1024;
        t.setFreeCapacity(freeCapacity);
        Long totalCapacity = Long.valueOf(s2dStoragePool.getTotalCapacity()) / 2 * 1024;
        t.setTotalCapacity(totalCapacity);
        t.setUpdatedTime(new Date());
        t.setRaidLevel(s2dStoragePool.getTier0RaidLv());
        String runningstatus = s2dStoragePool.getRUNNINGSTATUS();
        if (runningstatus.equalsIgnoreCase("27")) {
            t.setDeviceStatus("ONLINE");
        } else {
            t.setDeviceStatus("OFFLINE");
        }
        storagePoolDao.updateData(t);

    }

    @Override
    public void saveStorageData(S2DStoragePool s2dStoragePool, String arrayId) {
        // TODO Auto-generated method stub
        NStoragePool t = new NStoragePool();
        t.setArrayId(arrayId);
        t.setCapacityThreshold(Long.valueOf(s2dStoragePool.getConsumedCapacityThreshold()));
        t.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        t.setDeleted(false);
        t.setDescription(s2dStoragePool.getDESCRIPTION());
        String runningstatus = s2dStoragePool.getRUNNINGSTATUS();
        if (runningstatus.equalsIgnoreCase("27")) {
            t.setDeviceStatus("ONLINE");
        } else {
            t.setDeviceStatus("OFFLINE");
        }
        Long freeCapacity = Long.valueOf(s2dStoragePool.getFreeCapacity()) / 2 * 1024;
        t.setFreeCapacity(freeCapacity);
        Long totalCapacity = Long.valueOf(s2dStoragePool.getTotalCapacity()) / 2 * 1024;
        t.setTotalCapacity(totalCapacity);
        t.setName(s2dStoragePool.getNAME());
        t.setParentId(s2dStoragePool.getPARENTID());
        t.setParentName(s2dStoragePool.getPARENTNAME());
        t.setRaidLevel(s2dStoragePool.getTier0RaidLv());
        t.setRawPoolId(s2dStoragePool.getID());
        t.setDeletedTime(new Timestamp(System.currentTimeMillis()));
        LOGGER.info("begin to saveStorageData, NStoragePool = " + t);
        storagePoolDao.save(t);
    }

    @Override
    public int getUnbindStoragePoolSizeByArrayId(String arrayId) {
        // TODO Auto-generated method stub
        return storagePoolDao.getUnbindStoragePoolSizeByArrayId(arrayId);
    }

    /*
    @Override
    public void bindStoragePools(String arrayId, String containerId,
            String[] pools) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<>();
        map.put("containerId", containerId);
        map.put("arrayId", arrayId);
        map.put("pools", pools);
        storagePoolDao.bindStoragePools(map);
    }
    */
    @Override
    public void unbindStoragePools(String arrayId, String containerId,
                                   String[] pools) {
        Map<String, Object> map = new HashMap<>();
        map.put("arrayId", arrayId);
        map.put("containerId", containerId);
        map.put("pools", pools);
        storagePoolDao.deleteStoragePoolsByPoolIds(map);
		/*
		Map<String, Object> map = new HashMap<>();
		map.put("arrayId", arrayId);
		map.put("containerId", containerId);
		map.put("pools", pools);
		storagePoolDao.unbindStoragePools(map);
		*/
    }

    @Override
    public void setStoragePoolsLost(String arrayId, String containerId,
                                    String[] pools) {
        Map<String, Object> map = new HashMap<>();
        map.put("arrayId", arrayId);
        map.put("containerId", containerId);
        map.put("pools", pools);
        storagePoolDao.setStoragePoolsLost(map);
    }

    @Override
    public List<NStoragePool> getPoolListByContainerId(String containerId) throws StorageFault {
        // TODO Auto-generated method stub
        try {

            NStoragePool nStoragePool = new NStoragePool();
            nStoragePool.setContainerId(containerId);
            nStoragePool.setDeleted(false);
            List<NStoragePool> search = this.search(nStoragePool);
            return search;
        } catch (Exception e) {
            LOGGER.error("getPoolListByContainerId error. containerId:"
                    + containerId);
            throw FaultUtil
                    .storageFault("getPoolListByContainerId. containerId:"
                            + containerId);
        }
    }

    @Override
    public synchronized boolean descStoragePoolSize(String rawPoolId, long freeCapacity) {
        NStoragePool t = new NStoragePool();
        t.setRawPoolId(rawPoolId);
        NStoragePool dataByKey = super.getDataByKey(t);
        long freeSize = dataByKey.getFreeCapacity() - freeCapacity;
        if (freeSize >= 0) {
            NStoragePool updateObject = new NStoragePool();
            updateObject.setRawPoolId(rawPoolId);
            updateObject.setFreeCapacity(freeSize);
            storagePoolDao.updateData(updateObject);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean incStoragePoolSize(String rawPoolId, long freeCapacity) {
        NStoragePool t = new NStoragePool();
        t.setRawPoolId(rawPoolId);
        NStoragePool dataByKey = super.getDataByKey(t);
        long freeSize = dataByKey.getFreeCapacity() + freeCapacity;
        NStoragePool updateObject = new NStoragePool();
        updateObject.setRawPoolId(rawPoolId);
        updateObject.setFreeCapacity(freeSize);
        storagePoolDao.updateData(updateObject);
        return true;
    }

    @Override
    public List<NStoragePool> queryStoragePoolByArrayIdAndPageSize(String arrayId, String pageIndex, String pageSize) {
        // TODO Auto-generated method stub
        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        Map<String, String> map = new HashMap<String, String>();
        map.put("arrayId", arrayId);
        map.put("offSet", String.valueOf(offSet));
        map.put("pageSize", pageSize);
        return storagePoolDao.getUnbindStoragePoolPageByArrayId(map);
    }

    @Override
    public List<NStoragePool> queryStoragePoolByContainerIdAndPageSize(String containerId, String pageIndex, String pageSize) {
        // TODO Auto-generated method stub
        LOGGER.info("In queryStoragePoolByContainerIdAndPageSize,the pageIndex=" + pageIndex + " pageSize=" + pageSize);
        if (Integer.valueOf(pageIndex) < 1) {
            pageIndex = "1";
        }
        int offSet = Integer.valueOf(pageSize) * (Integer.valueOf(pageIndex) - 1);
        Map<String, String> map = new HashMap<String, String>();
        map.put("containerId", containerId);
        map.put("offSet", String.valueOf(offSet));
        map.put("pageSize", pageSize);
        return storagePoolDao.queryStoragePoolByContainerIdAndPageSize(map);
    }

    @Override
    public List<NStoragePool> getAllBindStoragePoolByArrayId(String arrayId) {
        // TODO Auto-generated method stub
        return storagePoolDao.getAllBindStoragePoolByArrayId(arrayId);
    }

    @Override
    public void bindStoragePools(NStoragePool storagePool) {
        storagePoolDao.save(storagePool);
    }
}
