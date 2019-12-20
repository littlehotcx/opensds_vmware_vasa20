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

package org.opensds.vasa.domain.model.bean;

public class S2DStoragePool extends DomainBaseBean {

    private String totalCapacity;

    private String freeCapacity;

    private String consumedCapacity;

    private String consumedCapacityPercentage;

    private String consumedCapacityThreshold;

    private String tier0RaidLv;

    private String tier1RaidLv;

    private String tier2RaidLv;

    private String dataSpace;

    private String enableSmartCatch;

    private String isSmartTierEnable;

    private String tier0capacity;

    private String tier1capacity;

    private String tier2capacity;

    private String tier0disktype;

    private String tier1disktype;

    private String tier2disktype;

    private String usageType;

    public String getTier0disktype() {
        return tier0disktype;
    }

    public void setTier0disktype(String tier0disktype) {
        this.tier0disktype = tier0disktype;
    }

    public String getTier1disktype() {
        return tier1disktype;
    }

    public void setTier1disktype(String tier1disktype) {
        this.tier1disktype = tier1disktype;
    }

    public String getTier2disktype() {
        return tier2disktype;
    }

    public void setTier2disktype(String tier2disktype) {
        this.tier2disktype = tier2disktype;
    }

    public String getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(String totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getFreeCapacity() {
        return freeCapacity;
    }

    public void setFreeCapacity(String freeCapacity) {
        this.freeCapacity = freeCapacity;
    }

    public String getConsumedCapacity() {
        return consumedCapacity;
    }

    public void setConsumedCapacity(String consumedCapacity) {
        this.consumedCapacity = consumedCapacity;
    }

    public String getConsumedCapacityPercentage() {
        return consumedCapacityPercentage;
    }

    public void setConsumedCapacityPercentage(String consumedCapacityPercentage) {
        this.consumedCapacityPercentage = consumedCapacityPercentage;
    }

    public String getConsumedCapacityThreshold() {
        return consumedCapacityThreshold;
    }

    public void setConsumedCapacityThreshold(String consumedCapacityThreshold) {
        this.consumedCapacityThreshold = consumedCapacityThreshold;
    }

    public String getTier0RaidLv() {
        return tier0RaidLv;
    }

    public void setTier0RaidLv(String tier0RaidLv) {
        this.tier0RaidLv = tier0RaidLv;
    }


    public String getTier1RaidLv() {
        return tier1RaidLv;
    }

    public void setTier1RaidLv(String tier1RaidLv) {
        this.tier1RaidLv = tier1RaidLv;
    }

    public String getTier2RaidLv() {
        return tier2RaidLv;
    }

    public void setTier2RaidLv(String tier2RaidLv) {
        this.tier2RaidLv = tier2RaidLv;
    }

    public String getDataSpace() {
        return dataSpace;
    }

    public void setDataSpace(String dataSpace) {
        this.dataSpace = dataSpace;
    }

    public String getEnableSmartCatch() {
        return enableSmartCatch;
    }

    public void setEnableSmartCatch(String enableSmartCatch) {
        this.enableSmartCatch = enableSmartCatch;
    }

    public String getIsSmartTierEnable() {
        return isSmartTierEnable;
    }

    public void setIsSmartTierEnable(String isSmartTierEnable) {
        this.isSmartTierEnable = isSmartTierEnable;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    @Override
    public String toString() {
        return "S2DStoragePool [totalCapacity=" + totalCapacity
                + ", freeCapacity=" + freeCapacity + ", consumedCapacity="
                + consumedCapacity + ", consumedCapacityPercentage="
                + consumedCapacityPercentage + ", consumedCapacityThreshold="
                + consumedCapacityThreshold + ", tier0RaidLv=" + tier0RaidLv
                + ", tier1RaidLv=" + tier1RaidLv + ", tier2RaidLv=" + tier2RaidLv
                + ", dataSpace=" + dataSpace + ", enableSmartCatch="
                + enableSmartCatch + ", isSmartTierEnable=" + isSmartTierEnable
                + ", tier0capacity=" + tier0capacity + ", tier1capacity="
                + tier1capacity + ", tier2capacity=" + tier2capacity
                + ", getID()=" + getID() + ", getNAME()=" + getNAME()
                + ", getPARENTID()=" + getPARENTID() + ", getPARENTNAME()="
                + getPARENTNAME() + ", getLOCATION()=" + getLOCATION()
                + ", getHEALTHSTATUS()=" + getHEALTHSTATUS()
                + ", getRUNNINGSTATUS()=" + getRUNNINGSTATUS()
                + ", getDESCRIPTION()=" + getDESCRIPTION() + ", getCOUNT()="
                + getCOUNT() + ", getASSOCIATEOBJID()=" + getASSOCIATEOBJID()
                + ", getAPPLICATION()=" + getAPPLICATION()
                + ", getTENANCYID()=" + getTENANCYID() + ", getTENANCYNAME()="
                + getTENANCYNAME() + ", getTYPE()=" + getTYPE()
                + ", getPARENTTYPE()=" + getPARENTTYPE()
                + ", getASSOCIATEOBJTYPE()=" + getASSOCIATEOBJTYPE()
                + ", getSUBTYPE()=" + getSUBTYPE()
                + ", getUsageType()=" + getUsageType() + "]";
    }

    public String getTier0capacity() {
        return tier0capacity;
    }

    public void setTier0capacity(String tier0capacity) {
        this.tier0capacity = tier0capacity;
    }

    public String getTier1capacity() {
        return tier1capacity;
    }

    public void setTier1capacity(String tier1capacity) {
        this.tier1capacity = tier1capacity;
    }

    public String getTier2capacity() {
        return tier2capacity;
    }

    public void setTier2capacity(String tier2capacity) {
        this.tier2capacity = tier2capacity;
    }


}
