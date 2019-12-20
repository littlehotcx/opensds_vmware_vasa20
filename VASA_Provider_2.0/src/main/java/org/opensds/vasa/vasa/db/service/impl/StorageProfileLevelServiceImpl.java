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
import org.opensds.vasa.vasa.db.dao.StorageProfileLevelDao;
import org.opensds.vasa.vasa.db.model.NStorageProfileLevel;
import org.opensds.vasa.vasa.db.service.StorageProfileLevelService;

public class StorageProfileLevelServiceImpl extends BaseServiceImpl<NStorageProfileLevel> implements StorageProfileLevelService {

    public StorageProfileLevelServiceImpl(BaseDao<NStorageProfileLevel> baseDao) {
        super(baseDao);
        // TODO Auto-generated constructor stub
    }

    @Override
    public NStorageProfileLevel getUserLevelConf(String userLevel) {

        NStorageProfileLevel profileLevel = new NStorageProfileLevel();
        profileLevel.setType(NStorageProfileLevel.USER_LEVEL_TYPE);
        profileLevel.setLevel(userLevel);
        return ((StorageProfileLevelDao) baseDao).getUserLevelConf(profileLevel);
    }
}
