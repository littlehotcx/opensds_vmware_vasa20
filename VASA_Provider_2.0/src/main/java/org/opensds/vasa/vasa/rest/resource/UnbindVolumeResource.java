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

package org.opensds.vasa.vasa.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import com.google.gson.Gson;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.rest.bean.UnbindFaultDataResponse;

import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchReturnStatus;

@Path("vasa/unbindVolume")
public class UnbindVolumeResource {

    private Logger LOGGER = LogManager.getLogger(UnbindVolumeResource.class);
    private VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil
            .getBean("vvolMetadataService");
    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UnbindFaultDataResponse unbindVolume(
            @QueryParam("vvolId") String vvolId) {

        LOGGER.info("unbindVolume start");
        UnbindFaultDataResponse response = new UnbindFaultDataResponse();
        if (StringUtils.isEmpty(vvolId)) {
            LOGGER.info("unbindVolume vvolid is null");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        DiscoverService discoverService = DiscoverService.getInstance();
        List<String> vvolIds = new ArrayList<String>();
        List<NVirtualVolume> unbindNVirtualVolume = new ArrayList<NVirtualVolume>();
        vvolIds.add(vvolId);

        response.setVvolId(vvolId);

        try {
            unbindNVirtualVolume = discoverService.queryVirtualVolumeFromDataBase(vvolIds);
            if (unbindNVirtualVolume.size() == 0) {
                LOGGER.error("unbindVolume vvolid is not exist.");
                response.setResultCode(VASAResponseCode.virtualVolumeService.STORAGE_VOLUME_IS_NOT_EXIST);
                response.setResultDescription(VASAResponseCode.virtualVolumeService.STORAGE_VOLUME_IS_NOT_EXIST_DESCRIPTION);
                return response;

            }

            if (DiscoverServiceImpl.getInstance().isNasVvol(vvolId)) {
                LOGGER.info("unbind nas vvol on platform by user" + vvolId);
                vvolPathDBService.setBindState(vvolId, false);
                response.setVvolName(vvolMetadataService.getvmNameByVvolId(unbindNVirtualVolume.get(0).getVvolid()));
                response.setVvolType(unbindNVirtualVolume.get(0).getVvolType());
                response.setSourceType(unbindNVirtualVolume.get(0).getSourceType());
                LOGGER.info("The Volume info is : " + unbindNVirtualVolume.toString());
            } else {
                response.setVvolName(vvolMetadataService.getvmNameByVvolId(unbindNVirtualVolume.get(0).getVvolid()));
                response.setVvolType(unbindNVirtualVolume.get(0).getVvolType());
                response.setSourceType(unbindNVirtualVolume.get(0).getSourceType());
                LOGGER.info("The Volume info is : " + unbindNVirtualVolume.toString());
            }

        } catch (StorageFault e) {
            LOGGER.error(e);
            response.setResultCode("1");
            response.setResultDescription("unbind fail, please try again later");
            return response;
        }

        List<BatchReturnStatus> result = null;

        try {
            result = discoverService.unbindVirtualVolumeFromAllHost(vvolIds);
            LOGGER.info("unbindVolume  result  is: " + new Gson().toJson(result));
        } catch (StorageFault e) {
            LOGGER.error(e);
            response.setResultCode("1");
            response.setResultDescription("unbind fail, please try again later");
            return response;
        }

        if (null != result && null != result.get(0)) {
            if (null == result.get(0).getErrorResult()) {
                response.setResultCode("0");
                response.setResultDescription("unbind successfully");
            } else {
                response.setResultCode("1");
                if (unbindNVirtualVolume.size() == 0) {
                    response.setResultDescription("unbind fail.");
                    LOGGER.error("The vvolId : " + vvolId + "  execute unbindVirtualVolumeFromAllHost unbind fail.");
                } else {
                    List<Object> res = result.get(0).getErrorResult().getError();
                    String error = "";

                    if (null != res && 0 < res.size()) {
                        for (Object obj : res) {
                            error += obj.toString();
                        }
                    }
                    LOGGER.error(error);
                    response.setResultDescription("unbind fail, please try again later");
                }
            }
        } else {
            response.setResultCode("1");
            response.setResultDescription("unbind error");
        }

        LOGGER.info("unbindVolume end");
        return response;
    }
}
