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

package org.opensds.vasa.vasa.rest.bean;

import java.util.List;

public class DelFaultDataResutl extends ResponseHeader {
    private String vvolid;
    private String vvolName;
    private String vvolSourceType;
    private List<DeletedFailVolume> deletedFailVolume;

    public String getVvolid() {
        return vvolid;
    }

    public void setVvolid(String vvolid) {
        this.vvolid = vvolid;
    }

    public String getVvolName() {
        return vvolName;
    }

    public void setVvolName(String vvolName) {
        this.vvolName = vvolName;
    }

    public List<DeletedFailVolume> getDeletedFailVolume() {
        return deletedFailVolume;
    }

    public void setDeletedFailVolume(List<DeletedFailVolume> deletedFailVolume) {
        this.deletedFailVolume = deletedFailVolume;
    }

    public String getVvolSourceType() {
        return vvolSourceType;
    }

    public void setVvolSourceType(String vvolSourceType) {
        this.vvolSourceType = vvolSourceType;
    }

    @Override
    public String toString() {
        return "DelFaultDataResutl [vvolid=" + vvolid + ", vvolName=" + vvolName + ", vvolSourceType=" + vvolSourceType
                + ", deletedFailVolume=" + deletedFailVolume + ", resultCode=" + resultCode + ", resultDescription="
                + resultDescription + "]";
    }
}
