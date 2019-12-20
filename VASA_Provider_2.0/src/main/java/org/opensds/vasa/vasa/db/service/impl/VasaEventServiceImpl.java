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
import org.opensds.vasa.vasa.db.dao.VasaEventDao;
import org.opensds.vasa.vasa.db.model.NVasaEvent;
import org.opensds.vasa.vasa.db.service.VasaEventService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VasaEventServiceImpl implements VasaEventService {
    private static Logger LOGGER = LogManager
            .getLogger(VasaEventServiceImpl.class);

    private VasaEventDao vasaEventDao;

    public VasaEventDao getVasaEventDao() {
        return vasaEventDao;
    }

    public void setVasaEventDao(VasaEventDao vasaEventDao) {
        this.vasaEventDao = vasaEventDao;
    }

    @Override
    public NVasaEvent getEventByArrayId(String arrayId) throws StorageFault {
        try {
            return vasaEventDao.getEventByArrayId(arrayId);
        } catch (Exception e) {
            LOGGER.error("getEventByArrayId error.");
            throw FaultUtil.storageFault("getEventByArrayId error.");
        }
    }

    @Override
    public List<String> getAllArrayIds() throws StorageFault {
        try {
            return vasaEventDao.getAllArrayIds();
        } catch (Exception e) {
            LOGGER.error("getAllArrayIds error.");
            throw FaultUtil.storageFault("getAllArrayIds error.");
        }
    }

    @Override
    public void addEvent(NVasaEvent event) throws StorageFault {
        try {
            vasaEventDao.addEvent(event);
        } catch (Exception e) {
            LOGGER.error("addEvent error.");
            throw FaultUtil.storageFault("addEvent error.");
        }
    }

    @Override
    public void updateEventByArrayId(NVasaEvent event) throws StorageFault {
        try {
            vasaEventDao.updateEventByArrayId(event);
        } catch (Exception e) {
            LOGGER.error("updateEventByArrayId error.");
            throw FaultUtil.storageFault("updateEventByArrayId error.");
        }
    }

    @Override
    public void deleteVasaEventByArrayId(String arrayId) throws StorageFault {
        try {
            vasaEventDao.deleteVasaEventByArrayId(arrayId);
        } catch (Exception e) {
            LOGGER.error("deleteVasaEventByArrayId error.");
            throw FaultUtil.storageFault("deleteVasaEventByArrayId error.");
        }
    }

}
