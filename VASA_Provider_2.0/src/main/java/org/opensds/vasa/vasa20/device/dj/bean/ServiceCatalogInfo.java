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

public class ServiceCatalogInfo {
    private List<EndpointInfo> endpoints;

    private List<String> endpoints_links;

    private String type;

    private String name;

    public List<EndpointInfo> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointInfo> endpoints) {
        this.endpoints = endpoints;
    }

    public List<String> getEndpoints_links() {
        return endpoints_links;
    }

    public void setEndpoints_links(List<String> endpoints_links) {
        this.endpoints_links = endpoints_links;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


} 
