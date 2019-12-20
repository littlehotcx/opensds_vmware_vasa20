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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;

public class PoolFilterSizeService extends AbstractPoolFilterService {

    private static Logger logger = LogManager.getLogger(PoolFilterSizeService.class);

    @Override
    public boolean checkPoolIsMatch(S2DStoragePool nStoragePool,
                                    StoragePolicy storagePolicy, long sizeInMB) {
        // TODO Auto-generated method stub
        Long freeCapacity = Long.valueOf(nStoragePool.getFreeCapacity());
        long freeCapacitySizeInMb = freeCapacity * MagicNumber.LONG512 / (MagicNumber.LONG1024 * MagicNumber.LONG1024);
        logger.info("select poll ,freeCapacitySizeInMb = " + freeCapacitySizeInMb + ",sizeInMB = " + sizeInMB + ",poolRawId = " + nStoragePool.getID());
        if (freeCapacitySizeInMb > sizeInMB) {
            return true;
        }
        return false;
    }

    @Override
    public void beforCheckMatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCheckMatch(List<S2DStoragePool> result, final long sizeInMB) {
        // TODO Auto-generated method stub
        //sort the storagePool witch is filter by size
        //sort it with capacity_threshold
        Collections.sort(result, new Comparator<S2DStoragePool>() {

            @Override
            public int compare(S2DStoragePool o1, S2DStoragePool o2) {
                // TODO Auto-generated method stub
                Long freeCapacity_o1 = Long.valueOf(o1.getFreeCapacity());

                Long freeCapacity_o2 = Long.valueOf(o2.getFreeCapacity());

                return freeCapacity_o2.compareTo(freeCapacity_o1);
            }
        });
    }


}
