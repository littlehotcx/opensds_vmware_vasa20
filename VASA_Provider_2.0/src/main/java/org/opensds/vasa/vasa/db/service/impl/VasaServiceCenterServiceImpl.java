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
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.db.dao.VasaServiceCenterDao;
import org.opensds.vasa.vasa.db.model.NVasaServiceCenter;
import org.opensds.vasa.vasa.db.service.VasaServiceCenterService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VasaServiceCenterServiceImpl implements VasaServiceCenterService {
    private Logger LOGGER = LogManager
            .getLogger(VasaServiceCenterServiceImpl.class);

    private VasaServiceCenterDao vasaServiceCenterDao;

    public VasaServiceCenterDao getVasaServiceCenterDao() {
        return vasaServiceCenterDao;
    }

    public void setVasaServiceCenterDao(VasaServiceCenterDao vasaServiceCenterDao) {
        this.vasaServiceCenterDao = vasaServiceCenterDao;
    }

    @Override
    public List<NVasaServiceCenter> queryVasaService(String serviceIp) throws StorageFault {
        try {
            return vasaServiceCenterDao.queryVasaService(serviceIp);
        } catch (Exception e) {
            LOGGER.error("queryVasaService error.");
            throw FaultUtil.storageFault("queryVasaService error.");
        }
    }

    @Override
    public List<NVasaServiceCenter> queryCurrentMaster() throws StorageFault {
        try {
            return vasaServiceCenterDao.queryCurrentMaster();
        } catch (Exception e) {
            LOGGER.error("queryCurrentMaster error.");
            throw FaultUtil.storageFault("queryCurrentMaster error.");
        }
    }

    @Override
    public void competeMaster(String serviceIp) throws StorageFault {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put("serviceIp", serviceIp);
            params.put("modifyTime", DateUtil.getUTCDate());

            vasaServiceCenterDao.completeMaster(params);

        } catch (Exception e) {
            LOGGER.error("competeMaster error.");
            throw FaultUtil.storageFault("competeMaster error.");
        }

    }

    @Override
    public void updateMaster(String serviceIp) throws StorageFault {
        try {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put("serviceIp", serviceIp);
            params.put("modifyTime", DateUtil.getUTCDate());

            vasaServiceCenterDao.updateMaster(params);
        } catch (Exception e) {
            LOGGER.error("updateMaster error.");
            throw FaultUtil.storageFault("updateMaster error.");
        }

    }

    @Override
    public List<NVasaServiceCenter> queryAllService() {
        return vasaServiceCenterDao.queryAllService();
    }

    @Override
    public void addVasaService(NVasaServiceCenter center) {
        vasaServiceCenterDao.addVasaService(center);
    }

    @Override
    public List<NVasaServiceCenter> getVasaServiceByServiceIp(String serviceIp) {
        return vasaServiceCenterDao.getVasaServiceByServiceIp(serviceIp);
    }

}
