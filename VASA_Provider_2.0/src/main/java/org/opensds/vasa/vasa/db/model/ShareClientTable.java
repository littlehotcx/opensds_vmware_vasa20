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

package org.opensds.vasa.vasa.db.model;

public class ShareClientTable {

    private String shareProperty;
    private String shareId;

    public ShareClientTable(String shareProperty, String shareId) {
        super();
        this.shareProperty = shareProperty;
        this.shareId = shareId;
    }


    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareProperty() {
        return shareProperty;
    }

    public void setShareProperty(String shareProperty) {
        this.shareProperty = shareProperty;
    }

    @Override
    public String toString() {
        return "ShareClientTable [shareProperty=" + shareProperty + ", shareId=" + shareId + "]";
    }
}
