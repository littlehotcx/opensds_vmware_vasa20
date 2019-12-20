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

package org.opensds.vasa.vasa.util;

public class CommandResult {

    public int result;
    public String responseMsg;
    public String errorMsg;

    public CommandResult(int result) {
        this.result = result;
    }

    public CommandResult(int result, String responseMsg, String errorMsg) {
        this.result = result;
        this.responseMsg = responseMsg;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "errorMsg='" + errorMsg + '\'' +
                ", result=" + result +
                ", responseMsg='" + responseMsg + '\'' +
                '}';
    }
}