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

package org.opensds.vasa.vasa20.device.array.qos;

import java.util.List;

public class QosUpdateReqBean {

    private String ID;
    private String NAME;
    private String DESCRIPTION;
    private String IOTYPE;
    private Long MAXBANDWIDTH;
    private Long MINBANDWIDTH;
    private Long MAXIOPS;
    private Long MINIOPS;
    private Long LATENCY;
    private List<String> LUNLIST;
    private int SCHEDULEPOLICY = 2;
    private long SCHEDULESTARTTIME = 100800;
    private String STARTTIME = "08:00";
    private String DURATION = "86400";
    private String CYCLESET = "[1,2,3,4,5,6,0]";

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String getIOTYPE() {
        return IOTYPE;
    }

    public void setIOTYPE(String iOTYPE) {
        IOTYPE = iOTYPE;
    }

    public Long getMAXBANDWIDTH() {
        return MAXBANDWIDTH;
    }

    public void setMAXBANDWIDTH(Long mAXBANDWIDTH) {
        MAXBANDWIDTH = mAXBANDWIDTH;
    }

    public Long getMINBANDWIDTH() {
        return MINBANDWIDTH;
    }

    public void setMINBANDWIDTH(Long mINBANDWIDTH) {
        MINBANDWIDTH = mINBANDWIDTH;
    }

    public Long getMAXIOPS() {
        return MAXIOPS;
    }

    public void setMAXIOPS(Long mAXIOPS) {
        MAXIOPS = mAXIOPS;
    }

    public Long getMINIOPS() {
        return MINIOPS;
    }

    public void setMINIOPS(Long mINIOPS) {
        MINIOPS = mINIOPS;
    }

    public Long getLATENCY() {
        return LATENCY;
    }

    public void setLATENCY(Long lATENCY) {
        LATENCY = lATENCY;
    }

    public List<String> getLUNLIST() {
        return LUNLIST;
    }

    public void setLUNLIST(List<String> lUNLIST) {
        LUNLIST = lUNLIST;
    }

    public int getSCHEDULEPOLICY() {
        return SCHEDULEPOLICY;
    }

    public void setSCHEDULEPOLICY(int sCHEDULEPOLICY) {
        SCHEDULEPOLICY = sCHEDULEPOLICY;
    }

    public long getSCHEDULESTARTTIME() {
        return SCHEDULESTARTTIME;
    }

    public void setSCHEDULESTARTTIME(long sCHEDULESTARTTIME) {
        SCHEDULESTARTTIME = sCHEDULESTARTTIME;
    }

    public String getSTARTTIME() {
        return STARTTIME;
    }

    public void setSTARTTIME(String sTARTTIME) {
        STARTTIME = sTARTTIME;
    }

    public String getDURATION() {
        return DURATION;
    }

    public void setDURATION(String dURATION) {
        DURATION = dURATION;
    }

    public String getCYCLESET() {
        return CYCLESET;
    }

    public void setCYCLESET(String cYCLESET) {
        CYCLESET = cYCLESET;
    }


}
