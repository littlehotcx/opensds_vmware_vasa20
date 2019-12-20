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
import java.util.List;

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.model.BaseData;
import org.opensds.vasa.vasa.db.service.BaseService;

public class BaseServiceImpl<T extends BaseData> implements BaseService<T> {

    public BaseDao<T> baseDao;

    public BaseServiceImpl(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public List<T> getAll() {
        // TODO Auto-generated method stub
        return baseDao.getAll();
    }

    @Override
    public List<T> search(T t) {
        // TODO Auto-generated method stub
        return baseDao.search(t);
    }

    @Override
    public T getDataByKey(T t) {
        // TODO Auto-generated method stub
        return baseDao.getDataByKey(t);
    }

    @Override
    public void delete(T t) {
        // TODO Auto-generated method stub
        baseDao.delete(t);
    }

    @Override
    public void updateData(T t) {
        // TODO Auto-generated method stub
        t.setUpdatedTime(new Date());
        baseDao.updateData(t);
    }

    public BaseDao<T> getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public void save(T t) {
        // TODO Auto-generated method stub
        t.setCreatedTime(new Date());
        baseDao.save(t);
    }


}
