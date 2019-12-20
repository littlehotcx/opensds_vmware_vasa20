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

public class PassThroughResBean {
    private String raw_rsp_status;

    private PassThroughHeadInfo raw_rsp_head;

    private PassThroughBodyInfo raw_rsp_body;

    public String getRaw_rsp_status() {
        return raw_rsp_status;
    }

    public void setRaw_rsp_status(String raw_rsp_status) {
        this.raw_rsp_status = raw_rsp_status;
    }

    public PassThroughHeadInfo getRaw_rsp_head() {
        return raw_rsp_head;
    }

    public void setRaw_rsp_head(PassThroughHeadInfo raw_rsp_head) {
        this.raw_rsp_head = raw_rsp_head;
    }

    public PassThroughBodyInfo getRaw_rsp_body() {
        return raw_rsp_body;
    }

    public void setRaw_rsp_body(PassThroughBodyInfo raw_rsp_body) {
        this.raw_rsp_body = raw_rsp_body;
    }


}
