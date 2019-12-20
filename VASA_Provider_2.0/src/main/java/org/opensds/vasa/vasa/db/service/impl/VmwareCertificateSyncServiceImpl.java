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

import org.opensds.vasa.vasa.db.dao.VmwareCertificateSyncDao;
import org.opensds.vasa.vasa.db.model.NVmwareCertificateSync;
import org.opensds.vasa.vasa.db.service.VmwareCertificateSyncService;
import org.opensds.vasa.vasa.util.FaultUtil;

import com.vmware.vim.vasa.v20.StorageFault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VmwareCertificateSyncServiceImpl implements VmwareCertificateSyncService {
    private static Logger LOGGER = LogManager.getLogger(VmwareCertificateSyncServiceImpl.class);

    private VmwareCertificateSyncDao vmwareCertificateSyncDao;


    public VmwareCertificateSyncDao getVmwareCertificateSyncDao() {
        return vmwareCertificateSyncDao;
    }

    public void setVmwareCertificateSyncDao(VmwareCertificateSyncDao vmwareCertificateSyncDao) {
        this.vmwareCertificateSyncDao = vmwareCertificateSyncDao;
    }


    @Override
    public void addVmwareCertificateSync(NVmwareCertificateSync certificateSync) throws StorageFault {
        try {
            vmwareCertificateSyncDao.addVmwareCertificateSync(certificateSync);
        } catch (Exception e) {
            LOGGER.error("addVmwareCertificateSync error. NVmwareCertificateSync:" + certificateSync);
            throw FaultUtil.storageFault("addVmwareCertificateSync error.");
        }
    }


}
