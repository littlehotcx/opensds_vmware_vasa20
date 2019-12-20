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

package org.opensds.vasa.vasa20.device.array.fileSystem;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
class FileSystemCreateReqBean {

    private String NAME;

    private String PARENTID;

    private String ALLOCTYPE;

    private long CAPACITY;

    private int SECTORSIZE;

    private String SPACESELFADJUSTINGMODE;

    private int SNAPSHOTRESERVEPER;// 0

//    private String OWNINGCONTROLLER;

    private boolean ISCLONEFS;

//    private String vstoreId;

    private int AUTOSHRINKTHRESHOLDPERCENT;//50

    private int AUTOGROWTHRESHOLDPERCENT;//95

//    private Long MINAUTOSIZE;//defualt

    private Long MAXAUTOSIZE;//设置为所有卷的累加和 * 1.2

    private Long AUTOSIZEINCREMENT;//2G

    @Override
    public String toString() {
        return "FileSystemCreateReqBean{" +
                "NAME='" + NAME + '\'' +
                ", PARENTID='" + PARENTID + '\'' +
                ", ALLOCTYPE='" + ALLOCTYPE + '\'' +
                ", CAPACITY=" + CAPACITY +
                ", SECTORSIZE=" + SECTORSIZE +
                ", SPACESELFADJUSTINGMODE=" + SPACESELFADJUSTINGMODE +
                ", SNAPSHOTRESERVEPER=" + SNAPSHOTRESERVEPER +
                ", ISCLONEFS=" + ISCLONEFS +
                ", AUTOSHRINKTHRESHOLDPERCENT=" + AUTOSHRINKTHRESHOLDPERCENT +
                ", AUTOGROWTHRESHOLDPERCENT=" + AUTOGROWTHRESHOLDPERCENT +
                ", MAXAUTOSIZE=" + MAXAUTOSIZE +
                ", AUTOSIZEINCREMENT=" + AUTOSIZEINCREMENT +
                '}';
    }

    public int getSNAPSHOTRESERVEPER() {
        return SNAPSHOTRESERVEPER;
    }

    public void setSNAPSHOTRESERVEPER(int SNAPSHOTRESERVEPER) {
        this.SNAPSHOTRESERVEPER = SNAPSHOTRESERVEPER;
    }


    public boolean isISCLONEFS() {
        return ISCLONEFS;
    }

    public void setISCLONEFS(boolean ISCLONEFS) {
        this.ISCLONEFS = ISCLONEFS;
    }


    public int getAUTOSHRINKTHRESHOLDPERCENT() {
        return AUTOSHRINKTHRESHOLDPERCENT;
    }

    public void setAUTOSHRINKTHRESHOLDPERCENT(int AUTOSHRINKTHRESHOLDPERCENT) {
        this.AUTOSHRINKTHRESHOLDPERCENT = AUTOSHRINKTHRESHOLDPERCENT;
    }

    public int getAUTOGROWTHRESHOLDPERCENT() {
        return AUTOGROWTHRESHOLDPERCENT;
    }

    public void setAUTOGROWTHRESHOLDPERCENT(int AUTOGROWTHRESHOLDPERCENT) {
        this.AUTOGROWTHRESHOLDPERCENT = AUTOGROWTHRESHOLDPERCENT;
    }

    public Long getMAXAUTOSIZE() {
        return MAXAUTOSIZE;
    }

    public void setMAXAUTOSIZE(Long MAXAUTOSIZE) {
        this.MAXAUTOSIZE = MAXAUTOSIZE;
    }

    public Long getAUTOSIZEINCREMENT() {
        return AUTOSIZEINCREMENT;
    }

    public void setAUTOSIZEINCREMENT(Long AUTOSIZEINCREMENT) {
        this.AUTOSIZEINCREMENT = AUTOSIZEINCREMENT;
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

    public String getALLOCTYPE() {
        return ALLOCTYPE;
    }

    public void setALLOCTYPE(String ALLOCTYPE) {
        this.ALLOCTYPE = ALLOCTYPE;
    }

    public long getCAPACITY() {
        return CAPACITY;
    }

    public void setCAPACITY(long CAPACITY) {
        this.CAPACITY = CAPACITY;
    }

    public int getSECTORSIZE() {
        return SECTORSIZE;
    }

    public void setSECTORSIZE(int SECTORSIZE) {
        this.SECTORSIZE = SECTORSIZE;
    }

    public String getSPACESELFADJUSTINGMODE() {
        return SPACESELFADJUSTINGMODE;
    }

    public void setSPACESELFADJUSTINGMODE(String SPACESELFADJUSTINGMODE) {
        this.SPACESELFADJUSTINGMODE = SPACESELFADJUSTINGMODE;
    }
}
