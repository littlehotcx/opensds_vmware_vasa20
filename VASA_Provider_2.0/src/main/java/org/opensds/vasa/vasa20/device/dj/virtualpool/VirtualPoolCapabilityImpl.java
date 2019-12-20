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

package org.opensds.vasa.vasa20.device.dj.virtualpool;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.virtualpool.IVirtualPoolCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DVirtualPool;
import org.opensds.vasa.domain.model.bean.S2DVirtualPoolSpaceStats;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.dj.bean.BatchVirtualPoolResBean;
import org.opensds.vasa.vasa20.device.dj.bean.VirtualPoolResBean;
import org.opensds.vasa.vasa20.device.dj.bean.VirtualPoolSpaceStatsResBean;
import org.opensds.vasa.vasa20.device.dj.convert.VirtualPoolCapabilityConvert;

public class VirtualPoolCapabilityImpl extends AbstractVASACapability implements IVirtualPoolCapability {
    private static final Logger LOGGER = LogManager.getLogger(VirtualPoolCapabilityImpl.class);

    private VirtualPoolCapabilityConvert virtualpoolCapabilityconvert = new VirtualPoolCapabilityConvert();

    private static final int NUM_PER_FETCH = 100;

    public VirtualPoolCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DVirtualPool>> getAllVirtualPool() {
        LOGGER.debug("getAllVirtualPool() start");
        SDKResult<List<S2DVirtualPool>> sdkResult = new SDKResult<List<S2DVirtualPool>>();
        List<S2DVirtualPool> allVirtualPools = new ArrayList<S2DVirtualPool>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        int offset = 0;
        int limit = NUM_PER_FETCH;
        try {
            while (true) {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/virtual_pools?limit=" + limit
                        + "&offset=" + offset, "org.opensds.vasa.vasa20.device.dj.bean.BatchVirtualPoolResBean");

                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                BatchVirtualPoolResBean batchArr = (BatchVirtualPoolResBean) res.getResData();
                if (null == batchArr.getVirtual_pools() || batchArr.getVirtual_pools().size() <= 0) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allVirtualPools);
                    return sdkResult;
                }

                int len = batchArr.getVirtual_pools().size();
                allVirtualPools.addAll(virtualpoolCapabilityconvert.convertSouth2Model(batchArr.getVirtual_pools()));
                if (len < NUM_PER_FETCH) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allVirtualPools);
                    return sdkResult;
                } else {
                    offset = offset + NUM_PER_FETCH;
                }
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllVirtualPool() error");
        }

        LOGGER.debug("getAllVirtualPool() end");
        return sdkResult;
    }

    @Override
    public SDKResult<S2DVirtualPool> getVirtualPoolById(String poolId) {
        LOGGER.debug("getVirtualPoolById() start");
        SDKResult<S2DVirtualPool> sdkResult = new SDKResult<S2DVirtualPool>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/virtual_pools/" + poolId, "org.opensds.vasa.vasa20.device.dj.bean.VirtualPoolResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VirtualPoolResBean svp = (VirtualPoolResBean) res.getResData();
                sdkResult.setResult(virtualpoolCapabilityconvert.convertSouth2Model(svp.getVirtual_pool()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getVirtualPoolById() error");
        }

        LOGGER.debug("getVirtualPoolById() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVirtualPoolSpaceStats> getVirtualPoolSpaceStatsById(String poolId) {
        LOGGER.debug("getVirtualPoolSpaceStatsById() start");
        SDKResult<S2DVirtualPoolSpaceStats> sdkResult = new SDKResult<S2DVirtualPoolSpaceStats>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/virtual_pool_spacestats/" + poolId, "org.opensds.vasa.vasa20.device.dj.bean.VirtualPoolSpaceStatsResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VirtualPoolSpaceStatsResBean svp = (VirtualPoolSpaceStatsResBean) res.getResData();
                sdkResult.setResult(virtualpoolCapabilityconvert.convertSouth2Model(svp.getVirtual_pool_spacestats()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getVirtualPoolSpaceStatsById() error");
        }

        LOGGER.debug("getVirtualPoolSpaceStatsById() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DVirtualPool>> getVirtualPoolByArrayId(String arrayId) {
        LOGGER.debug("getVirtualPoolByArrayId() start");
        SDKResult<List<S2DVirtualPool>> sdkResult = new SDKResult<List<S2DVirtualPool>>();
        List<S2DVirtualPool> allVirtualPools = new ArrayList<S2DVirtualPool>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        int offset = 0;
        int limit = NUM_PER_FETCH;
        try {
            while (true) {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/virtual_pools?storage_array_id=" + arrayId + "&limit=" + limit
                        + "&offset=" + offset, "org.opensds.vasa.vasa20.device.dj.bean.BatchVirtualPoolResBean");

                sdkResult.setErrCode(res.getErrorCode());
                sdkResult.setDescription(res.getDescription());

                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                BatchVirtualPoolResBean batchArr = (BatchVirtualPoolResBean) res.getResData();
                if (null == batchArr.getVirtual_pools() || batchArr.getVirtual_pools().size() <= 0) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allVirtualPools);
                    return sdkResult;
                }

                int len = batchArr.getVirtual_pools().size();
                allVirtualPools.addAll(virtualpoolCapabilityconvert.convertSouth2Model(batchArr.getVirtual_pools()));
                if (len < NUM_PER_FETCH) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allVirtualPools);
                    return sdkResult;
                } else {
                    offset = offset + NUM_PER_FETCH;
                }
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getVirtualPoolByArrayId() error");
        }

        LOGGER.debug("getVirtualPoolByArrayId() end");

        return sdkResult;
    }

}
