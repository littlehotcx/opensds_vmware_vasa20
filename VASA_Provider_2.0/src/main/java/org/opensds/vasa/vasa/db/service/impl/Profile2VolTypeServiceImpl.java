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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.db.dao.Profile2VolTypeDao;
import org.opensds.vasa.vasa.db.model.NProfile2VolType;
import org.opensds.vasa.vasa.db.service.Profile2VolTypeService;

import com.vmware.vim.vasa.v20.StorageFault;

public class Profile2VolTypeServiceImpl implements Profile2VolTypeService {
    private static Logger LOGGER = LogManager
            .getLogger(Profile2VolTypeServiceImpl.class);

    private Profile2VolTypeDao profile2VolTypeDao;

    public Profile2VolTypeDao getProfile2VolTypeDao() {
        return profile2VolTypeDao;
    }

    public void setProfile2VolTypeDao(Profile2VolTypeDao profile2VolTypeDao) {
        this.profile2VolTypeDao = profile2VolTypeDao;
    }

    @Override
    public NProfile2VolType getProfile2VolTypeByProfileId(String profileId, String containerId, String thinThick, long generationId, String deprecated) {
        LOGGER.info("profileId: " + profileId + " containerId: " + containerId + " thinOrThick: " + thinThick + " generationId:" + generationId);
        Map<String, Object> profile2VolType = new HashMap<String, Object>();
        profile2VolType.put("profileId", profileId);
        profile2VolType.put("containerId", containerId);
        profile2VolType.put("thinThick", thinThick);
        profile2VolType.put("generationId", generationId);
        profile2VolType.put("deprecated", deprecated);

        NProfile2VolType profile2VolType2 = new NProfile2VolType();
        profile2VolType2 = profile2VolTypeDao.getProfile2VolTypeByProfileId(profile2VolType);
        LOGGER.info("Get profile2VolType: " + profile2VolType2);
        return profile2VolType2;
    }

    @Override
    public void delProfile2VolType(String profileId, long generationId, String containerId, String thinThick) throws StorageFault {
        try {
            Map<String, Object> delParamMap = new HashMap<String, Object>();
            delParamMap.put("profileId", profileId);
            delParamMap.put("generationId", generationId);
            delParamMap.put("containerId", containerId);
            delParamMap.put("thinThick", thinThick);
            profile2VolTypeDao.delProfile2VolType(delParamMap);
        } catch (Exception e) {
            LOGGER.error("delProfile2VolType error. vvolid: " + profileId);
            throw FaultUtil.storageFault("delProfile2VolType error.");
        }

    }

    @Override
    public void updateLastUseTime(String profileId, Date date, long generationId) throws StorageFault {
        NProfile2VolType profile2VolType = new NProfile2VolType();
        profile2VolType.SetProfileId(profileId);
        profile2VolType.setLastUseTime(date);
        profile2VolType.setGenerationId(generationId);
        try {
            profile2VolTypeDao.updateLastUseTime(profile2VolType);
        } catch (Exception e) {
            LOGGER.error("updateLastUseTime error. vvolid: " + profileId);
            throw FaultUtil.storageFault("updateLastUseTime error.");
        }
    }

    @Override
    public void insertProfile2VolType(NProfile2VolType profile2VolType) throws StorageFault {
        try {
            LOGGER.info("Insert profile2VolType to DB, profile2VolType: " + profile2VolType);
            profile2VolTypeDao.insertProfile2VolType(profile2VolType);
        } catch (Exception e) {
            LOGGER.error("insertProfile2VolType error. profile2VolType: " + profile2VolType);
            throw FaultUtil.storageFault("insertProfile2VolType error.");
        }
    }

    @Override
    public List<NProfile2VolType> getAllProfile2VolType() throws StorageFault {
        try {
            return profile2VolTypeDao.getAllProfile2VolType();
        } catch (Exception e) {
            LOGGER.error("getAllProfile2VolType error.");
            throw FaultUtil.storageFault("getAllProfile2VolType error.");
        }
    }

    @Override
    public void updateDeprecated(String containerId, String deprecated) throws StorageFault {
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("containerId", containerId);
            paramMap.put("deprecated", deprecated);
            profile2VolTypeDao.updateDeprecated(paramMap);
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.error("updateDeprecated error. containerId: " + containerId + " deprecated: " + deprecated);
            throw FaultUtil.storageFault("updateDeprecated error.");
        }
    }
}
