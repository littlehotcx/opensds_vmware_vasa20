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

package org.opensds.vasa.vasa20.device.array.snapshot;

public class SnapshotCreateReqBean {

    private String NAME;
    private String PARENTID;
    private String DESCRIPTION;
    private String SUBTYPE;
    private String PARENTTYPE;

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

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String sUBTYPE) {
        SUBTYPE = sUBTYPE;
    }

    public String getPARENTTYPE() {
        return PARENTTYPE;
    }

    public void setPARENTTYPE(String pARENTTYPE) {
        PARENTTYPE = pARENTTYPE;
    }

    @Override
    public String toString() {
        return "SnapshotCreateReqBean [NAME=" + NAME + ", PARENTID=" + PARENTID
                + ", DESCRIPTION=" + DESCRIPTION + ", SUBTYPE=" + SUBTYPE
                + ", PARENTTYPE=" + PARENTTYPE + "]";
    }


}
