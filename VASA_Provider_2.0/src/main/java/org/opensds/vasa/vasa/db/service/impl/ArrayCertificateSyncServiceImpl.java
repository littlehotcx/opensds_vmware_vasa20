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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.vasa.vasa.db.dao.ArrayCertificateSyncDao;
import org.opensds.vasa.vasa.db.model.NArrayCertificateSync;
import org.opensds.vasa.vasa.db.service.ArrayCertificateSyncService;

public class ArrayCertificateSyncServiceImpl implements ArrayCertificateSyncService {

    private static Logger LOGGER = LogManager.getLogger(ArrayCertificateSyncServiceImpl.class);

    private ArrayCertificateSyncDao arrayCertificateSyncDao;

    public ArrayCertificateSyncDao getArrayCertificateSyncDao() {
        return arrayCertificateSyncDao;
    }

    public void setArrayCertificateSyncDao(ArrayCertificateSyncDao arrayCertificateSyncDao) {
        this.arrayCertificateSyncDao = arrayCertificateSyncDao;
    }

    @Override
    public void addArrayCertificateSync(NArrayCertificateSync certificateSync) {
        arrayCertificateSyncDao.addArrayCertificateSync(certificateSync);
    }
}
