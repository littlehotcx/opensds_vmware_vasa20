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

import org.opensds.vasa.domain.model.bean.S2DArray;
import org.opensds.vasa.vasa20.device.dj.bean.SArray;

public class ArrayCapabilityConvert {
    public S2DArray ConvertSouth2Model(SArray southItem) {
        if (null == southItem) {
            return null;
        }

        S2DArray modelItem = new S2DArray();
        modelItem.setId(southItem.getId());
        modelItem.setModel(southItem.getModel());
        modelItem.setSn(southItem.getSn());
        modelItem.setCreated_at(southItem.getCreated_at());
        modelItem.setDevice_status(southItem.getDevice_status());

        return modelItem;
    }

    public List<S2DArray> convertSouth2Model(List<SArray> southItem) {
        List<S2DArray> modelItem = new ArrayList<S2DArray>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SArray sa : southItem) {
            modelItem.add(ConvertSouth2Model(sa));
        }
        return modelItem;
    }
}
