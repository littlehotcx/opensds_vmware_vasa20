

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

package org.opensds.vasa.vasa20.device.array.NFSshare;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-02
 */
public class AddAuthClientReqBean {

    private String NAME;

    private String PARENTID;

    private int ACCESSVAL;

    private int SYNC;

    private int ALLSQUASH;

    private int ROOTSQUASH;

    // private String VSTOREID;

    @Override
    public String toString() {
        return "AddAuthClientReqBean{" +
                "NAME='" + NAME + '\'' +
                ", PARENTID='" + PARENTID + '\'' +
                ", ACCESSVAL=" + ACCESSVAL +
                ", SYNC=" + SYNC +
                ", ALLSQUASH=" + ALLSQUASH +
                ", ROOTSQUASH=" + ROOTSQUASH +
                ",'" + '\'' +
                '}';
    }

    public int getACCESSVAL() {
        return ACCESSVAL;
    }

    public void setACCESSVAL(int ACCESSVAL) {
        this.ACCESSVAL = ACCESSVAL;
    }

    public int getSYNC() {
        return SYNC;
    }

    public void setSYNC(int SYNC) {
        this.SYNC = SYNC;
    }

    public int getALLSQUASH() {
        return ALLSQUASH;
    }

    public void setALLSQUASH(int ALLSQUASH) {
        this.ALLSQUASH = ALLSQUASH;
    }

    public int getROOTSQUASH() {
        return ROOTSQUASH;
    }

    public void setROOTSQUASH(int ROOTSQUASH) {
        this.ROOTSQUASH = ROOTSQUASH;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String PARENTID) {
        this.PARENTID = PARENTID;
    }



/*    public String getVSTOREID() {
        return VSTOREID;
    }

    public void setVSTOREID(String VSTOREID) {
        this.VSTOREID = VSTOREID;
    }*/
}
