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
 * @since 2019-02-28
 */
public class NFSvvolCreateReqBean {

    public String cmd;

    public String srcPath;

    public String dstPath;

    public long fileSize;

    private String vstoreId;

    @Override
    public String toString() {
        return "NFSvvolCreateReqBean{" +
                "cmd='" + cmd + '\'' +
                ", srcPath='" + srcPath + '\'' +
                ", dstPath='" + dstPath + '\'' +
                ", fileSize=" + fileSize +
                ", vstoreId='" + vstoreId + '\'' +
                '}';
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getVstoreId() {
        return vstoreId;
    }

    public void setVstoreId(String vstoreId) {
        this.vstoreId = vstoreId;
    }
}
