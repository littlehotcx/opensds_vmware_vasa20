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
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.vasa.vasa.pool.service.IPoolFilterService;
import org.opensds.vasa.vasa.pool.service.ISelectPoolService;

public class SelectPoolServiceImpl implements ISelectPoolService {

    public List<IPoolFilterService> filterServices;
    private static Logger logger = LogManager.getLogger(SelectPoolServiceImpl.class);

    @Override
    public S2DStoragePool selectPool(List<S2DStoragePool> containerPools,
                                     StoragePolicy storagePolicy, long sizeInMB) {
        // TODO Auto-generated method stub

        //filter the storagePool in hierarchies, we can also select it in parallel.
        if (null != filterServices && filterServices.size() != 0) {
            List<S2DStoragePool> filterStoragePool = containerPools;
            for (IPoolFilterService filterService : filterServices) {
                if (null != filterStoragePool && filterStoragePool.size() != 0) {
                    filterStoragePool = filterService.filterStoragePool(filterStoragePool, storagePolicy, sizeInMB);
                }
            }

            //sort the storagePool witch is filter by size
            sortStoragePoolBySize(filterStoragePool);

            if (null != filterStoragePool && filterStoragePool.size() != 0) {
                //get the first filter pool
                logger.debug("filteredPools" + filterStoragePool);
                return filterStoragePool.get(0);
            }
        }
        return null;
    }

    public void sortStoragePoolBySize(List<S2DStoragePool> result) {
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

    public List<IPoolFilterService> getFilterServices() {
        return filterServices;
    }


    public void setFilterServices(List<IPoolFilterService> filterServices) {
        this.filterServices = filterServices;
    }


}
