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

import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa20.device.dj.bean.StoragePoolResBean;

public class StoragePoolResBeanCapabilityConvert {
    public static S2DStoragePool convertSouth2Model(StoragePoolResBean southItem) {
        if (null == southItem) {
            return null;
        }
        S2DStoragePool modelItem = new S2DStoragePool();
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

        modelItem.setTotalCapacity(southItem.getUSERTOTALCAPACITY());
        modelItem.setFreeCapacity(southItem.getUSERFREECAPACITY());
        modelItem.setConsumedCapacity(southItem.getUSERCONSUMEDCAPACITY());
        modelItem.setConsumedCapacityPercentage(southItem.getUSERCONSUMEDCAPACITYPERCENTAGE());
        modelItem.setConsumedCapacityThreshold(southItem.getUSERCONSUMEDCAPACITYTHRESHOLD());
        modelItem.setTier0RaidLv(southItem.getTIER0RAIDLV());
        modelItem.setTier1RaidLv(southItem.getTIER1RAIDLV());
        modelItem.setTier2RaidLv(southItem.getTIER2RAIDLV());
        modelItem.setDataSpace(southItem.getDATASPACE());
        modelItem.setEnableSmartCatch(southItem.getENABLESMARTCACHE());
        modelItem.setIsSmartTierEnable(southItem.getISSMARTTIERENABLE());
        modelItem.setTier0capacity(southItem.getTIER0CAPACITY());
        modelItem.setTier1capacity(southItem.getTIER1CAPACITY());
        modelItem.setTier2capacity(southItem.getTIER2CAPACITY());
        modelItem.setTier0disktype(southItem.getTIER0DISKTYPE());
        modelItem.setTier1disktype(southItem.getTIER1DISKTYPE());
        modelItem.setTier2disktype(southItem.getTIER2DISKTYPE());
        modelItem.setUsageType(southItem.getUSAGETYPE());

        return modelItem;
    }
}

