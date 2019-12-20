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

package org.opensds.vasa.vasa.pool.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.vasa.vasa.pool.service.IPoolFilterService;

public abstract class AbstractPoolFilterService implements IPoolFilterService {


    public Boolean filter_use_swith = true;

    @Override
    public List<S2DStoragePool> filterStoragePool(List<S2DStoragePool> pools,
                                                  StoragePolicy storagePolicy, long sizeInMB) {
        // TODO Auto-generated method stub
        if (filter_use_swith) {
            beforCheckMatch();
            List<S2DStoragePool> result = new ArrayList<>();
            if (null != pools && pools.size() != 0) {
                for (S2DStoragePool s2dStoragePool : pools) {
                    if (checkPoolIsMatch(s2dStoragePool, storagePolicy, sizeInMB)) {
                        result.add(s2dStoragePool);
                    }
                }
            }
            afterCheckMatch(result, sizeInMB);
            return result;
        } else {
            return pools;
        }
    }

    public abstract void beforCheckMatch();

    public abstract boolean checkPoolIsMatch(S2DStoragePool s2dStoragePool,
                                             StoragePolicy storagePolicy, long sizeInMB);

    public abstract void afterCheckMatch(List<S2DStoragePool> result, long sizeInMB);

    public Boolean getFilter_use_swith() {
        return filter_use_swith;
    }

    public void setFilter_use_swith(Boolean filter_use_swith) {
        this.filter_use_swith = filter_use_swith;
    }


}
