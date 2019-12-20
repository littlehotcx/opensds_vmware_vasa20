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
import org.opensds.vasa.vasa.db.dao.DefaultProfileDao;
import org.opensds.vasa.vasa.db.model.NDefaultProfile;
import org.opensds.vasa.vasa.db.service.DefaultProfileService;

import com.vmware.vim.vasa.v20.StorageFault;

public class DefaultProfileServiceImpl implements DefaultProfileService {
    private static Logger LOGGER = LogManager
            .getLogger(DefaultProfileServiceImpl.class);

    private DefaultProfileDao defaultProfileDao;

    public DefaultProfileDao getDefaultProfileDao() {
        return defaultProfileDao;
    }

    public void setDefaultProfileDao(DefaultProfileDao defaultProfileDao) {
        this.defaultProfileDao = defaultProfileDao;
    }

    @Override
    public List<NDefaultProfile> getAllDefaultProfile() throws StorageFault {
        try {
            return defaultProfileDao.getAllDefaultProfile();
        } catch (Exception e) {
            LOGGER.error("getAllDefaultProfile error.");
            throw FaultUtil.storageFault("getAllDefaultProfile error.");
        }
    }

}
