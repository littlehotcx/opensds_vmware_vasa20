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

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
public class NFSshareService extends AbstractVASACapability implements INFSshareService {

    public NFSshareService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    private static Logger LOGGER = LogManager.getLogger(NFSshareService.class);
    private String NFSshare_url = "nfshare";
    private String NFSshareAuthClient_url = "NFS_SHARE_AUTH_CLIENT";
    private String deleteNFSShare_url = "NFSHARE/";
    private String queryNFSshare_url = "NFSHARE?filter=SHAREPATH:";
    private String queryNFSshareAuthClientONName_url = "NFS_SHARE_AUTH_CLIENT?filter=NAME::";
    private String queryNFSshareAuthClientONParentId_url = "NFS_SHARE_AUTH_CLIENT?filter=PARENTID::";
    private String queryNFSshareAuthClient_url = "NFS_SHARE_AUTH_CLIENT?";

    @Override
    public SDKResult<NFSshareCreateResBean> createShare(String arrayId, String FSId, String sharePath, String description) {
        LOGGER.debug("create NFSshare:" + FSId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        NFSshareCreateReqBean reqPayload = new NFSshareCreateReqBean();
        SDKResult<NFSshareCreateResBean> sdkResult = new SDKResult<>();
        createNFSshareReqBean(reqPayload, FSId, sharePath, description);
        req.setPayload(reqPayload);
        LOGGER.debug("req data: " + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, NFSshare_url, "org.opensds.vasa.vasa20.device.array.NFSshare.NFSshareCreateResBean");

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                NFSshareCreateResBean result = (NFSshareCreateResBean) response.getResData();
                LOGGER.debug("create NFSshare success " + result);
                sdkResult.setResult(result);
            } else {
                NFSshareCreateResBean result = (NFSshareCreateResBean) response.getResData();
                LOGGER.error("create NFSshare failed " + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription(response.getDescription());
                return sdkResult;
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("create NFSshare fail! NFSshareCreateReqBean=" + reqPayload, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("creat NFSshare fail, NFSshareCreateReqBean=" + reqPayload);
            e.printStackTrace();
        }

        return sdkResult;

    }

    private void createNFSshareReqBean(NFSshareCreateReqBean reqPayload, String fsId, String sharePath, String description) {

        reqPayload.setDESCRIPTION(description);
        reqPayload.setSHAREPATH(sharePath);
        reqPayload.setFSID(fsId);
    }

    @Override
    public SDKErrorCode deleteShare(String arrayId, String shareId, String vstoreId) {
        LOGGER.debug("delete nfsShare:" + shareId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SDKErrorCode sdkResult = new SDKErrorCode();

        try {
            ResBean response;
            if (vstoreId == null) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteNFSShare_url + shareId, null);
            } else {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteNFSShare_url + shareId + "?vstoreId=" + vstoreId, null);
            }
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("success delete nfsShare,response=" + response);

        } catch (ProtocolAdapterException e) {
            LOGGER.error("delete nfsShare failed! nfsShare ID =" + shareId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("delete nfsShare fail! nfsShare ID = " + shareId);
            e.printStackTrace();
        }
        return sdkResult;

    }

    @Override
    public void modifyShare(String shareId, String description) {

    }

    public SDKResult<NFSshareCreateResBean> queryShare(String arrayId, String sharePath, String vstoreId) {

        LOGGER.debug("queryShareReqBean" + sharePath);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        req.setPaging(true);
        req.setHasRange(true);
        SDKResult<NFSshareCreateResBean> sdkResult = new SDKResult<>();
        List<NFSshareCreateResBean> rsp = new ArrayList<>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSshare_url + sharePath, "org.opensds.vasa.vasa20.device.array.NFSshare.NFSshareCreateResBean");

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                LOGGER.debug("query nfsshare success" + response.getResData());
                rsp = (List<NFSshareCreateResBean>) response.getResData();
                sdkResult.setResult(rsp.get(0));
                return sdkResult;
            } else {
                LOGGER.error("query nfsshare fail! nfsshare=" + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription("query nfsshare fail, NFSsharequery=" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("query nfsshare fail! nfsshare=" + e.getSdkErrDesc());
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("query nfsshare fail, NFSsharequery=" + e.getSdkErrDesc());
            e.printStackTrace();
        }
        return sdkResult;
    }

    @Override
    public SDKResult<AddAuthClientResBean> addAuthClient(String arrayId, String name, String parentId, String accessval, String sync, String allSquash, String rootSquash) {

        LOGGER.debug("addAuthClientReqBean " + parentId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        AddAuthClientReqBean reqPayload = new AddAuthClientReqBean();
        SDKResult<AddAuthClientResBean> sdkResult = new SDKResult<>();
        createAddAuthClientReqBean(reqPayload, name, parentId, accessval, sync, allSquash, rootSquash);
        req.setPayload(reqPayload);
        LOGGER.debug("req data: " + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, NFSshareAuthClient_url, "org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean");

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                AddAuthClientResBean result = (AddAuthClientResBean) response.getResData();
                LOGGER.debug("add AuthClient success! " + result);
                sdkResult.setResult(result);
                return sdkResult;
            } else {
                LOGGER.error("create AuthClient fail! AuthClient=" + reqPayload, response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription("creat AuthClient fail, NFSshareCreateReqBean=" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("create AuthClient fail! AuthClient=" + reqPayload, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("creat AuthClient fail, NFSshareCreateReqBean=" + reqPayload);
            e.printStackTrace();
        }

        return sdkResult;


    }

    private void createAddAuthClientReqBean(AddAuthClientReqBean reqPayload, String name, String parentId, String accessval, String sync, String allSquash, String rootSquash) {
        reqPayload.setNAME(name);
        reqPayload.setPARENTID(parentId);
        reqPayload.setACCESSVAL(Integer.valueOf(accessval));
        reqPayload.setSYNC(Integer.valueOf(sync));
        reqPayload.setALLSQUASH(Integer.valueOf(allSquash));
        reqPayload.setROOTSQUASH(Integer.valueOf(rootSquash));
    }

    @Override
    public SDKErrorCode deleteAuthClient(String clientId) {
        SDKErrorCode ret = new SDKErrorCode();
        LOGGER.debug("deleteAuthClient:" + clientId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        LOGGER.debug("delete client id:" + clientId);
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, NFSshareAuthClient_url + "/" + clientId, null);

            ret.setErrCode(response.getErrorCode());
            ret.setDescription(response.getDescription());
        } catch (ProtocolAdapterException e) {
            LOGGER.error("delete AuthClient fail! AuthClient.");
            ret.setErrCode(e.getErrorCode());
            ret.setDescription("delete AuthClient fail.");
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void modifyAuthClient(String clientId, String accessval, String sync, String allSquash, String rootSquash) {

    }

    @Override
    //public SDKResult<AddAuthClientResBean> queryAuthClient(String arrayId, String vstoreId, String shareId, String name) {
    public SDKResult<List<AddAuthClientResBean>> queryAuthClient(String arrayId, String vstoreId, String shareId, String name) {

        LOGGER.debug("queryAuthClientReqBean" + name);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setHasRange(true);
        req.setArrayId(arrayId);
        SDKResult<List<AddAuthClientResBean>> sdkResult = new SDKResult<>();
        try {
            StringBuffer urlBuffer = new StringBuffer();
            ResBean response = null;
            if (StringUtils.isNotEmpty(name) && StringUtils.isEmpty(shareId)) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSshareAuthClientONName_url + name
                        , "org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean");
            }
            if (StringUtils.isNotEmpty(shareId) && StringUtils.isEmpty(name)) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSshareAuthClientONParentId_url + shareId
                        , "org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean");
            }
            if (StringUtils.isNotEmpty(shareId) && StringUtils.isNotEmpty(name)) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSshareAuthClientONName_url + name
                        , "org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean");
            }
            if (StringUtils.isEmpty(name) && StringUtils.isEmpty(shareId)) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, queryNFSshareAuthClient_url
                        , "org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean");
            }
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                @SuppressWarnings("unchecked")
                List<AddAuthClientResBean> objectList = (List<AddAuthClientResBean>) response.getResData();
//                List<AddAuthClientResBean> addAuthClientResBeans=new ArrayList<>();
//                if(objectList!=null && objectList.size()>0){
//                    addAuthClientResBeans=new Gson().fromJson(response.getResData().toString(), new TypeToken<List<AddAuthClientResBean>>(){}.getType());
//                }
                sdkResult.setResult(objectList);
                return sdkResult;
            } else {
                LOGGER.error("query AuthClient fail! ", response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription("query AuthClient fail!" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("query AuthClient fail! ");
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("query AuthClient fail! ");
            e.printStackTrace();
        }
        return sdkResult;
    }
}
