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

public class UnbindFaultDataResponse extends ResponseHeader {
    private String vvolName;

    private String vvolType;

    private String sourceType;

    private String vvolId;

    public void setVvolName(String vvolName) {
        this.vvolName = vvolName;
    }

    public String getVvolName() {
        return vvolName;
    }

    public void setVvolType(String vvolType) {
        this.vvolType = vvolType;
    }

    public String getVvolType() {
        return vvolType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setVvolId(String vvolId) {
        this.vvolId = vvolId;
    }

    public String getVvolId() {
        return vvolId;
    }

}
