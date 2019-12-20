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

package org.opensds.vasa.vasa20.device.storage.storagepool;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.storagepool.IStoragePoolCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.StoragePoolResBean;
import org.opensds.vasa.vasa20.device.dj.convert.StoragePoolResBeanCapabilityConvert;

public class StoragePoolCapabilityImpl extends AbstractVASACapability implements IStoragePoolCapability {

    private static final Logger LOGGER = LogManager.getLogger(StoragePoolCapabilityImpl.class);
    private static final String poolRange = "[0-100]";
    private String getStoragepool_url = "storagepool/";

    public StoragePoolCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
        // TODO Auto-generated constructor stub
    }

    @Override
    public SDKResult<List<S2DStoragePool>> getAllStoragePool() {
        // TODO Auto-generated method stub
        LOGGER.info("getAllStoragePool() start");
        SDKResult<List<S2DStoragePool>> sdkResult = new SDKResult<List<S2DStoragePool>>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setHasRange(true);
        req.setPaging(true);
        req.setPageSize(100);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/storagepool", "org.opensds.vasa.vasa20.device.dj.bean.StoragePoolResBean");
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                List<S2DStoragePool> list = new ArrayList<>();
                List<StoragePoolResBean> storagePoolResBean = (List<StoragePoolResBean>) res.getResData();
                for (StoragePoolResBean storagePoolResBean2 : storagePoolResBean) {
                    S2DStoragePool convertSouth2Model = StoragePoolResBeanCapabilityConvert.convertSouth2Model(storagePoolResBean2);
                    list.add(convertSouth2Model);
            		/*if(storagePoolResBean2.getUSAGETYPE().equalsIgnoreCase("1")){
            			S2DStoragePool convertSouth2Model = StoragePoolResBeanCapabilityConvert.convertSouth2Model(storagePoolResBean2);
            			list.add(convertSouth2Model);
            		}*/
                }
                sdkResult.setResult(list);
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("getAllStoragePool() error ,msg=" + e.getMessage());
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllStoragePool() error", e);
        } catch (Exception e) {
            // TODO: handle exception
            //sdkResult.setErrCode(e.getErrorCode());
            //sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllStoragePool() error ,msg=" + e.getMessage());
            LOGGER.error("getAllStoragePool() error", e);
        }

        LOGGER.debug("getAllStoragePool() end,the result is : " + sdkResult.toString());
        return sdkResult;
    }

    @Override
    public SDKResult<S2DStoragePool> getStoragePoolByPoolId(String arrayId, String poolId) {
        // TODO Auto-generated method stub
        LOGGER.info("getStoragePoolByPoolId() start");
        SDKResult<S2DStoragePool> sdkResult = new SDKResult<S2DStoragePool>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, getStoragepool_url + poolId, "org.opensds.vasa.vasa20.device.dj.bean.StoragePoolResBean");
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                StoragePoolResBean storagePoolResBean = (StoragePoolResBean) res.getResData();
                //if(storagePoolResBean.getUSAGETYPE().equalsIgnoreCase("1")){
                S2DStoragePool convertSouth2Model = StoragePoolResBeanCapabilityConvert.convertSouth2Model(storagePoolResBean);
                sdkResult.setResult(convertSouth2Model);
                //}
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("getStoragePoolByPoolId error ,msg=" + e.getMessage());
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getStoragePoolByPoolId error", e);
        } catch (Exception e) {
            // TODO: handle exception
            //sdkResult.setErrCode(e.getErrorCode());
            //sdkResult.setDescription(e.getMessage());
            LOGGER.error("getStoragePoolByPoolId error ,msg=" + e.getMessage());
            LOGGER.error("getStoragePoolByPoolId error", e);
        }

        LOGGER.debug("getStoragePoolByPoolId end,the result is : " + sdkResult.toString());
        return sdkResult;
    }

}
