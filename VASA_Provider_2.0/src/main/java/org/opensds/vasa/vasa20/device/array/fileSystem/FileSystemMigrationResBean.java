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

package org.opensds.vasa.vasa20.device.array.fileSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-26
 */
public class FileSystemMigrationResBean {

    @JsonProperty
    String migrateTmpFsID;

    @Override
    public String toString() {
        return "FileSystemMigrationResBean{" +
                "migrateTmpFsID='" + migrateTmpFsID + '\'' +
                '}';
    }

    public String getMigrateTmpFsID() {
        return migrateTmpFsID;
    }

    public void setMigrateTmpFsID(String migrateTmpFsID) {
        this.migrateTmpFsID = migrateTmpFsID;
    }

}
