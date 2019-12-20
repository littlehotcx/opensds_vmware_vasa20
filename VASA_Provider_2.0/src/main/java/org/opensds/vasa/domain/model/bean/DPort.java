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

import java.util.ArrayList;
import java.util.List;

public class DPort extends DBaseStorageEntity {

    protected List<String> alternateName;

    protected String iscsiIdentifier;

    protected String nodeWwn;

    protected String portType;

    protected String portWwn;

    public List<String> getAlternateName() {
        if (this.alternateName == null)
            this.alternateName = new ArrayList();
        return this.alternateName;
    }

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

    public String getPortType() {
        return this.portType;
    }

    public void setPortType(String paramString) {
        this.portType = paramString;
    }

    public String getPortWwn() {
        return this.portWwn;
    }

    public void setPortWwn(String paramString) {
        this.portWwn = paramString;
    }

    @Override
    public String toString() {
        return "DPort [alternateName=" + alternateName + ", iscsiIdentifier=" + iscsiIdentifier + ", nodeWwn=" + nodeWwn
                + ", portType=" + portType + ", portWwn=" + portWwn + "]";
    }
}
