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

package org.opensds.vasa.vasa20.device.dj.convert;

import java.util.ArrayList;
import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DVirtualPool;
import org.opensds.vasa.domain.model.bean.S2DVirtualPoolSpaceStats;
import org.opensds.vasa.vasa20.device.dj.bean.SVirtualPool;
import org.opensds.vasa.vasa20.device.dj.bean.SVirtualPoolSpaceStats;

public class VirtualPoolCapabilityConvert {
    public S2DVirtualPool convertSouth2Model(SVirtualPool southItem) {
        if (null == southItem) {
            return null;
        }

        S2DVirtualPool modelItem = new S2DVirtualPool();
        modelItem.setId(southItem.getId());
        modelItem.setName(southItem.getName());
        modelItem.setTotal_capacity(southItem.getTotal_capacity());
        modelItem.setFree_capacity(southItem.getFree_capacity());
        modelItem.setAvailable_capacity(southItem.getAvailable_capacity());

        return modelItem;
    }

    public List<S2DVirtualPool> convertSouth2Model(List<SVirtualPool> southItem) {
        List<S2DVirtualPool> modelItem = new ArrayList<S2DVirtualPool>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SVirtualPool vp : southItem) {
            modelItem.add(convertSouth2Model(vp));
        }
        return modelItem;
    }

    public S2DVirtualPoolSpaceStats convertSouth2Model(SVirtualPoolSpaceStats southItem) {
        if (null == southItem) {
            return null;
        }

        S2DVirtualPoolSpaceStats modelItem = new S2DVirtualPoolSpaceStats();
        modelItem.setId(southItem.getId());
        modelItem.setTotal_capacity(southItem.getTotal_capacity());
        modelItem.setFree_capacity(southItem.getFree_capacity());
        modelItem.setAvailable_capacity(southItem.getAvailable_capacity());


        return modelItem;
    }
}
