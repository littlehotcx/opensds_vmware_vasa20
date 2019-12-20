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

import java.util.ArrayList;
import java.util.List;

import org.opensds.vasa.vasa.db.model.NStorageProfileLevel;

public class StorageProfileLevelResult extends ResponseHeader {

    private List<NStorageProfileLevel> beans = new ArrayList<NStorageProfileLevel>();

    public List<NStorageProfileLevel> getBeans() {
        return beans;
    }

    public void setBeans(List<NStorageProfileLevel> beans) {
        this.beans = beans;
    }

    public void addData(NStorageProfileLevel nStorageProfileLevel) {
        if (null == beans) {
            beans = new ArrayList<NStorageProfileLevel>();
        }
        beans.add(nStorageProfileLevel);
    }

}
