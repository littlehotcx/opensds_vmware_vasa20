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

public class QosCreateReqBean extends QosUpdateReqBean {


    private String CLASSTYPE = "1";

    public String getCLASSTYPE() {
        return CLASSTYPE;
    }

    public void setCLASSTYPE(String cLASSTYPE) {
        CLASSTYPE = cLASSTYPE;
    }

    @Override
    public String toString() {
        return "QosCreateReqBean [CLASSTYPE=" + CLASSTYPE + ", getID()=" + getID() + ", getNAME()=" + getNAME()
                + ", getDESCRIPTION()=" + getDESCRIPTION() + ", getIOTYPE()=" + getIOTYPE() + ", getMAXBANDWIDTH()="
                + getMAXBANDWIDTH() + ", getMINBANDWIDTH()=" + getMINBANDWIDTH() + ", getMAXIOPS()=" + getMAXIOPS()
                + ", getMINIOPS()=" + getMINIOPS() + ", getLATENCY()=" + getLATENCY() + ", getLUNLIST()=" + getLUNLIST()
                + ", getSCHEDULEPOLICY()=" + getSCHEDULEPOLICY() + ", getSCHEDULESTARTTIME()=" + getSCHEDULESTARTTIME()
                + ", getSTARTTIME()=" + getSTARTTIME() + ", getDURATION()=" + getDURATION() + ", getCYCLESET()="
                + getCYCLESET() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }


}
