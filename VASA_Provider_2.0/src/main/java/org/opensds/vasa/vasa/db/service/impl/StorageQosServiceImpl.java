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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.StorageQosDao;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.service.StorageQosService;

import com.vmware.vim.vasa.v20.StorageFault;

public class StorageQosServiceImpl extends BaseServiceImpl<NStorageQos> implements StorageQosService {
    public StorageQosServiceImpl(BaseDao<NStorageQos> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    private static Logger LOGGER = LogManager
            .getLogger(StorageQosServiceImpl.class);

    private StorageQosDao storageQosDao;

    @Override
    public NStorageQos getStorageQosByQosId(String qosId) throws StorageFault {
        try {
            return storageQosDao.getStorageQosByQosId(qosId);
        } catch (Exception e) {
            LOGGER.error("GetStorageProfileByProfileId error. vvolid : " + e);
            throw FaultUtil.storageFault("GetStorageProfileByProfileId error.");
        }
    }

    @Override
    public void updateStorageQosByQosId(NStorageQos qos) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            storageQosDao.updateStorageQosByQosId(qos);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("UpdateStorageQosByQosId error. vvolid : " + e);
            throw FaultUtil.storageFault("UpdateStorageQosByQosId error.");
        }
    }

    public StorageQosDao getStorageQosDao() {
        return storageQosDao;
    }

    public void setStorageQosDao(StorageQosDao storageQosDao) {
        this.storageQosDao = storageQosDao;
    }

    @Override
    public NStorageQos queryQosById(String qosId) {
        // TODO Auto-generated method stub
        NStorageQos nStorageQos = new NStorageQos();
        nStorageQos.setId(qosId);
        return baseDao.getDataByKey(nStorageQos);
    }

    @Override
    public void deleteStorageQosByQosId(NStorageQos qos) throws StorageFault {
        // TODO Auto-generated method stub
        try {
            storageQosDao.deleteStorageQosByQosId(qos);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("deleteStorageQosByQosId error. vvolid : " + e);
            throw FaultUtil.storageFault("deleteStorageQosByQosId error.");
        }
    }

    @Override
    public void deleteStorageQosById(String id) throws StorageFault {
        // TODO Auto-generated method stub
        NStorageQos nStorageQos = new NStorageQos();
        nStorageQos.setId(id);
        nStorageQos.setDeletedTime(new Date());
        this.deleteStorageQosByQosId(nStorageQos);
    }

}
