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

import org.opensds.vasa.vasa.db.dao.EsxHostIpDao;
import org.opensds.vasa.vasa.db.model.EsxHostIp;
import org.opensds.vasa.vasa.db.service.EsxHostIpService;

public class EsxHostIpServiceImpl implements EsxHostIpService {

    EsxHostIpDao esxHostIpDao;

    public EsxHostIpDao getEsxHostIpDao() {
        return esxHostIpDao;
    }

    public void setEsxHostIpDao(EsxHostIpDao esxHostIpDao) {
        this.esxHostIpDao = esxHostIpDao;
    }

    @Override
    public void insertRecord(EsxHostIp host) {
        esxHostIpDao.insertRecord(host);
    }

    @Override
    public EsxHostIp queryRecordByHostIdAndIp(EsxHostIp host) {
        return esxHostIpDao.queryRecordByHostIdAndIp(host);
    }

    @Override
    public void deleteRecord(EsxHostIp host) {
        esxHostIpDao.deleteRecord(host);
    }

    @Override
    public List<EsxHostIp> queryEsxHostIpByHostId(String hostId) {
        return esxHostIpDao.queryEsxHostIpByHostId(hostId);
    }

}
