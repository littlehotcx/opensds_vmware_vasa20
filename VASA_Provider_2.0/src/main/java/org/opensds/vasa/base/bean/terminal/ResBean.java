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

package org.opensds.vasa.base.bean.terminal;

public class ResBean {
    private long errorCode;

    private String description;

    private Object resData;

    private Object errData;

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getResData() {
        return resData;
    }

    public void setResData(Object resData) {
        this.resData = resData;
    }

    public Object getErrData() {
        return errData;
    }

    public void setErrData(Object errData) {
        this.errData = errData;
    }

    @Override
    public String toString() {
        return "ResBean [errorCode=" + errorCode + ", description=" + description + ", resData=" + resData
                + ", errData=" + errData + "]";
    }


}
