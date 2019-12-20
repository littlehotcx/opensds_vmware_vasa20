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

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.StorageCapabilityQosDao;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.service.StorageCapabilityQosService;

import com.vmware.vim.vasa.v20.StorageFault;

public class StorageCapabilityQosServiceImpl extends BaseServiceImpl<NStorageCapabilityQos> implements StorageCapabilityQosService {

    private StorageCapabilityQosDao capabilityQosDao;

    public StorageCapabilityQosServiceImpl(BaseDao<NStorageCapabilityQos> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
        this.setCapabilityQosDao((StorageCapabilityQosDao) baseDao);
    }

    public StorageCapabilityQosDao getCapabilityQosDao() {
        return capabilityQosDao;
    }

    public void setCapabilityQosDao(StorageCapabilityQosDao capabilityQosDao) {
        this.capabilityQosDao = capabilityQosDao;
    }

    @Override
    public void deleteStorageCapabilityQosById(String id) throws StorageFault {
        // TODO Auto-generated method stub
        NStorageCapabilityQos nStorageCapabilityQos = new NStorageCapabilityQos();
        nStorageCapabilityQos.setId(id);
        nStorageCapabilityQos.setDeletedTime(new Date());
        capabilityQosDao.delete(nStorageCapabilityQos);
    }

    @Override
    public NStorageCapabilityQos getStorageCapabilityQosById(String id) throws StorageFault {
        // TODO Auto-generated method stub
        NStorageCapabilityQos nStorageCapabilityQos = new NStorageCapabilityQos();
        nStorageCapabilityQos.setId(id);
        nStorageCapabilityQos.setDeletedTime(new Date());
        return capabilityQosDao.getDataByKey(nStorageCapabilityQos);
    }

}
