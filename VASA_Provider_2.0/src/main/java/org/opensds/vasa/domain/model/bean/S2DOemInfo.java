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

public class S2DOemInfo extends DomainBaseBean {
    private String CMO_OEM_BRAND_INFO;

    private String CMO_OEM_MANUFACTORY_INFO;

    private String CMO_OEM_PRODUCT_MODEL;

    public String getCMO_OEM_BRAND_INFO() {
        return CMO_OEM_BRAND_INFO;
    }

    public void setCMO_OEM_BRAND_INFO(String cMO_OEM_BRAND_INFO) {
        CMO_OEM_BRAND_INFO = cMO_OEM_BRAND_INFO;
    }

    public String getCMO_OEM_MANUFACTORY_INFO() {
        return CMO_OEM_MANUFACTORY_INFO;
    }

    public void setCMO_OEM_MANUFACTORY_INFO(String cMO_OEM_MANUFACTORY_INFO) {
        CMO_OEM_MANUFACTORY_INFO = cMO_OEM_MANUFACTORY_INFO;
    }

    public String getCMO_OEM_PRODUCT_MODEL() {
        return CMO_OEM_PRODUCT_MODEL;
    }

    public void setCMO_OEM_PRODUCT_MODEL(String cMO_OEM_PRODUCT_MODEL) {
        CMO_OEM_PRODUCT_MODEL = cMO_OEM_PRODUCT_MODEL;
    }
}
