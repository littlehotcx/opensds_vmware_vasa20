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

package org.opensds.vasa.vasa20.device.array.NFSshare;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

import java.util.List;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
public interface INFSshareService {

    String ACCESSVAL_READ_WRITE = "1";

    String ACCESSVAL_READ_ONLY = "0";

    String SYNC = "0";
    String ASYNC = "1";

    String ALLSQUASH_ALL = "0";
    String ALLSQUASH_NO_ALL = "1";

    String ROOTSQUASH_ROOT = "0";
    String ROOTSQUASH_NO_ROOT = "1";

    long SHARE_EXIST = 1077940500;
    long SHARE_NOT_EXIST = 1077939726;
    long CLIENT_EXIST = 1077939727;
    long CLIENT_NOT_EXIST = 1077939728;

    public SDKResult<NFSshareCreateResBean> createShare(String arrayId, String FSid, String sharePath, String description);

    public SDKErrorCode deleteShare(String arrayId, String shareId, String vstoreId);

    public void modifyShare(String shareId, String description);

    public SDKResult<NFSshareCreateResBean> queryShare(String arrayId, String sharePath, String vstoreId);

    public SDKResult<AddAuthClientResBean> addAuthClient(String arrayId, String name, String parentId, String accessval, String sync, String allSquash, String rootSquash);

    public SDKErrorCode deleteAuthClient(String clientId);

    public void modifyAuthClient(String clientId, String accessval, String sync, String allSquash, String rootSquash);

    public SDKResult<List<AddAuthClientResBean>> queryAuthClient(String arrayId, String vstoreId, String shareId, String name);

}
