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

import org.opensds.platform.common.SDKResult;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
public interface INFSvvolService {

    String operate_create = "create";
    String operate_rmdir = "rmdir";
    String operate_remove = "remove";
    String operate_mkdir = "mkdir";
    String operate_setattr = "setattr";
    String operate_rename = "rename";
    int ERROR_OBJ_NOT_EXIST = 1073819176;
    int FS_NOT_EXIST = 1077948540;
    //cmd include mkdir rmdir create remove rename(dstpath) setattr(filsize)

    // file size in bytes

    public SDKResult<NFSvvolCreateResBean> operateNFSvvol(String arrayId, String cmd, String srcPath, String dstPath, long filesize, String vstoreId);

    public SDKResult<NFSvvolQueryResBean> queryNFSvvol(String arrayId, String vstoreId, String srcPath);

}
