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

public class ResponseHeader {

    public Long resultCode;
    public String resultDescription;

    public Long getResultCode() {
        return resultCode;
    }

    public void setResultCode(Long resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = Long.valueOf(resultCode);
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    @Override
    public String toString() {
        return "BaseRestResultBean [resultCode=" + resultCode
                + ", resultDescription=" + resultDescription + "]";
    }


}