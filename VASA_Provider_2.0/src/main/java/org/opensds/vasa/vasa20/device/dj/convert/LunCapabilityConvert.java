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

import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.vasa20.device.dj.bean.SLun;

public class LunCapabilityConvert {
    public S2DLun convertSouth2Model(SLun southItem) {
        if (null == southItem) {
            return null;
        }
        S2DLun modelItem = new S2DLun();
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
        modelItem.setCAPABILITY(southItem.getCAPABILITY());
        modelItem.setCAPACITY(southItem.getCAPACITY());
        modelItem.setALLOCTYPE(southItem.getALLOCTYPE());
        modelItem.setALLOCCAPACITY(southItem.getALLOCCAPACITY());
        modelItem.setWWN(southItem.getWWN());
        modelItem.setDRS_ENABLE(southItem.getDRS_ENABLE());
        modelItem.setCAPACITYALARMLEVEL(southItem.getCAPACITYALARMLEVEL());
        modelItem.setTHINCAPACITYUSAGE(southItem.getTHINCAPACITYUSAGE());
        modelItem.setSECTORSIZE(southItem.getSECTORSIZE());
        modelItem.setUSAGETYPE(southItem.getUSAGETYPE());

        return modelItem;
    }

    public List<S2DLun> convertSouth2Model(List<SLun> southItem) {
        List<S2DLun> modelItem = new ArrayList<S2DLun>();
        if (null == southItem || southItem.size() == 0) {
            return modelItem;
        }

        for (SLun slun : southItem) {
            modelItem.add(convertSouth2Model(slun));
        }
        return modelItem;
    }
}
