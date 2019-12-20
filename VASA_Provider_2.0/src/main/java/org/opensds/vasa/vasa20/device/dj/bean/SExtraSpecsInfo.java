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

public class SExtraSpecsInfo {
    private String thin;

    private String vvol_creation;

    private String max_iops;

    public String getThin() {
        return thin;
    }

    public void setThin(String thin) {
        this.thin = thin;
    }

    public String getVvol_creation() {
        return vvol_creation;
    }

    public void setVvol_creation(String vvol_creation) {
        this.vvol_creation = vvol_creation;
    }

    public String getMax_iops() {
        return max_iops;
    }

    public void setMax_iops(String max_iops) {
        this.max_iops = max_iops;
    }

}
