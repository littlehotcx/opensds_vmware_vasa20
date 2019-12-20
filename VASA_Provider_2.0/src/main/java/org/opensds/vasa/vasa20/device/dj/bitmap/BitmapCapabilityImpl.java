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

package org.opensds.vasa.vasa20.device.dj.bitmap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.bitmap.IBitmapCapability;
import org.opensds.vasa.vasa20.device.dj.convert.BitmapCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DBitmap;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SBitmap;

public class BitmapCapabilityImpl extends AbstractVASACapability implements IBitmapCapability {
    private static final Logger LOGGER = LogManager.getLogger(BitmapCapabilityImpl.class);
    private BitmapCapabilityConvert bitmapCapabilityconvert = new BitmapCapabilityConvert();

    public BitmapCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DBitmap> getAllocatedBitmap(String arrayId, String vvolId, long segmentStartOffsetBytes,
                                                   long segmentLengthBytes, long chunkSizeBytes) {
        LOGGER.debug("getAllocatedBitmap() start");
        SDKResult<S2DBitmap> sdkResult = new SDKResult<S2DBitmap>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        String url = "/vvol_bitmap/get_alloc?VVOLID=" + vvolId + "&SEGMENTSTARTOFFSETBYTES=" + segmentStartOffsetBytes
                + "&SEGMENTLENGTHBYTES=" + segmentLengthBytes + "&CHUNKSIZEBYTES=" + chunkSizeBytes;

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, url,
                    "org.opensds.vasa.vasa20.device.dj.bean.SBitmap");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SBitmap sBitmap = (SBitmap) (res.getResData());

                sdkResult.setResult(bitmapCapabilityconvert.convertSouth2Model(sBitmap));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllocatedBitmap() error", e);
        }
        LOGGER.debug("getAllocatedBitmap() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DBitmap> getUnsharedBitmap(String arrayId, String vvolId, String baseVvolId,
                                                  long segmentStartOffsetBytes, long segmentLengthBytes, long chunkSizeBytes) {
        LOGGER.debug("getUnsharedBitmap() start");
        SDKResult<S2DBitmap> sdkResult = new SDKResult<S2DBitmap>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        String url = "/vvol_bitmap/get_unshared?VVOLID=" + vvolId + "&BASEVVOLID=" + baseVvolId
                + "&SEGMENTSTARTOFFSETBYTES=" + segmentStartOffsetBytes + "&SEGMENTLENGTHBYTES=" + segmentLengthBytes
                + "&CHUNKSIZEBYTES=" + chunkSizeBytes;

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, url,
                    "org.opensds.vasa.vasa20.device.dj.bean.SBitmap");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SBitmap sBitmap = (SBitmap) (res.getResData());

                sdkResult.setResult(bitmapCapabilityconvert.convertSouth2Model(sBitmap));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getUnsharedBitmap() error", e);
        }
        LOGGER.debug("getUnsharedBitmap() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DBitmap> getUnsharedChunks(String arrayId, String vvolId, String baseVvolId,
                                                  long segmentStartOffsetBytes, long segmentLengthBytes) {
        LOGGER.info("getUnsharedChunks() start. ArrayId: " + arrayId + ", vvolId: " + vvolId + ", baseVvolId: "
                + baseVvolId + ", segmentStartOffsetBytes: " + segmentStartOffsetBytes + ", segmentLengthBytes: "
                + segmentLengthBytes);
        SDKResult<S2DBitmap> sdkResult = new SDKResult<S2DBitmap>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        String url;
        if (null == baseVvolId) {
            url = "/vvol_bitmap/get_unshared_chk?VVOLID=" + vvolId + "&SEGMENTSTARTOFFSETBYTES="
                    + segmentStartOffsetBytes + "&SEGMENTLENGTHBYTES=" + segmentLengthBytes;
        } else {
            url = "/vvol_bitmap/get_unshared_chk?VVOLID=" + vvolId + "&BASEVVOLID=" + baseVvolId
                    + "&SEGMENTSTARTOFFSETBYTES=" + segmentStartOffsetBytes + "&SEGMENTLENGTHBYTES="
                    + segmentLengthBytes;
        }

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, url,
                    "org.opensds.vasa.vasa20.device.dj.bean.SBitmap");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SBitmap sBitmap = (SBitmap) (res.getResData());

                sdkResult.setResult(bitmapCapabilityconvert.convertSouth2Model(sBitmap));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getUnsharedChunks() error", e);
        }
        LOGGER.info("getUnsharedChunks() end. ErrCode: " + sdkResult.getErrCode() + ", description:"
                + sdkResult.getDescription());

        return sdkResult;
    }

}
