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

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.service.Profile2VolTypeService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;

@Path("vasa/updateP2VResource")
public class UpdateP2VResource {

    private Logger LOGGER = LogManager.getLogger(UpdateP2VResource.class);

    private static Profile2VolTypeService profile2VolTypeService = (Profile2VolTypeService) ApplicationContextUtil
            .getBean("profile2VolTypeService");

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader UpdateP2V(@QueryParam("containerId") String containerId) {
        ResponseHeader response = new ResponseHeader();
        LOGGER.info("In vasa UpdateP2V, containerId: " + containerId);
        try {
            Date nowDate = new Date();
            long nowLong = nowDate.getTime();
            profile2VolTypeService.updateDeprecated(containerId, Long.toString(nowLong));
            response.setResultCode("0");
            response.setResultDescription("updateDeprecated to true successfully.");
            LOGGER.info("Out vasa UpdateP2V, operated successfully.");
            return response;
        } catch (Exception e) {
            response.setResultCode("-1");
            response.setResultDescription("UpdateP2V error.");
            LOGGER.error("UpdateP2V error.");
        }
        LOGGER.info("Out vasa UpdateP2V, operation error.");
        return response;
    }
}
