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

package org.opensds.vasa.vasa20.device.array.lun;


public class LunCopyCreateReqBean {

    private Integer TYPE = 219;
    private String NAME;
    private String DESCRIPTION;
    private String SOURCELUN;
    private String TARGETLUN;
    private String SUBTYPE;
    private String BASELUN;
    private int COPYSPEED;
    private String LUNCOPYTYPE = "1";

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

    public String getSOURCELUN() {
        return SOURCELUN;
    }

    public void setSOURCELUN(String sOURCELUN) {
        SOURCELUN = sOURCELUN;
    }

    public String getTARGETLUN() {
        return TARGETLUN;
    }

    public void setTARGETLUN(String tARGETLUN) {
        TARGETLUN = tARGETLUN;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String sUBTYPE) {
        SUBTYPE = sUBTYPE;
    }

    public String getBASELUN() {
        return BASELUN;
    }

    public void setBASELUN(String bASELUN) {
        BASELUN = bASELUN;
    }

    public int getCOPYSPEED() {
        return COPYSPEED;
    }

    public void setCOPYSPEED(int cOPYSPEED) {
        COPYSPEED = cOPYSPEED;
    }

    @Override
    public String toString() {
        return "LunCopyCreateReqBean [TYPE=" + TYPE + ", NAME=" + NAME
                + ", DESCRIPTION=" + DESCRIPTION + ", SOURCELUN=" + SOURCELUN
                + ", TARGETLUN=" + TARGETLUN + ", SUBTYPE=" + SUBTYPE
                + ", BASELUN=" + BASELUN + ", COPYSPEED=" + COPYSPEED
                + ", LUNCOPYTYPE=" + LUNCOPYTYPE + "]";
    }

    public String getLUNCOPYTYPE() {
        return LUNCOPYTYPE;
    }

    public void setLUNCOPYTYPE(String lUNCOPYTYPE) {
        LUNCOPYTYPE = lUNCOPYTYPE;
    }

    public Integer getTYPE() {
        return TYPE;
    }

    public void setTYPE(Integer tYPE) {
        TYPE = tYPE;
    }


}
