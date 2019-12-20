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

public class IsmStoragePort extends DPort {
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2020579537100237231L;


    //控制器ID,这个ID是VASA Provider的产生的ID
    private String processorUniquID = "";


    /**
     * 方法 ： getProcessorUniquIdentifier
     *
     * @return String 返回结果
     */
    public String getProcessorUniquIdentifier() {
        return processorUniquID;
    }

    /**
     * 方法 ： setProcessorUniquIdentifier
     *
     * @param processorUniquIDIn 方法参数：processorUniquIDIn
     */
    public void setProcessorUniquIdentifier(String processorUniquIDIn) {
        this.processorUniquID = processorUniquIDIn;
    }
}
