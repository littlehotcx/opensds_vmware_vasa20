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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.db.dao.VvolMetadataDao;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VvolMetadataServiceImpl implements VvolMetadataService {
    private static Logger LOGGER = LogManager
            .getLogger(VvolMetadataServiceImpl.class);

    private VvolMetadataDao vvolMetadataDao;

    public VvolMetadataDao getVvolMetadataDao() {
        return vvolMetadataDao;
    }

    public void setVvolMetadataDao(VvolMetadataDao vvolMetadataDao) {
        this.vvolMetadataDao = vvolMetadataDao;
    }

    @Override
    public List<NVvolMetadata> getAllReportVvolMetadata() throws StorageFault {
        try {
            List<String> availableStatuses = new ArrayList<String>();
            availableStatuses.add("available");
            availableStatuses.add("active");
            availableStatuses.add("creating");
            return vvolMetadataDao.getAllReportVvolMetadata(availableStatuses);
        } catch (Exception e) {
            LOGGER.error("getAllVvolMetadata error.");
            throw FaultUtil.storageFault("getAllVvolMetadata error.");
        }
    }

    @Override
    public List<NVvolMetadata> getVvolMetadataByVvolId(String vvolid) throws StorageFault {
        try {
            return vvolMetadataDao.getVvolMetadataByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("getVvolMetadataByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("getVvolMetadataByVvolId error.");
        }
    }

    @Override
    public int getCountByVvolIdAndKey(String vvolid, String key) throws StorageFault {
        try {
            NVvolMetadata vvolMetadata = new NVvolMetadata();
            vvolMetadata.setVvolid(vvolid);
            vvolMetadata.setKey(key);
            return vvolMetadataDao.getCountByVvolIdAndKey(vvolMetadata);
        } catch (Exception e) {
            LOGGER.error("getCountByVvolIdAndKey error. vvolid:" + vvolid + ", key:" + key);
            throw FaultUtil.storageFault("getCountByVvolIdAndKey error");
        }
    }

    @Override
    public void addVvolMetadata(NVvolMetadata vvolMetadata) throws StorageFault {
        try {
            vvolMetadataDao.addVvolMetadata(vvolMetadata);
        } catch (Exception e) {
            LOGGER.error("addVvolMetadata error. vvolMetadata:" + vvolMetadata);
            throw FaultUtil.storageFault("addVvolMetadata error.");
        }
    }

    @Override
    public void deleteVvolMetadataByVvolIdAndKey(String vvolid, String key) throws StorageFault {
        try {
            NVvolMetadata vvolMetadata = new NVvolMetadata();
            vvolMetadata.setVvolid(vvolid);
            vvolMetadata.setKey(key);
            vvolMetadataDao.deleteVvolMetadataByVvolIdAndKey(vvolMetadata);
        } catch (Exception e) {
            LOGGER.error("deleteVvolMetadataByVvolIdAndKey error. vvolid:" + vvolid + ", key:" + key);
            throw FaultUtil.storageFault("deleteVvolMetadataByVvolIdAndKey error.");
        }
    }

    @Override
    public void deleteVvolMetadataByVvolId(String vvolid) throws StorageFault {
        try {
            vvolMetadataDao.deleteVvolMetadataByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("deleteVvolMetadataByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("deleteVvolMetadataByVvolId error.");
        }
    }

    @Override
    public void updateVvolMetadataByVvolIdAndKey(String vvolid, String key,
                                                 String value) throws StorageFault {
        try {
            NVvolMetadata vvolMetadata = new NVvolMetadata();
            vvolMetadata.setVvolid(vvolid);
            vvolMetadata.setKey(key);
            vvolMetadata.setValue(value);
            vvolMetadataDao.updateVvolMetadataByVvolIdAndKey(vvolMetadata);
        } catch (Exception e) {
            LOGGER.error("updateVvolMetadataByVvolIdAndKey error. vvolid:" + vvolid +
                    ", key:" + key + ", value:" + value);
            throw FaultUtil.storageFault("updateVvolMetadataByVvolIdAndKey error.");
        }
    }

    @Override
    public String getvmNameByVvolId(String vvolid) throws StorageFault {
        try {
            return vvolMetadataDao.getvmNameByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("getvmNameByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("getvmNameByVvolId error.");
        }
    }

}
