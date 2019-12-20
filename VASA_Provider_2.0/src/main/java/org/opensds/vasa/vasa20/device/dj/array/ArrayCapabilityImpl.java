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

package org.opensds.vasa.vasa20.device.dj.array;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.array.IArrayCapability;
import org.opensds.vasa.vasa20.device.dj.convert.ArrayCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DArray;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.BatchArrayResBean;
import org.opensds.vasa.vasa20.device.dj.bean.StorageArrayResBean;

public class ArrayCapabilityImpl extends AbstractVASACapability implements IArrayCapability {
    private static final Logger LOGGER = LogManager.getLogger(ArrayCapabilityImpl.class);

    private ArrayCapabilityConvert arrayCapabilityconvert = new ArrayCapabilityConvert();

    public ArrayCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DArray>> getAllArray() {
        LOGGER.debug("getAllArray() start");
        SDKResult<List<S2DArray>> sdkResult = new SDKResult<List<S2DArray>>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/storage_arrays?limit=100&offset=0",
                    "org.opensds.vasa.vasa20.device.dj.bean.BatchArrayResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                BatchArrayResBean batchArr = (BatchArrayResBean) res.getResData();
                sdkResult.setResult(arrayCapabilityconvert.convertSouth2Model(batchArr.getStorage_arrays()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllArray() error", e);
        }

        LOGGER.debug("getAllArray() end");

        return sdkResult;

    }

    //非透传
    @Override
    public SDKResult<S2DArray> getArrayById(String arrayId) {
        LOGGER.debug("getArrayById() start");
        SDKResult<S2DArray> sdkResult = new SDKResult<S2DArray>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/storage_arrays/" + arrayId,
                    "org.opensds.vasa.vasa20.device.dj.bean.StorageArrayResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                StorageArrayResBean arrayRes = (StorageArrayResBean) res.getResData();
                sdkResult.setResult(arrayCapabilityconvert.ConvertSouth2Model(arrayRes.getStorage_array()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getArrayById() error", e);
        }

        LOGGER.debug("getArrayById() end");

        return sdkResult;
    }

}
