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

import org.opensds.vasa.domain.model.bean.S2DBitmap;
import org.opensds.vasa.vasa20.device.dj.bean.SBitmap;

public class BitmapCapabilityConvert {
    public S2DBitmap convertSouth2Model(SBitmap southItem) {
        if (null == southItem) {
            return null;
        }

        S2DBitmap modelItem = new S2DBitmap();
        modelItem.setVVOLID(southItem.getVVOLID());
        modelItem.setBASEVVOLID(southItem.getBASEVVOLID());
        modelItem.setSEGMENTSTARTOFFSETBYTES(southItem.getSEGMENTSTARTOFFSETBYTES());
        modelItem.setSEGMENTLENGTHBYTES(southItem.getSEGMENTLENGTHBYTES());
        modelItem.setCHUNKSIZEBYTES(southItem.getCHUNKSIZEBYTES());
        modelItem.setUNSHAREDCHUNKS(southItem.getUNSHAREDCHUNKS());
        modelItem.setSCANNEDCHUNKS(southItem.getSCANNEDCHUNKS());
        modelItem.setCHUNKBITMAP(southItem.getCHUNKBITMAP());

        return modelItem;
    }

    public List<S2DBitmap> convertSouth2Model(List<SBitmap> southItem) {
        List<S2DBitmap> modelItem = new ArrayList<S2DBitmap>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SBitmap sa : southItem) {
            modelItem.add(convertSouth2Model(sa));
        }
        return modelItem;
    }
}
