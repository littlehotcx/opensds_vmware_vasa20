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

package org.opensds.vasa.vasa20.device.dj.bean;

import java.util.List;

public class AccessInfo {
    private TokenInfo token;

    private List<ServiceCatalogInfo> serviceCatalog;

    private UserInfo user;

    private MetaDataInfo metadata;

    public TokenInfo getToken() {
        return token;
    }

    public void setToken(TokenInfo token) {
        this.token = token;
    }

    public List<ServiceCatalogInfo> getServiceCatalog() {
        return serviceCatalog;
    }

    public void setServiceCatalog(List<ServiceCatalogInfo> serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public MetaDataInfo getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaDataInfo metadata) {
        this.metadata = metadata;
    }

}
