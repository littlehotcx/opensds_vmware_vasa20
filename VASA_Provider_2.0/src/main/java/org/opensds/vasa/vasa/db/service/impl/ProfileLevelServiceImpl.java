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

import org.opensds.vasa.vasa.db.dao.BaseDao;
import org.opensds.vasa.vasa.db.dao.ProfileLevelDao;
import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.service.ProfileLevelService;

public class ProfileLevelServiceImpl extends BaseServiceImpl<NProfileLevel> implements ProfileLevelService {

    public ProfileLevelServiceImpl(BaseDao<NProfileLevel> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void deleteById(String id) {
        // TODO Auto-generated method stub
        NProfileLevel profileLevel = new NProfileLevel();
        profileLevel.setProfileLevelId(id);
        this.baseDao.delete(profileLevel);
    }

    @Override
    public NProfileLevel getById(String id) {
        // TODO Auto-generated method stub
        NProfileLevel profileLevel = new NProfileLevel();
        profileLevel.setProfileLevelId(id);
        return baseDao.getDataByKey(profileLevel);
    }

    @Override
    public int getConfiguredLevelCount(String level) {
        return ((ProfileLevelDao) baseDao).getConfiguredLevelCount(level);
    }
}
