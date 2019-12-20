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

public class QueryStoragePoolResponse extends ResponseHeader {
    private int count;
    private List<StoragePoolRestBean> storagePools;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<StoragePoolRestBean> getStoragePools() {
        return storagePools;
    }

    public void setStoragePools(List<StoragePoolRestBean> storagePools) {
        this.storagePools = storagePools;
    }

    @Override
    public String toString() {
        return "QueryStoragePoolResponse [count=" + count + ", storagePools=" + storagePools + ", resultCode="
                + resultCode + ", resultDescription=" + resultDescription + "]";
    }
}
