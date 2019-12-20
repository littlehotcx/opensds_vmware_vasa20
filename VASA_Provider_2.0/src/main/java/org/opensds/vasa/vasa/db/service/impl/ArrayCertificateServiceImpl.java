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

import org.opensds.vasa.vasa.db.dao.ArrayCertificateDao;
import org.opensds.vasa.vasa.db.model.NArrayCertificate;
import org.opensds.vasa.vasa.db.model.NMultiVcCertificate;
import org.opensds.vasa.vasa.db.service.ArrayCertificateService;

public class ArrayCertificateServiceImpl implements ArrayCertificateService {

    private ArrayCertificateDao arrayCertificateDao;


    @Override
    public void save(NArrayCertificate arrayCertificate) {
        // TODO Auto-generated method stub
        arrayCertificateDao.save(arrayCertificate);
    }

    @Override
    public List<NArrayCertificate> getall() {
        // TODO Auto-generated method stub
        return arrayCertificateDao.getall();
    }

    @Override
    public void delete(int id) {
        // TODO Auto-generated method stub
        arrayCertificateDao.delete(id);
    }

    @Override
    public NArrayCertificate getByArrayId(String arrayid) {
        return arrayCertificateDao.getByArrayId(arrayid);
    }

    @Override
    public NArrayCertificate getByCacontent(byte[] contentByte) {
        return arrayCertificateDao.getByCacontent(contentByte);
    }

    public ArrayCertificateDao getArrayCertificateDao() {
        return arrayCertificateDao;
    }

    public void setArrayCertificateDao(ArrayCertificateDao arrayCertificateDao) {
        this.arrayCertificateDao = arrayCertificateDao;
    }

    @Override
    public List<NArrayCertificate> queryNeedSyncArrayCerts(String syncIp) {
        // TODO Auto-generated method stub
        return arrayCertificateDao.queryNeedSyncArrayCerts(syncIp);
    }

    @Override
    public void saveMultiVcCertificate(NMultiVcCertificate nMultiVcCertificate) {
        // TODO Auto-generated method stub
        arrayCertificateDao.saveMultiVcCertificate(nMultiVcCertificate);
    }

    @Override
    public NMultiVcCertificate getMultiVcCertificate() {
        // TODO Auto-generated method stub
        return arrayCertificateDao.getMultiVcCertificate();
    }
}
