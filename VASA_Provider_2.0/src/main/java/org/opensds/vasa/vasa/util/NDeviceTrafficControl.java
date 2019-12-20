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

package org.opensds.vasa.vasa.util;

public class NDeviceTrafficControl {

    private String arrayId;
    private String sn;
    private int maxRequestConcurrencyNum;
    private int currentRequestConcurrencyNum;

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getMaxRequestConcurrencyNum() {
        return maxRequestConcurrencyNum;
    }

    public void setMaxRequestConcurrencyNum(int maxRequestConcurrencyNum) {
        this.maxRequestConcurrencyNum = maxRequestConcurrencyNum;
    }

    public int getCurrentRequestConcurrencyNum() {
        return currentRequestConcurrencyNum;
    }

    public void setCurrentRequestConcurrencyNum(int currentRequestConcurrencyNum) {
        this.currentRequestConcurrencyNum = currentRequestConcurrencyNum;
    }

    @Override
    public String toString() {
        return "NDeviceTrafficControl [arrayId=" + arrayId + ", sn=" + sn + ", maxRequestConcurrencyNum="
                + maxRequestConcurrencyNum + ", currentRequestConcurrencyNum=" + currentRequestConcurrencyNum + "]";
    }
}
