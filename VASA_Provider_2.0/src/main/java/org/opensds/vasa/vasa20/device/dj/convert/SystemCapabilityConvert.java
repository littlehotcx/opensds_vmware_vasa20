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

import org.opensds.vasa.domain.model.bean.S2DSystem;
import org.opensds.vasa.vasa20.device.dj.bean.SSystem;

public class SystemCapabilityConvert {
    public S2DSystem convertSouth2Model(SSystem southItem) {
        if (null == southItem) {
            return null;
        }

        S2DSystem modelItem = new S2DSystem();
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
        modelItem.setVASA_ALTERNATE_NAME(southItem.getVASA_ALTERNATE_NAME());
        modelItem.setVASA_SUPPORT_BLOCK(southItem.getVASA_SUPPORT_BLOCK());
        modelItem.setVASA_SUPPORT_FILESYSTEM(southItem.getVASA_SUPPORT_FILESYSTEM());
        modelItem.setVASA_SUPPORT_PROFILE(southItem.getVASA_SUPPORT_PROFILE());
        modelItem.setPRODUCTMODE(southItem.getPRODUCTMODE());
        modelItem.setPRODUCTVERSION(southItem.getPRODUCTVERSION());


        return modelItem;
    }

    public List<S2DSystem> convertSouth2Model(List<SSystem> southItem) {
        List<S2DSystem> modelItem = new ArrayList<S2DSystem>();
        if (null == southItem) {
            return modelItem;
        }

        for (SSystem ss : southItem) {
            modelItem.add(convertSouth2Model(ss));
        }
        return modelItem;
    }
}
