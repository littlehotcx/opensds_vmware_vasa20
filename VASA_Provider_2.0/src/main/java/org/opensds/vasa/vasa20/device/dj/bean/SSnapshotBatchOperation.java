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

import java.util.List;

public class SSnapshotBatchOperation {
    private List<String> SNAPSHOTLIST;

    private List<String> SNAPSHOT2LIST;

    private List<String> SNAPSHOT3LIST;

    public List<String> getSNAPSHOTLIST() {
        return SNAPSHOTLIST;
    }

    public void setSNAPSHOTLIST(List<String> sNAPSHOTLIST) {
        SNAPSHOTLIST = sNAPSHOTLIST;
    }

    public List<String> getSNAPSHOT2LIST() {
        return SNAPSHOT2LIST;
    }

    public void setSNAPSHOT2LIST(List<String> sNAPSHOT2LIST) {
        SNAPSHOT2LIST = sNAPSHOT2LIST;
    }

    public List<String> getSNAPSHOT3LIST() {
        return SNAPSHOT3LIST;
    }

    public void setSNAPSHOT3LIST(List<String> sNAPSHOT3LIST) {
        SNAPSHOT3LIST = sNAPSHOT3LIST;
    }

}
