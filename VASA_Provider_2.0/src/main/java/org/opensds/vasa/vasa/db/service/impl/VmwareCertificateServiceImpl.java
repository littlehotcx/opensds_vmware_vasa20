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
import org.opensds.vasa.vasa.db.dao.VmwareCertificateDao;
import org.opensds.vasa.vasa.db.model.NVmwareCertificate;
import org.opensds.vasa.vasa.db.service.VmwareCertificateService;

import com.vmware.vim.vasa.v20.StorageFault;

public class VmwareCertificateServiceImpl implements VmwareCertificateService {
    private static Logger LOGGER = LogManager.getLogger(VmwareCertificateServiceImpl.class);

    private VmwareCertificateDao vmwareCertificateDao;


    public VmwareCertificateDao getVmwareCertificateDao() {
        return vmwareCertificateDao;
    }

    public void setVmwareCertificateDao(VmwareCertificateDao vmwareCertificateDao) {
        this.vmwareCertificateDao = vmwareCertificateDao;
    }

    @Override
    public void addVmwareCertificate(NVmwareCertificate certificate) throws StorageFault {
        try {
            vmwareCertificateDao.delCert2SyncResult();
            vmwareCertificateDao.addVmwareCertificate(certificate);
        } catch (Exception e) {
            LOGGER.error("addVmwareCertificate error. NVmwareCertificate:" + certificate);
            throw FaultUtil.storageFault("addVmwareCertificate error.");
        }
    }

    @Override
    public List<NVmwareCertificate> queryNeedSyncCerts(String syncIp) throws StorageFault {
        try {
            return vmwareCertificateDao.queryNeedSyncCerts(syncIp);
        } catch (Exception e) {
            LOGGER.error("queryNeedSyncCerts error.");
            throw FaultUtil.storageFault("queryNeedSyncCerts error.");
        }
    }


}
