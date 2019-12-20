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

import org.opensds.vasa.domain.model.bean.S2DVolume;
import org.opensds.vasa.domain.model.bean.S2DVolumeMetaData;
import org.opensds.vasa.vasa20.device.dj.bean.SVolume;
import org.opensds.vasa.vasa20.device.dj.bean.SVolumeMetaData;

public class VolumeCapabilityConvert {
    public S2DVolumeMetaData convertSouth2Model(SVolumeMetaData southItem) {
        if (null == southItem) {
            return null;
        }

        S2DVolumeMetaData modelItem = new S2DVolumeMetaData();
        modelItem.setCreationWay(southItem.getCreationWay());
        modelItem.setIsVvol(southItem.getIsVvol());
        modelItem.setRawId(southItem.getRawId());
        modelItem.setStorageArrayId(southItem.getStorageArrayId());
        modelItem.setVirtualPoolId(southItem.getVirtualPoolId());
        modelItem.setRawCopyId(southItem.getRawCopyId());
        modelItem.setSizeInMB(southItem.getSizeInMegaBytes());
        modelItem.setDisplayName(southItem.getDisplayName());
        modelItem.setAffinityEnabled(southItem.getAffinityEnabled());
        modelItem.setAffinityParams(southItem.getAffinityParams());

        return modelItem;
    }

    public S2DVolume convertSouth2Model(SVolume southItem) {
        if (null == southItem) {
            return null;
        }

        S2DVolume modelItem = new S2DVolume();
        modelItem.setCreated_at(southItem.getCreated_at());
        modelItem.setDescription(southItem.getDescription());
        modelItem.setId(southItem.getId());
        modelItem.setMetadata(convertSouth2Model(southItem.getMetadata()));
        modelItem.setName(southItem.getName());
        modelItem.setRaw_id(southItem.getRaw_id());
        modelItem.setSize(southItem.getSize());
        modelItem.setSnapshot_id(southItem.getSnapshot_id());
        modelItem.setSource_volid(southItem.getSource_volid());
        modelItem.setStatus(southItem.getStatus());
        modelItem.setVolume_type(southItem.getVolume_type());

        return modelItem;
    }

    public List<S2DVolume> convertSouth2Model(List<SVolume> southItem) {
        List<S2DVolume> modelItem = new ArrayList<S2DVolume>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SVolume vol : southItem) {
            modelItem.add(convertSouth2Model(vol));
        }
        return modelItem;
    }
}
