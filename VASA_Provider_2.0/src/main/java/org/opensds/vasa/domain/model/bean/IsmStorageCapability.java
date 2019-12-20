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

public class IsmStorageCapability extends DStorageCapability {
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 6818931024604518213L;

    private int storageCapabilityTypeID = -1;


    /**
     * 方法 ： getStorageCapabilityTypeID
     *
     * @return int 返回结果
     */
    public int getStorageCapabilityTypeID() {
        return storageCapabilityTypeID;
    }

    /**
     * 方法 ： setStorageCapabilityTypeID
     *
     * @param storageCapabilityType 方法参数：storageCapabilityType
     */
    public void setStorageCapabilityTypeID(int storageCapabilityType) {
        this.storageCapabilityTypeID = storageCapabilityType;
    }
}
