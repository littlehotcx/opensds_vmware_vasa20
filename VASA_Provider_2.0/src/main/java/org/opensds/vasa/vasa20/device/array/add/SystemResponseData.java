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

package org.opensds.vasa.vasa20.device.array.add;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemResponseData {
    @JsonProperty
    private String CACHEWRITEQUOTA;
    @JsonProperty
    private String CONFIGMODEL;
    @JsonProperty
    private String DESCRIPTION;
    @JsonProperty
    private String DOMAINNAME;
    @JsonProperty
    private String FREEDISKSCAPACITY;
    @JsonProperty
    private String HEALTHSTATUS;
    @JsonProperty
    private String HOTSPAREDISKSCAPACITY;
    @JsonProperty
    private String ID;
    @JsonProperty
    private String LOCATION;
    @JsonProperty
    private String MEMBERDISKSCAPACITY;
    @JsonProperty
    private String NAME;
    @JsonProperty
    private String PRODUCTMODE;
    @JsonProperty
    private String PRODUCTVERSION;
    @JsonProperty
    private String RUNNINGSTATUS;
    @JsonProperty
    private String SECTORSIZE;
    @JsonProperty
    private String STORAGEPOOLCAPACITY;
    @JsonProperty
    private String STORAGEPOOLFREECAPACITY;
    @JsonProperty
    private String STORAGEPOOLHOSTSPARECAPACITY;
    @JsonProperty
    private String STORAGEPOOLRAWCAPACITY;
    @JsonProperty
    private String STORAGEPOOLUSEDCAPACITY;
    @JsonProperty
    private String THICKLUNSALLOCATECAPACITY;
    @JsonProperty
    private String THICKLUNSUSEDCAPACITY;
    @JsonProperty
    private String THINLUNSALLOCATECAPACITY;
    @JsonProperty
    private String THINLUNSMAXCAPACITY;
    @JsonProperty
    private String THINLUNSUSEDCAPACITY;
    @JsonProperty
    private String TOTALCAPACITY;
    @JsonProperty
    private String TYPE;
    @JsonProperty
    private String UNAVAILABLEDISKSCAPACITY;
    @JsonProperty
    private String USEDCAPACITY;
    @JsonProperty
    private String VASA_ALTERNATE_NAME;
    @JsonProperty
    private String VASA_SUPPORT_BLOCK;
    @JsonProperty
    private String VASA_SUPPORT_FILESYSTEM;
    @JsonProperty
    private String VASA_SUPPORT_PROFILE;
    @JsonProperty
    private String WRITETHROUGHSW;
    @JsonProperty
    private String WRITETHROUGHTIME;
    @JsonProperty
    private String productModeString;
    private String mappedLunsCountCapacity;
    private String patchVersion;
    private String unMappedLunsCountCapacity;
    private String userFreeCapacity;
    private String wwn;
    private String vendor;


    public String getProductModeString() {
        return productModeString;
    }

    public void setProductModeString(String productModeString) {
        this.productModeString = productModeString;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getCACHEWRITEQUOTA() {
        return CACHEWRITEQUOTA;
    }

    public void setCACHEWRITEQUOTA(String cACHEWRITEQUOTA) {
        CACHEWRITEQUOTA = cACHEWRITEQUOTA;
    }

    public String getCONFIGMODEL() {
        return CONFIGMODEL;
    }

    public void setCONFIGMODEL(String cONFIGMODEL) {
        CONFIGMODEL = cONFIGMODEL;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String getDOMAINNAME() {
        return DOMAINNAME;
    }

    public void setDOMAINNAME(String dOMAINNAME) {
        DOMAINNAME = dOMAINNAME;
    }

    public String getFREEDISKSCAPACITY() {
        return FREEDISKSCAPACITY;
    }

    public void setFREEDISKSCAPACITY(String fREEDISKSCAPACITY) {
        FREEDISKSCAPACITY = fREEDISKSCAPACITY;
    }

    public String getHEALTHSTATUS() {
        return HEALTHSTATUS;
    }

    public void setHEALTHSTATUS(String hEALTHSTATUS) {
        HEALTHSTATUS = hEALTHSTATUS;
    }

    public String getHOTSPAREDISKSCAPACITY() {
        return HOTSPAREDISKSCAPACITY;
    }

    public void setHOTSPAREDISKSCAPACITY(String hOTSPAREDISKSCAPACITY) {
        HOTSPAREDISKSCAPACITY = hOTSPAREDISKSCAPACITY;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    public String getMEMBERDISKSCAPACITY() {
        return MEMBERDISKSCAPACITY;
    }

    public void setMEMBERDISKSCAPACITY(String mEMBERDISKSCAPACITY) {
        MEMBERDISKSCAPACITY = mEMBERDISKSCAPACITY;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getPRODUCTMODE() {
        return PRODUCTMODE;
    }

    public void setPRODUCTMODE(String pRODUCTMODE) {
        PRODUCTMODE = pRODUCTMODE;
    }

    public String getPRODUCTVERSION() {
        return PRODUCTVERSION;
    }

    public void setPRODUCTVERSION(String pRODUCTVERSION) {
        PRODUCTVERSION = pRODUCTVERSION;
    }

    public String getRUNNINGSTATUS() {
        return RUNNINGSTATUS;
    }

    public void setRUNNINGSTATUS(String rUNNINGSTATUS) {
        RUNNINGSTATUS = rUNNINGSTATUS;
    }

    public String getSECTORSIZE() {
        return SECTORSIZE;
    }

    public void setSECTORSIZE(String sECTORSIZE) {
        SECTORSIZE = sECTORSIZE;
    }

    public String getSTORAGEPOOLCAPACITY() {
        return STORAGEPOOLCAPACITY;
    }

    public void setSTORAGEPOOLCAPACITY(String sTORAGEPOOLCAPACITY) {
        STORAGEPOOLCAPACITY = sTORAGEPOOLCAPACITY;
    }

    public String getSTORAGEPOOLFREECAPACITY() {
        return STORAGEPOOLFREECAPACITY;
    }

    public void setSTORAGEPOOLFREECAPACITY(String sTORAGEPOOLFREECAPACITY) {
        STORAGEPOOLFREECAPACITY = sTORAGEPOOLFREECAPACITY;
    }

    public String getSTORAGEPOOLHOSTSPARECAPACITY() {
        return STORAGEPOOLHOSTSPARECAPACITY;
    }

    public void setSTORAGEPOOLHOSTSPARECAPACITY(String sTORAGEPOOLHOSTSPARECAPACITY) {
        STORAGEPOOLHOSTSPARECAPACITY = sTORAGEPOOLHOSTSPARECAPACITY;
    }

    public String getSTORAGEPOOLRAWCAPACITY() {
        return STORAGEPOOLRAWCAPACITY;
    }

    public void setSTORAGEPOOLRAWCAPACITY(String sTORAGEPOOLRAWCAPACITY) {
        STORAGEPOOLRAWCAPACITY = sTORAGEPOOLRAWCAPACITY;
    }

    public String getSTORAGEPOOLUSEDCAPACITY() {
        return STORAGEPOOLUSEDCAPACITY;
    }

    public void setSTORAGEPOOLUSEDCAPACITY(String sTORAGEPOOLUSEDCAPACITY) {
        STORAGEPOOLUSEDCAPACITY = sTORAGEPOOLUSEDCAPACITY;
    }

    public String getTHICKLUNSALLOCATECAPACITY() {
        return THICKLUNSALLOCATECAPACITY;
    }

    public void setTHICKLUNSALLOCATECAPACITY(String tHICKLUNSALLOCATECAPACITY) {
        THICKLUNSALLOCATECAPACITY = tHICKLUNSALLOCATECAPACITY;
    }

    public String getTHICKLUNSUSEDCAPACITY() {
        return THICKLUNSUSEDCAPACITY;
    }

    public void setTHICKLUNSUSEDCAPACITY(String tHICKLUNSUSEDCAPACITY) {
        THICKLUNSUSEDCAPACITY = tHICKLUNSUSEDCAPACITY;
    }

    public String getTHINLUNSALLOCATECAPACITY() {
        return THINLUNSALLOCATECAPACITY;
    }

    public void setTHINLUNSALLOCATECAPACITY(String tHINLUNSALLOCATECAPACITY) {
        THINLUNSALLOCATECAPACITY = tHINLUNSALLOCATECAPACITY;
    }

    public String getTHINLUNSMAXCAPACITY() {
        return THINLUNSMAXCAPACITY;
    }

    public void setTHINLUNSMAXCAPACITY(String tHINLUNSMAXCAPACITY) {
        THINLUNSMAXCAPACITY = tHINLUNSMAXCAPACITY;
    }

    public String getTHINLUNSUSEDCAPACITY() {
        return THINLUNSUSEDCAPACITY;
    }

    public void setTHINLUNSUSEDCAPACITY(String tHINLUNSUSEDCAPACITY) {
        THINLUNSUSEDCAPACITY = tHINLUNSUSEDCAPACITY;
    }

    public String getTOTALCAPACITY() {
        return TOTALCAPACITY;
    }

    public void setTOTALCAPACITY(String tOTALCAPACITY) {
        TOTALCAPACITY = tOTALCAPACITY;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }

    public String getUNAVAILABLEDISKSCAPACITY() {
        return UNAVAILABLEDISKSCAPACITY;
    }

    public void setUNAVAILABLEDISKSCAPACITY(String uNAVAILABLEDISKSCAPACITY) {
        UNAVAILABLEDISKSCAPACITY = uNAVAILABLEDISKSCAPACITY;
    }

    public String getUSEDCAPACITY() {
        return USEDCAPACITY;
    }

    public void setUSEDCAPACITY(String uSEDCAPACITY) {
        USEDCAPACITY = uSEDCAPACITY;
    }

    public String getVASA_ALTERNATE_NAME() {
        return VASA_ALTERNATE_NAME;
    }

    public void setVASA_ALTERNATE_NAME(String vASA_ALTERNATE_NAME) {
        VASA_ALTERNATE_NAME = vASA_ALTERNATE_NAME;
    }

    public String getVASA_SUPPORT_BLOCK() {
        return VASA_SUPPORT_BLOCK;
    }

    public void setVASA_SUPPORT_BLOCK(String vASA_SUPPORT_BLOCK) {
        VASA_SUPPORT_BLOCK = vASA_SUPPORT_BLOCK;
    }

    public String getVASA_SUPPORT_FILESYSTEM() {
        return VASA_SUPPORT_FILESYSTEM;
    }

    public void setVASA_SUPPORT_FILESYSTEM(String vASA_SUPPORT_FILESYSTEM) {
        VASA_SUPPORT_FILESYSTEM = vASA_SUPPORT_FILESYSTEM;
    }

    public String getVASA_SUPPORT_PROFILE() {
        return VASA_SUPPORT_PROFILE;
    }

    public void setVASA_SUPPORT_PROFILE(String vASA_SUPPORT_PROFILE) {
        VASA_SUPPORT_PROFILE = vASA_SUPPORT_PROFILE;
    }

    public String getWRITETHROUGHSW() {
        return WRITETHROUGHSW;
    }

    public void setWRITETHROUGHSW(String wRITETHROUGHSW) {
        WRITETHROUGHSW = wRITETHROUGHSW;
    }

    public String getWRITETHROUGHTIME() {
        return WRITETHROUGHTIME;
    }

    public void setWRITETHROUGHTIME(String wRITETHROUGHTIME) {
        WRITETHROUGHTIME = wRITETHROUGHTIME;
    }

    public String getMappedLunsCountCapacity() {
        return mappedLunsCountCapacity;
    }

    public void setMappedLunsCountCapacity(String mappedLunsCountCapacity) {
        this.mappedLunsCountCapacity = mappedLunsCountCapacity;
    }

    public String getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(String patchVersion) {
        this.patchVersion = patchVersion;
    }

    public String getUnMappedLunsCountCapacity() {
        return unMappedLunsCountCapacity;
    }

    public void setUnMappedLunsCountCapacity(String unMappedLunsCountCapacity) {
        this.unMappedLunsCountCapacity = unMappedLunsCountCapacity;
    }

    public String getUserFreeCapacity() {
        return userFreeCapacity;
    }

    public void setUserFreeCapacity(String userFreeCapacity) {
        this.userFreeCapacity = userFreeCapacity;
    }

    public String getWwn() {
        return wwn;
    }

    public void setWwn(String wwn) {
        this.wwn = wwn;
    }

    @Override
    public String toString() {
        return "SystemResponseData [CACHEWRITEQUOTA=" + CACHEWRITEQUOTA + ", CONFIGMODEL=" + CONFIGMODEL
                + ", DESCRIPTION=" + DESCRIPTION + ", DOMAINNAME=" + DOMAINNAME + ", FREEDISKSCAPACITY="
                + FREEDISKSCAPACITY + ", HEALTHSTATUS=" + HEALTHSTATUS + ", HOTSPAREDISKSCAPACITY="
                + HOTSPAREDISKSCAPACITY + ", ID=" + ID + ", LOCATION=" + LOCATION + ", MEMBERDISKSCAPACITY="
                + MEMBERDISKSCAPACITY + ", NAME=" + NAME + ", PRODUCTMODE=" + PRODUCTMODE + ", PRODUCTVERSION="
                + PRODUCTVERSION + ", RUNNINGSTATUS=" + RUNNINGSTATUS + ", SECTORSIZE=" + SECTORSIZE
                + ", STORAGEPOOLCAPACITY=" + STORAGEPOOLCAPACITY + ", STORAGEPOOLFREECAPACITY="
                + STORAGEPOOLFREECAPACITY + ", STORAGEPOOLHOSTSPARECAPACITY=" + STORAGEPOOLHOSTSPARECAPACITY
                + ", STORAGEPOOLRAWCAPACITY=" + STORAGEPOOLRAWCAPACITY + ", STORAGEPOOLUSEDCAPACITY="
                + STORAGEPOOLUSEDCAPACITY + ", THICKLUNSALLOCATECAPACITY=" + THICKLUNSALLOCATECAPACITY
                + ", THICKLUNSUSEDCAPACITY=" + THICKLUNSUSEDCAPACITY + ", THINLUNSALLOCATECAPACITY="
                + THINLUNSALLOCATECAPACITY + ", THINLUNSMAXCAPACITY=" + THINLUNSMAXCAPACITY + ", THINLUNSUSEDCAPACITY="
                + THINLUNSUSEDCAPACITY + ", TOTALCAPACITY=" + TOTALCAPACITY + ", TYPE=" + TYPE
                + ", UNAVAILABLEDISKSCAPACITY=" + UNAVAILABLEDISKSCAPACITY + ", USEDCAPACITY=" + USEDCAPACITY
                + ", VASA_ALTERNATE_NAME=" + VASA_ALTERNATE_NAME + ", VASA_SUPPORT_BLOCK=" + VASA_SUPPORT_BLOCK
                + ", VASA_SUPPORT_FILESYSTEM=" + VASA_SUPPORT_FILESYSTEM + ", VASA_SUPPORT_PROFILE="
                + VASA_SUPPORT_PROFILE + ", WRITETHROUGHSW=" + WRITETHROUGHSW + ", WRITETHROUGHTIME=" + WRITETHROUGHTIME
                + ", mappedLunsCountCapacity=" + mappedLunsCountCapacity + ", patchVersion=" + patchVersion
                + ", unMappedLunsCountCapacity=" + unMappedLunsCountCapacity + ", userFreeCapacity=" + userFreeCapacity
                + ", wwn=" + wwn + "]";
    }

}
