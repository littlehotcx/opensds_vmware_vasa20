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

package org.opensds.vasa.vasa20.device.dj.fs;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.fs.IFileSystemCapability;
import org.opensds.vasa.vasa20.device.array.bean.NFSShareResBean;
import org.opensds.vasa.vasa20.device.dj.bean.SFileSystem;
import org.opensds.vasa.vasa20.device.dj.convert.FileSystemCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DFileSystem;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class FileSystemCapabilityImpl extends AbstractVASACapability implements IFileSystemCapability {
    private static final Logger LOGGER = LogManager.getLogger(FileSystemCapabilityImpl.class);

    private FileSystemCapabilityConvert fsCapabilityconvert = new FileSystemCapabilityConvert();

    public FileSystemCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<String> getFileSystemBySharePathCount(String arrayId, String sharePath) {
        LOGGER.debug("getFileSystemBySharePathCount() start");
        SDKResult<String> sdkResult = new SDKResult<String>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/fileSystem/count?filter=NAME:" + sharePath, "org.opensds.vasa.vasa20.device.dj.bean.SFileSystem");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SFileSystem sfs = (SFileSystem) (res.getResData());
                sdkResult.setResult(sfs.getCOUNT());
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getFileSystemBySharePathCount() error", e);
        }
        LOGGER.debug("getFileSystemBySharePathCount() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DFileSystem>> getFileSystemBySharePath(String arrayId, String sharePath, String count) {
        LOGGER.debug("getFileSystemBySharePath() start");
        SDKResult<List<S2DFileSystem>> sdkResult = new SDKResult<List<S2DFileSystem>>();
        List<S2DFileSystem> result = new ArrayList<S2DFileSystem>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setPageSize(Integer.valueOf(count));
        req.setArrayId(arrayId);
        req.setPaging(true);
        req.setPageSize(Integer.parseInt(count));

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/fileSystem?filter=NAME:" + sharePath, "org.opensds.vasa.vasa20.device.dj.bean.SFileSystem");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                List<SFileSystem> list = (List<SFileSystem>) (res.getResData());
                if (null != list && list.size() != 0) {
                    for (SFileSystem sFileSystem : list) {
                        result.add(fsCapabilityconvert.convertSouth2Model(sFileSystem));
                    }
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getFileSystemBySharePath() error", e);
        }
        LOGGER.debug("getFileSystemBySharePath() end");

        return sdkResult;
    }

    @Override
    public SDKResult<String> getNFSSharePathByShareName(String arrayId, String shareName) {
        LOGGER.debug("getNFSSharePathByShareName() start");
        SDKResult<String> sdkResult = new SDKResult<String>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
//		req.setPageSize(Integer.valueOf("4"));
        req.setArrayId(arrayId);
        req.setPaging(true);
//		req.setPageSize(Integer.parseInt("4"));

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/NFSHARE?SHAREPATH:" + shareName.replace("/", ""), "org.opensds.vasa.vasa20.device.array.bean.NFSShareResBean");
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                String result = null;
                List<NFSShareResBean> list = (List<NFSShareResBean>) (res.getResData());
                if (null != list && list.size() != 0) {
                    for (NFSShareResBean nFSShareResBean : list) {
                        if (shareName.equals(nFSShareResBean.getNAME())) {
                            result = nFSShareResBean.getSHAREPATH();
                        }
                    }
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getNFSSharePathByShareName() error", e);
        }
        LOGGER.debug("getNFSSharePathByShareName() end");

        return sdkResult;
    }


}
