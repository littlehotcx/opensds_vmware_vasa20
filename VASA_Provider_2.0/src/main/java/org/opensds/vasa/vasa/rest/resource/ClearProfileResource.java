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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.VASAUtil;

import org.opensds.vasa.vasa.rest.bean.ResponseHeader;


@Path("vasa/clearProfileResource")
public class ClearProfileResource {

    private Logger LOGGER = LogManager.getLogger(ClearProfileResource.class);

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader clearProfile(@QueryParam("containerIds") String containerIds) {
        ResponseHeader response = new ResponseHeader();
        LOGGER.info("In vasa clearProfile");
        try {
            String[] containerIdArry = containerIds.split(",");
            VASAUtil.clearUnusedVolType(0, true, containerIdArry);

            response.setResultCode("0");
            response.setResultDescription("Clear profile successfully.");
            LOGGER.info("Out vasa clearProfile1");
            return response;
        } catch (Exception e) {
            response.setResultCode("0");
            response.setResultDescription("Some volume types can't be deleted.");
            LOGGER.info("Some volume types can't be deleted.");
        }
        LOGGER.info("Out vasa clearProfile2");
        return response;
    }
}
