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

public class SBitmap {
    @JsonProperty
    private String VVOLID;

    @JsonProperty
    private String BASEVVOLID;

    @JsonProperty
    private long SEGMENTSTARTOFFSETBYTES;

    @JsonProperty
    private long SEGMENTLENGTHBYTES;

    @JsonProperty
    private long CHUNKSIZEBYTES;

    @JsonProperty
    private long UNSHAREDCHUNKS;

    @JsonProperty
    private long SCANNEDCHUNKS;

    @JsonProperty
    private String CHUNKBITMAP;


    public String getVVOLID() {
        return VVOLID;
    }

    public void setVVOLID(String vVOLID) {
        VVOLID = vVOLID;
    }

    public String getBASEVVOLID() {
        return BASEVVOLID;
    }

    public void setBASEVVOLID(String bASEVVOLID) {
        BASEVVOLID = bASEVVOLID;
    }

    public long getSEGMENTSTARTOFFSETBYTES() {
        return SEGMENTSTARTOFFSETBYTES;
    }

    public void setSEGMENTSTARTOFFSETBYTES(long sEGMENTSTARTOFFSETBYTES) {
        SEGMENTSTARTOFFSETBYTES = sEGMENTSTARTOFFSETBYTES;
    }

    public long getSEGMENTLENGTHBYTES() {
        return SEGMENTLENGTHBYTES;
    }

    public void setSEGMENTLENGTHBYTES(long sEGMENTLENGTHBYTES) {
        SEGMENTLENGTHBYTES = sEGMENTLENGTHBYTES;
    }

    public long getCHUNKSIZEBYTES() {
        return CHUNKSIZEBYTES;
    }

    public void setCHUNKSIZEBYTES(long cHUNKSIZEBYTES) {
        CHUNKSIZEBYTES = cHUNKSIZEBYTES;
    }

    public long getUNSHAREDCHUNKS() {
        return UNSHAREDCHUNKS;
    }

    public void setUNSHAREDCHUNKS(long uNSHAREDCHUNKS) {
        UNSHAREDCHUNKS = uNSHAREDCHUNKS;
    }

    public long getSCANNEDCHUNKS() {
        return SCANNEDCHUNKS;
    }

    public void setSCANNEDCHUNKS(long sCANNEDCHUNKS) {
        SCANNEDCHUNKS = sCANNEDCHUNKS;
    }

    public String getCHUNKBITMAP() {
        return CHUNKBITMAP;
    }

    public void setCHUNKBITMAP(String cHUNKBITMAP) {
        CHUNKBITMAP = cHUNKBITMAP;
    }

}
