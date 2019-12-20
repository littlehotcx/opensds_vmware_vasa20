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

public class QueryStorageProfileResponse extends ResponseHeader {
    private List<StorageProfileRestBean> storageProfile;
    private int count;


    public List<StorageProfileRestBean> getStorageProfile() {
        return storageProfile;
    }

    public void setStorageProfile(List<StorageProfileRestBean> storageProfile) {
        this.storageProfile = storageProfile;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
