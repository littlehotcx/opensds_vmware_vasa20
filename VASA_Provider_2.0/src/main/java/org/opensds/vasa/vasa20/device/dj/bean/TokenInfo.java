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

public class TokenInfo {
    private String issued_at;

    private String expires;

    private String id;

    private TenantInfo tenant;

    private List<String> audit_ids;

    public String getIssued_at() {
        return issued_at;
    }

    public void setIssued_at(String issued_at) {
        this.issued_at = issued_at;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TenantInfo getTenant() {
        return tenant;
    }

    public void setTenant(TenantInfo tenant) {
        this.tenant = tenant;
    }

    public List<String> getAudit_ids() {
        return audit_ids;
    }

    public void setAudit_ids(List<String> audit_ids) {
        this.audit_ids = audit_ids;
    }

}
