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

package org.opensds.vasa.vasa20.device.dj.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoragePoolResBean extends SouthBaseBean {
    @JsonProperty
    public String USERTOTALCAPACITY;
    @JsonProperty
    public String USERFREECAPACITY;
    @JsonProperty
    public String USERCONSUMEDCAPACITY;
    @JsonProperty
    public String USERCONSUMEDCAPACITYPERCENTAGE;
    @JsonProperty
    public String USERCONSUMEDCAPACITYTHRESHOLD;
    @JsonProperty
    public String TIER0RAIDLV;
    @JsonProperty
    public String TIER1RAIDLV;
    @JsonProperty
    public String TIER2RAIDLV;
    @JsonProperty
    public String DATASPACE;
    @JsonProperty
    public String ENABLESMARTCACHE;
    @JsonProperty
    public String ISSMARTTIERENABLE;
    @JsonProperty
    public String USAGETYPE;
    @JsonProperty
    public String TIER0CAPACITY;
    @JsonProperty
    public String TIER1CAPACITY;
    @JsonProperty
    public String TIER2CAPACITY;
    @JsonProperty
    public String TIER0DISKTYPE;
    @JsonProperty
    public String TIER1DISKTYPE;
    @JsonProperty
    public String TIER2DISKTYPE;


    public String getTIER0DISKTYPE() {
        return TIER0DISKTYPE;
    }

    public void setTIER0DISKTYPE(String tIER0DISKTYPE) {
        TIER0DISKTYPE = tIER0DISKTYPE;
    }

    public String getTIER1DISKTYPE() {
        return TIER1DISKTYPE;
    }

    public void setTIER1DISKTYPE(String tIER1DISKTYPE) {
        TIER1DISKTYPE = tIER1DISKTYPE;
    }

    public String getTIER2DISKTYPE() {
        return TIER2DISKTYPE;
    }

    public void setTIER2DISKTYPE(String tIER2DISKTYPE) {
        TIER2DISKTYPE = tIER2DISKTYPE;
    }

    public String getUSAGETYPE() {
        return USAGETYPE;
    }

    public void setUSAGETYPE(String uSAGETYPE) {
        USAGETYPE = uSAGETYPE;
    }

    public String getUSERTOTALCAPACITY() {
        return USERTOTALCAPACITY;
    }

    public void setUSERTOTALCAPACITY(String uSERTOTALCAPACITY) {
        USERTOTALCAPACITY = uSERTOTALCAPACITY;
    }

    public String getUSERFREECAPACITY() {
        return USERFREECAPACITY;
    }

    public void setUSERFREECAPACITY(String uSERFREECAPACITY) {
        USERFREECAPACITY = uSERFREECAPACITY;
    }

    public String getUSERCONSUMEDCAPACITY() {
        return USERCONSUMEDCAPACITY;
    }

    public void setUSERCONSUMEDCAPACITY(String uSERCONSUMEDCAPACITY) {
        USERCONSUMEDCAPACITY = uSERCONSUMEDCAPACITY;
    }

    public String getUSERCONSUMEDCAPACITYPERCENTAGE() {
        return USERCONSUMEDCAPACITYPERCENTAGE;
    }

    public void setUSERCONSUMEDCAPACITYPERCENTAGE(String uSERCONSUMEDCAPACITYPERCENTAGE) {
        USERCONSUMEDCAPACITYPERCENTAGE = uSERCONSUMEDCAPACITYPERCENTAGE;
    }

    public String getUSERCONSUMEDCAPACITYTHRESHOLD() {
        return USERCONSUMEDCAPACITYTHRESHOLD;
    }

    public void setUSERCONSUMEDCAPACITYTHRESHOLD(String uSERCONSUMEDCAPACITYTHRESHOLD) {
        USERCONSUMEDCAPACITYTHRESHOLD = uSERCONSUMEDCAPACITYTHRESHOLD;
    }

    public String getTIER0RAIDLV() {
        return TIER0RAIDLV;
    }

    public void setTIER0RAIDLV(String tIER0RAIDLV) {
        TIER0RAIDLV = tIER0RAIDLV;
    }

    public String getTIER1RAIDLV() {
        return TIER1RAIDLV;
    }

    public void setTIER1RAIDLV(String tIER1RAIDLV) {
        TIER1RAIDLV = tIER1RAIDLV;
    }

    public String getTIER2RAIDLV() {
        return TIER2RAIDLV;
    }

    public void setTIER2RAIDLV(String tIER2RAIDLV) {
        TIER2RAIDLV = tIER2RAIDLV;
    }

    public String getDATASPACE() {
        return DATASPACE;
    }

    public void setDATASPACE(String dATASPACE) {
        DATASPACE = dATASPACE;
    }

    public String getENABLESMARTCACHE() {
        return ENABLESMARTCACHE;
    }

    public void setENABLESMARTCACHE(String eNABLESMARTCACHE) {
        ENABLESMARTCACHE = eNABLESMARTCACHE;
    }

    public String getISSMARTTIERENABLE() {
        return ISSMARTTIERENABLE;
    }

    public void setISSMARTTIERENABLE(String iSSMARTTIERENABLE) {
        ISSMARTTIERENABLE = iSSMARTTIERENABLE;
    }

    public String getTIER0CAPACITY() {
        return TIER0CAPACITY;
    }

    public void setTIER0CAPACITY(String tIER0CAPACITY) {
        TIER0CAPACITY = tIER0CAPACITY;
    }

    public String getTIER1CAPACITY() {
        return TIER1CAPACITY;
    }

    public void setTIER1CAPACITY(String tIER1CAPACITY) {
        TIER1CAPACITY = tIER1CAPACITY;
    }

    public String getTIER2CAPACITY() {
        return TIER2CAPACITY;
    }

    public void setTIER2CAPACITY(String tIER2CAPACITY) {
        TIER2CAPACITY = tIER2CAPACITY;
    }

}
