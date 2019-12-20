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

import org.opensds.vasa.vasa.db.dao.AddStorageArraySyncDao;
import org.opensds.vasa.vasa.db.model.NAddStorageArraySync;
import org.opensds.vasa.vasa.db.service.AddStorageArraySyncService;

public class AddStorageArraySyncServiceImpl implements AddStorageArraySyncService {
    private AddStorageArraySyncDao addStorageArraySyncDao;

    public AddStorageArraySyncDao getAddStorageArraySyncDao() {
        return addStorageArraySyncDao;
    }

    public void setAddStorageArraySyncDao(AddStorageArraySyncDao addStorageArraySyncDao) {
        this.addStorageArraySyncDao = addStorageArraySyncDao;
    }

    @Override
    public void addStorageArrayFuncSync(NAddStorageArraySync nAddStorageArraySync) {
        addStorageArraySyncDao.addStorageArrayFuncSync(nAddStorageArraySync);
    }
}
