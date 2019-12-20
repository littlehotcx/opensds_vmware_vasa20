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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NVasaServiceCenter;
import org.opensds.vasa.vasa.db.service.VasaServiceCenterService;
import org.opensds.vasa.vasa.rest.bean.QueryVasaServiceResponse;

@Path("vasa/queryVasaService")
public class VasaServiceCenterResource {
    private Logger LOGGER = LogManager.getLogger(VasaServiceCenterResource.class);

    private VasaServiceCenterService vasaServiceCenterService = ApplicationContextUtil
            .getBean("vasaServiceCenterService");

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryVasaServiceResponse queryVasaService(@QueryParam("serviceIp") String serviceIp) {
        LOGGER.info("In VASA queryVasaService function. param serviceIp = " + serviceIp);
        QueryVasaServiceResponse response = new QueryVasaServiceResponse();
        try {

            List<NVasaServiceCenter> list = vasaServiceCenterService.queryVasaService(serviceIp);

            response.setVasaService(list);
            response.setResultCode("0");
            response.setResultDescription("queryVasaService successfully.");
        } catch (Exception e) {
            LOGGER.error("queryVasaService fail : " + e);
            response.setResultCode("1");
            response.setResultDescription("queryVasaService fail." + e.getMessage());
        }
        LOGGER.info("End queryVasaService function : " + response.toString());
        return response;
    }

}
