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

import org.opensds.vasa.domain.model.bean.S2DPassThroughSnapshot;
import org.opensds.vasa.domain.model.bean.S2DSnapshot;
import org.opensds.vasa.domain.model.bean.S2DSnapshotMetadata;
import org.opensds.vasa.vasa20.device.dj.bean.PassThroughSnapshot;
import org.opensds.vasa.vasa20.device.dj.bean.SSnapshot;
import org.opensds.vasa.vasa20.device.dj.bean.SSnapshotMetadata;

public class SnapshotCapabilityConvert {
    public S2DSnapshotMetadata convertSouth2Model(SSnapshotMetadata southItem) {
        if (null == southItem) {
            return null;
        }

        S2DSnapshotMetadata modelItem = new S2DSnapshotMetadata();
        modelItem.setIsVvol(southItem.getIsVvol());
        modelItem.setRawId(southItem.getRawId());
        modelItem.setStorageArrayId(southItem.getStorageArrayId());

        return modelItem;
    }

    public S2DSnapshot convertSouth2Model(SSnapshot southItem) {
        if (null == southItem) {
            return null;
        }

        S2DSnapshot modelItem = new S2DSnapshot();
        modelItem.setCreated_at(southItem.getCreated_at());
        modelItem.setDescription(southItem.getDescription());
        modelItem.setId(southItem.getId());
        modelItem.setMetadata(convertSouth2Model(southItem.getMetadata()));
        modelItem.setName(southItem.getName());
        modelItem.setSize(southItem.getSize());
        modelItem.setStatus(southItem.getStatus());
        modelItem.setVolume_id(southItem.getVolume_id());

        return modelItem;
    }

    public List<S2DSnapshot> convertSouth2Model(List<SSnapshot> southItem) {
        List<S2DSnapshot> modelItem = new ArrayList<S2DSnapshot>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SSnapshot vol : southItem) {
            modelItem.add(convertSouth2Model(vol));
        }
        return modelItem;
    }

    public S2DPassThroughSnapshot convertSouth2Model(PassThroughSnapshot southItem) {
        if (null == southItem) {
            return null;
        }
        S2DPassThroughSnapshot modelItem = new S2DPassThroughSnapshot();
        //common tag
        modelItem.setAPPLICATION(southItem.getAPPLICATION());
        modelItem.setASSOCIATEOBJID(southItem.getASSOCIATEOBJID());
        modelItem.setASSOCIATEOBJTYPE(southItem.getASSOCIATEOBJTYPE());
        modelItem.setCOUNT(southItem.getCOUNT());
        modelItem.setDESCRIPTION(southItem.getDESCRIPTION());
        modelItem.setHEALTHSTATUS(southItem.getHEALTHSTATUS());
        modelItem.setID(southItem.getID());
        modelItem.setLOCATION(southItem.getLOCATION());
        modelItem.setNAME(southItem.getNAME());
        modelItem.setPARENTID(southItem.getPARENTID());
        modelItem.setPARENTNAME(southItem.getPARENTNAME());
        modelItem.setPARENTTYPE(southItem.getPARENTTYPE());
        modelItem.setRUNNINGSTATUS(southItem.getRUNNINGSTATUS());
        modelItem.setSUBTYPE(southItem.getSUBTYPE());
        modelItem.setTENANCYID(southItem.getTENANCYID());
        modelItem.setTENANCYNAME(southItem.getTENANCYNAME());
        modelItem.setTYPE(southItem.getTYPE());

        //private tag
        modelItem.setROLLBACKENDTIME(southItem.getROLLBACKENDTIME());
        modelItem.setROLLBACKRATE(southItem.getROLLBACKRATE());
        modelItem.setROLLBACKSPEED(southItem.getROLLBACKSPEED());
        modelItem.setROLLBACKSTARTTIME(southItem.getROLLBACKSTARTTIME());
        modelItem.setUSERCAPACITY(southItem.getUSERCAPACITY());
        modelItem.setCONSUMEDCAPACITY(southItem.getCONSUMEDCAPACITY());

        return modelItem;
    }

}
