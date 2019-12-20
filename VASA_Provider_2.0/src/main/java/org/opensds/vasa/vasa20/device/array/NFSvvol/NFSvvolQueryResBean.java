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

package org.opensds.vasa.vasa20.device.array.NFSvvol;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-11
 */
public class NFSvvolQueryResBean {

    private long fileSize;

    private long fileAllocSize;

    @Override
    public String toString() {
        return "NFSvvolQueryResBean{" +
                "fileSize=" + fileSize +
                ", fileAllocSize=" + fileAllocSize +
                '}';
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileAllocSize() {
        return fileAllocSize;
    }

    public void setFileAllocSize(int fileAllocSize) {
        this.fileAllocSize = fileAllocSize;
    }
}
