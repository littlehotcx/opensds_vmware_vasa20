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

public class DHostInitiatorInfo extends DBaseStorageEntity {

    protected String iscsiIdentifier;

    protected String nodeWwn;

    protected String portWwn;

    public String getIscsiIdentifier() {
        return this.iscsiIdentifier;
    }

    public void setIscsiIdentifier(String paramString) {
        this.iscsiIdentifier = paramString;
    }

    public String getNodeWwn() {
        return this.nodeWwn;
    }

    public void setNodeWwn(String paramString) {
        this.nodeWwn = paramString;
    }

    public String getPortWwn() {
        return this.portWwn;
    }

    public void setPortWwn(String paramString) {
        this.portWwn = paramString;
    }
}
