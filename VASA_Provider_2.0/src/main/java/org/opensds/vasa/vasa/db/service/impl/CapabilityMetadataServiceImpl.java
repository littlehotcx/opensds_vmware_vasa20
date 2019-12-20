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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.db.dao.CapabilityMetadataDao;
import org.opensds.vasa.vasa.db.model.NCapabilityMetadata;
import org.opensds.vasa.vasa.db.service.CapabilityMetadataService;

import com.vmware.vim.vasa.v20.StorageFault;

public class CapabilityMetadataServiceImpl implements CapabilityMetadataService {
    private static Logger LOGGER = LogManager
            .getLogger(CapabilityMetadataServiceImpl.class);

    private CapabilityMetadataDao capabilityMetadataDao;

    public CapabilityMetadataDao getCapabilityMetadataDao() {
        return capabilityMetadataDao;
    }

    public void setCapabilityMetadataDao(CapabilityMetadataDao capabilityMetadataDao) {
        this.capabilityMetadataDao = capabilityMetadataDao;
    }

    @Override
    public List<NCapabilityMetadata> getAllCapabilityMetadata() throws StorageFault {
        try {
            return capabilityMetadataDao.getAllCapabilityMetadata();
        } catch (Exception e) {
            LOGGER.error("getAllCapabilityMetadata error.");
            throw FaultUtil.storageFault("getAllCapabilityMetadata error.");
        }
    }

    @Override
    public List<String> getAllMetadataCategory() throws StorageFault {
        try {
            return capabilityMetadataDao.getAllMetadataCategory();
        } catch (Exception e) {
            LOGGER.error("getAllMetadataCategory error.");
            throw FaultUtil.storageFault("getAllMetadataCategory error.");
        }
    }

    @Override
    public List<String> getAllMetadataCapabilityId() throws StorageFault {
        try {
            return capabilityMetadataDao.getAllMetadataCapabilityId();
        } catch (Exception e) {
            LOGGER.error("getAllMetadataCapabilityId error.");
            throw FaultUtil.storageFault("getAllMetadataCapabilityId error.");
        }
    }

    @Override
    public List<NCapabilityMetadata> getCapabilityMetadataByCategory(String category) throws StorageFault {
        try {
            return capabilityMetadataDao.getCapabilityMetadataByCategory(category);
        } catch (Exception e) {
            LOGGER.error("getCapabilityMetadataByCategory error.");
            throw FaultUtil.storageFault("getCapabilityMetadataByCategory error.");
        }
    }

}
