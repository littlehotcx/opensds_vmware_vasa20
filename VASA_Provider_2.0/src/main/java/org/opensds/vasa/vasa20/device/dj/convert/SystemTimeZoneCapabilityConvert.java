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

import org.opensds.vasa.domain.model.bean.S2DSystemTimeZone;
import org.opensds.vasa.vasa20.device.dj.bean.SSystemTimeZone;

public class SystemTimeZoneCapabilityConvert {
    public S2DSystemTimeZone convertSouth2Model(SSystemTimeZone southItem) {
        if (null == southItem) {
            return null;
        }
        S2DSystemTimeZone modelItem = new S2DSystemTimeZone();
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
        modelItem.setCMO_SYS_TIME_ZONE(southItem.getCMO_SYS_TIME_ZONE());
        modelItem.setCMO_SYS_TIME_ZONE_NAME(southItem.getCMO_SYS_TIME_ZONE_NAME());
        modelItem.setCMO_SYS_TIME_ZONE_NAME_STYLE(southItem.getCMO_SYS_TIME_ZONE_NAME_STYLE());
        modelItem.setCMO_SYS_TIME_ZONE_USE_DST(southItem.getCMO_SYS_TIME_ZONE_USE_DST());

        return modelItem;
    }
}
