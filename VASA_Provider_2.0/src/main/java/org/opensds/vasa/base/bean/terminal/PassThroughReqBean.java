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

public class PassThroughReqBean {
    private String array_id;

    private String raw_method;

    private String raw_uri;

    private Object raw_req_body;

    public String getArray_id() {
        return array_id;
    }

    public void setArray_id(String array_id) {
        this.array_id = array_id;
    }

    public String getRaw_method() {
        return raw_method;
    }

    public void setRaw_method(String raw_method) {
        this.raw_method = raw_method;
    }

    public String getRaw_uri() {
        return raw_uri;
    }

    public void setRaw_uri(String raw_uri) {
        this.raw_uri = raw_uri;
    }

    public Object getRaw_req_body() {
        return raw_req_body;
    }

    public void setRaw_req_body(Object raw_req_body) {
        this.raw_req_body = raw_req_body;
    }


}
