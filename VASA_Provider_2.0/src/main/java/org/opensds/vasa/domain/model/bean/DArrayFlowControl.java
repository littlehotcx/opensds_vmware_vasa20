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

import java.util.concurrent.Semaphore;

import org.opensds.vasa.vasa20.device.RestQosControl;

public class DArrayFlowControl {
    private String arrayId;

    private RestQosControl restQosControl;

    private Semaphore semaphore;

    private int MAX_ACCESS_COUNT;

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public RestQosControl getRestQosControl() {
        return restQosControl;
    }

    public void setRestQosControl(RestQosControl restQosControl) {
        this.restQosControl = restQosControl;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public int getMAX_ACCESS_COUNT() {
        return MAX_ACCESS_COUNT;
    }

    public void setMAX_ACCESS_COUNT(int mAX_ACCESS_COUNT) {
        MAX_ACCESS_COUNT = mAX_ACCESS_COUNT;
    }

    @Override
    public String toString() {
        return "DArrayFlowControl [arrayId=" + arrayId + ", restQosControl=" + restQosControl + ", MAX_ACCESS_COUNT="
                + MAX_ACCESS_COUNT + "]";
    }
}
