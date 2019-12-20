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
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.db.dao.VvolProfileDao;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.service.VvolProfileService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VvolProfileServiceImpl implements VvolProfileService {
    private static Logger LOGGER = LogManager
            .getLogger(VvolProfileServiceImpl.class);

    private VvolProfileDao vvolProfileDao;

    public VvolProfileDao getVvolProfileDao() {
        return vvolProfileDao;
    }

    public void setVvolProfileDao(VvolProfileDao vvolProfileDao) {
        this.vvolProfileDao = vvolProfileDao;
    }

    @Override
    public List<NVvolProfile> getVvolProfileByVvolId(String vvolid) throws StorageFault {
        try {
            return vvolProfileDao.getVvolProfileByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("getVvolProfileByVvolId error. vvolid:" + vvolid, e);
            throw FaultUtil.storageFault("getVvolProfileByVvolId error.");
        }
    }

    @Override
    public void addVvolProfile(NVvolProfile vvolProfile) throws StorageFault {
        try {
            vvolProfileDao.addVvolProfile(vvolProfile);
        } catch (Exception e) {
            LOGGER.error("addVvolProfile error. vvolProfile:" + vvolProfile);
            throw FaultUtil.storageFault("addVvolProfile error.");
        }
    }

    @Override
    public void deleteVvolProfileByVvolId(String vvolid) throws StorageFault {
        try {
            vvolProfileDao.deleteVvolProfileByVvolId(vvolid);
        } catch (Exception e) {
            LOGGER.error("deleteVvolProfileByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("deleteVvolProfileByVvolId error.");
        }

    }

    @Override
    public void deleteVvolProfileByVvolIdExceptCap(String vvolid) throws StorageFault {
        try {
            vvolProfileDao.deleteVvolProfileByVvolIdExceptCap(vvolid);
        } catch (Exception e) {
            LOGGER.error("deleteVvolProfileByVvolIdExceptCap error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("deleteVvolProfileByVvolIdExceptCap error.");
        }

    }

    @Override
    public int getVvolNumByProfileIdAndGenerationId(String profileId, long generationId) throws StorageFault {
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("profileId", profileId);
            paramMap.put("generationId", generationId);
            return vvolProfileDao.getVvolNumByProfileIdAndGenerationId(paramMap);
        } catch (Exception e) {
            LOGGER.error("getVvolNumByProfileIdAndGenerationId error. profileId: " + profileId);
            throw FaultUtil.storageFault("getVvolNumByProfileIdAndGenerationId error.");
        }

    }

    @Override
    public NVvolProfile getThinThickVvolProfile(String vvolid) throws StorageFault {
        try {
            return vvolProfileDao.getThinThickVvolProfile(vvolid);
        } catch (Exception e) {
            LOGGER.error("getVvolProfileByVvolId error. vvolid:" + vvolid);
            throw FaultUtil.storageFault("getVvolProfileByVvolId error.");
        }
    }

    @Override
    public int getVvolNumByProfileId(String profileId) throws StorageFault {
        try {
            return vvolProfileDao.getVvolNumByProfileId(profileId);
        } catch (Exception e) {
            LOGGER.error("getVvolNumByProfileId error. profileId: " + profileId);
            throw FaultUtil.storageFault("getVvolNumByProfileId error.");
        }
    }


}
