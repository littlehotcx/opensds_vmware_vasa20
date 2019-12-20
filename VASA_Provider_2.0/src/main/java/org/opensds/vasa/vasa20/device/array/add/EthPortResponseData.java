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

public class EthPortResponseData {
    @JsonProperty
    private String BONDID;
    @JsonProperty
    private String BONDNAME;
    @JsonProperty
    private String ERRORPACKETS;
    @JsonProperty
    private String ETHDUPLEX;
    @JsonProperty
    private String ETHNEGOTIATE;
    @JsonProperty
    private String HEALTHSTATUS;
    @JsonProperty
    private String ID;
    @JsonProperty
    private String INIORTGT;
    @JsonProperty
    private String IPV4ADDR;
    @JsonProperty
    private String IPV4GATEWAY;
    @JsonProperty
    private String IPV4MASK;
    @JsonProperty
    private String IPV6ADDR;
    @JsonProperty
    private String IPV6GATEWAY;
    @JsonProperty
    private String IPV6MASK;
    @JsonProperty
    private String ISCSINAME;
    @JsonProperty
    private String ISCSITCPPORT;
    @JsonProperty
    private String LOCATION;
    @JsonProperty
    private String LOGICTYPE;
    @JsonProperty
    private String LOSTPACKETS;
    @JsonProperty
    private String MACADDRESS;
    @JsonProperty
    private String MTU;
    @JsonProperty
    private String NAME;
    @JsonProperty
    private String OVERFLOWEDPACKETS;
    @JsonProperty
    private String PARENTID;
    @JsonProperty
    private String PARENTTYPE;
    @JsonProperty
    private String PORTSWITCH;
    @JsonProperty
    private String RUNNINGSTATUS;
    @JsonProperty
    private String SPEED;
    @JsonProperty
    private String STARTTIME;
    @JsonProperty
    private String TYPE;
    @JsonProperty
    private String crcErrors;
    private String dswId;
    private String dswLinkRight;
    private String frameErrors;
    private String frameLengthErrors;
    private String lightStatus;
    private String maxSpeed;
    private String selectType;
    private String zoneId;

    public String getBONDID() {
        return BONDID;
    }

    public void setBONDID(String bONDID) {
        BONDID = bONDID;
    }

    public String getBONDNAME() {
        return BONDNAME;
    }

    public void setBONDNAME(String bONDNAME) {
        BONDNAME = bONDNAME;
    }

    public String getERRORPACKETS() {
        return ERRORPACKETS;
    }

    public void setERRORPACKETS(String eRRORPACKETS) {
        ERRORPACKETS = eRRORPACKETS;
    }

    public String getETHDUPLEX() {
        return ETHDUPLEX;
    }

    public void setETHDUPLEX(String eTHDUPLEX) {
        ETHDUPLEX = eTHDUPLEX;
    }

    public String getETHNEGOTIATE() {
        return ETHNEGOTIATE;
    }

    public void setETHNEGOTIATE(String eTHNEGOTIATE) {
        ETHNEGOTIATE = eTHNEGOTIATE;
    }

    public String getHEALTHSTATUS() {
        return HEALTHSTATUS;
    }

    public void setHEALTHSTATUS(String hEALTHSTATUS) {
        HEALTHSTATUS = hEALTHSTATUS;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getINIORTGT() {
        return INIORTGT;
    }

    public void setINIORTGT(String iNIORTGT) {
        INIORTGT = iNIORTGT;
    }

    public String getIPV4ADDR() {
        return IPV4ADDR;
    }

    public void setIPV4ADDR(String iPV4ADDR) {
        IPV4ADDR = iPV4ADDR;
    }

    public String getIPV4GATEWAY() {
        return IPV4GATEWAY;
    }

    public void setIPV4GATEWAY(String iPV4GATEWAY) {
        IPV4GATEWAY = iPV4GATEWAY;
    }

    public String getIPV4MASK() {
        return IPV4MASK;
    }

    public void setIPV4MASK(String iPV4MASK) {
        IPV4MASK = iPV4MASK;
    }

    public String getIPV6ADDR() {
        return IPV6ADDR;
    }

    public void setIPV6ADDR(String iPV6ADDR) {
        IPV6ADDR = iPV6ADDR;
    }

    public String getIPV6GATEWAY() {
        return IPV6GATEWAY;
    }

    public void setIPV6GATEWAY(String iPV6GATEWAY) {
        IPV6GATEWAY = iPV6GATEWAY;
    }

    public String getIPV6MASK() {
        return IPV6MASK;
    }

    public void setIPV6MASK(String iPV6MASK) {
        IPV6MASK = iPV6MASK;
    }

    public String getISCSINAME() {
        return ISCSINAME;
    }

    public void setISCSINAME(String iSCSINAME) {
        ISCSINAME = iSCSINAME;
    }

    public String getISCSITCPPORT() {
        return ISCSITCPPORT;
    }

    public void setISCSITCPPORT(String iSCSITCPPORT) {
        ISCSITCPPORT = iSCSITCPPORT;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    public String getLOGICTYPE() {
        return LOGICTYPE;
    }

    public void setLOGICTYPE(String lOGICTYPE) {
        LOGICTYPE = lOGICTYPE;
    }

    public String getLOSTPACKETS() {
        return LOSTPACKETS;
    }

    public void setLOSTPACKETS(String lOSTPACKETS) {
        LOSTPACKETS = lOSTPACKETS;
    }

    public String getMACADDRESS() {
        return MACADDRESS;
    }

    public void setMACADDRESS(String mACADDRESS) {
        MACADDRESS = mACADDRESS;
    }

    public String getMTU() {
        return MTU;
    }

    public void setMTU(String mTU) {
        MTU = mTU;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getOVERFLOWEDPACKETS() {
        return OVERFLOWEDPACKETS;
    }

    public void setOVERFLOWEDPACKETS(String oVERFLOWEDPACKETS) {
        OVERFLOWEDPACKETS = oVERFLOWEDPACKETS;
    }

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String pARENTID) {
        PARENTID = pARENTID;
    }

    public String getPARENTTYPE() {
        return PARENTTYPE;
    }

    public void setPARENTTYPE(String pARENTTYPE) {
        PARENTTYPE = pARENTTYPE;
    }

    public String getPORTSWITCH() {
        return PORTSWITCH;
    }

    public void setPORTSWITCH(String pORTSWITCH) {
        PORTSWITCH = pORTSWITCH;
    }

    public String getRUNNINGSTATUS() {
        return RUNNINGSTATUS;
    }

    public void setRUNNINGSTATUS(String rUNNINGSTATUS) {
        RUNNINGSTATUS = rUNNINGSTATUS;
    }

    public String getSPEED() {
        return SPEED;
    }

    public void setSPEED(String sPEED) {
        SPEED = sPEED;
    }

    public String getSTARTTIME() {
        return STARTTIME;
    }

    public void setSTARTTIME(String sTARTTIME) {
        STARTTIME = sTARTTIME;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }

    public String getCrcErrors() {
        return crcErrors;
    }

    public void setCrcErrors(String crcErrors) {
        this.crcErrors = crcErrors;
    }

    public String getDswId() {
        return dswId;
    }

    public void setDswId(String dswId) {
        this.dswId = dswId;
    }

    public String getDswLinkRight() {
        return dswLinkRight;
    }

    public void setDswLinkRight(String dswLinkRight) {
        this.dswLinkRight = dswLinkRight;
    }

    public String getFrameErrors() {
        return frameErrors;
    }

    public void setFrameErrors(String frameErrors) {
        this.frameErrors = frameErrors;
    }

    public String getFrameLengthErrors() {
        return frameLengthErrors;
    }

    public void setFrameLengthErrors(String frameLengthErrors) {
        this.frameLengthErrors = frameLengthErrors;
    }

    public String getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(String lightStatus) {
        this.lightStatus = lightStatus;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public String toString() {
        return "EthPortResponseData [BONDID=" + BONDID + ", BONDNAME=" + BONDNAME + ", ERRORPACKETS=" + ERRORPACKETS
                + ", ETHDUPLEX=" + ETHDUPLEX + ", ETHNEGOTIATE=" + ETHNEGOTIATE + ", HEALTHSTATUS=" + HEALTHSTATUS
                + ", ID=" + ID + ", INIORTGT=" + INIORTGT + ", IPV4ADDR=" + IPV4ADDR + ", IPV4GATEWAY=" + IPV4GATEWAY
                + ", IPV4MASK=" + IPV4MASK + ", IPV6ADDR=" + IPV6ADDR + ", IPV6GATEWAY=" + IPV6GATEWAY + ", IPV6MASK="
                + IPV6MASK + ", ISCSINAME=" + ISCSINAME + ", ISCSITCPPORT=" + ISCSITCPPORT + ", LOCATION=" + LOCATION
                + ", LOGICTYPE=" + LOGICTYPE + ", LOSTPACKETS=" + LOSTPACKETS + ", MACADDRESS=" + MACADDRESS + ", MTU="
                + MTU + ", NAME=" + NAME + ", OVERFLOWEDPACKETS=" + OVERFLOWEDPACKETS + ", PARENTID=" + PARENTID
                + ", PARENTTYPE=" + PARENTTYPE + ", PORTSWITCH=" + PORTSWITCH + ", RUNNINGSTATUS=" + RUNNINGSTATUS
                + ", SPEED=" + SPEED + ", STARTTIME=" + STARTTIME + ", TYPE=" + TYPE + ", crcErrors=" + crcErrors
                + ", dswId=" + dswId + ", dswLinkRight=" + dswLinkRight + ", frameErrors=" + frameErrors
                + ", frameLengthErrors=" + frameLengthErrors + ", lightStatus=" + lightStatus + ", maxSpeed=" + maxSpeed
                + ", selectType=" + selectType + ", zoneId=" + zoneId + "]";
    }

}
