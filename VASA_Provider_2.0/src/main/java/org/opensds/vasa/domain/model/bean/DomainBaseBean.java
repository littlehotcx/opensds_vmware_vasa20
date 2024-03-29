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

public class DomainBaseBean {
    private Long TYPE;

    private String ID;

    private String NAME;

    private Long PARENTTYPE;

    private String PARENTID;

    private String PARENTNAME;

    private String LOCATION;

    private String HEALTHSTATUS;

    private String RUNNINGSTATUS;

    private String DESCRIPTION;

    private String COUNT;

    private Long ASSOCIATEOBJTYPE;

    private String ASSOCIATEOBJID;

    private String APPLICATION;

    private String TENANCYID;

    private String TENANCYNAME;

    private Long SUBTYPE;

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

    public String getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(String pARENTID) {
        PARENTID = pARENTID;
    }

    public String getPARENTNAME() {
        return PARENTNAME;
    }

    public void setPARENTNAME(String pARENTNAME) {
        PARENTNAME = pARENTNAME;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    public String getHEALTHSTATUS() {
        return HEALTHSTATUS;
    }

    public void setHEALTHSTATUS(String hEALTHSTATUS) {
        HEALTHSTATUS = hEALTHSTATUS;
    }

    public String getRUNNINGSTATUS() {
        return RUNNINGSTATUS;
    }

    public void setRUNNINGSTATUS(String rUNNINGSTATUS) {
        RUNNINGSTATUS = rUNNINGSTATUS;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(String cOUNT) {
        COUNT = cOUNT;
    }

    public String getASSOCIATEOBJID() {
        return ASSOCIATEOBJID;
    }

    public void setASSOCIATEOBJID(String aSSOCIATEOBJID) {
        ASSOCIATEOBJID = aSSOCIATEOBJID;
    }

    public String getAPPLICATION() {
        return APPLICATION;
    }

    public void setAPPLICATION(String aPPLICATION) {
        APPLICATION = aPPLICATION;
    }

    public String getTENANCYID() {
        return TENANCYID;
    }

    public void setTENANCYID(String tENANCYID) {
        TENANCYID = tENANCYID;
    }

    public String getTENANCYNAME() {
        return TENANCYNAME;
    }

    public void setTENANCYNAME(String tENANCYNAME) {
        TENANCYNAME = tENANCYNAME;
    }

    public Long getTYPE() {
        return TYPE;
    }

    public void setTYPE(Long tYPE) {
        TYPE = tYPE;
    }

    public Long getPARENTTYPE() {
        return PARENTTYPE;
    }

    public void setPARENTTYPE(Long pARENTTYPE) {
        PARENTTYPE = pARENTTYPE;
    }

    public Long getASSOCIATEOBJTYPE() {
        return ASSOCIATEOBJTYPE;
    }

    public void setASSOCIATEOBJTYPE(Long aSSOCIATEOBJTYPE) {
        ASSOCIATEOBJTYPE = aSSOCIATEOBJTYPE;
    }

    public Long getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(Long sUBTYPE) {
        SUBTYPE = sUBTYPE;
    }

}
