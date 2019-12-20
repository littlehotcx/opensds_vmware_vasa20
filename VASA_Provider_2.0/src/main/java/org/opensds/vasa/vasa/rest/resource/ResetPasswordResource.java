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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.PropertiesManagerUtil;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.ResetPwdRequest;
import org.opensds.vasa.vasa.rest.bean.RoolbackPwdRequest;


@Path("vasa/resetPasswordResource")
public class ResetPasswordResource {

    private Logger LOGGER = LogManager.getLogger(ResetPasswordResource.class);

    @GET
    @Path("detect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader detectStatus() {
        ResponseHeader result = new ResponseHeader();
        result.setResultCode("0");
        result.setResultDescription("service is online");
        return result;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader resetArrayPassword(ResetPwdRequest request) {
        ResponseHeader result = new ResponseHeader();
        try {
            if (AES128System.decryptPwd(ConfigManager.getInstance().getValue("dataBase_password")).equalsIgnoreCase(request.getOldPassword())) {
                PropertiesManagerUtil.updateProperty(PropertiesManagerUtil.DB_PWD_FILENAME, PropertiesManagerUtil.DB_PWD_KEY, request.getNewPassword());
                result.setResultCode("0");
                result.setResultDescription("reset success");
            } else {
                result.setResultCode("-1");
                result.setResultDescription("oldpassword is error.");
            }
        } catch (Exception e) {
            result.setResultCode("1");
            result.setResultDescription("reset fail." + e);
            LOGGER.error("resetArrayPassword error.", e);
        }
        return result;
    }

    @PUT
    @Path("rollback")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader rollbackArrayPassword(RoolbackPwdRequest request) {
        ResponseHeader result = new ResponseHeader();
        try {
            PropertiesManagerUtil.updateProperty(PropertiesManagerUtil.DB_PWD_FILENAME, PropertiesManagerUtil.DB_PWD_KEY, request.getOldPassword());
            result.setResultCode("0");
            result.setResultDescription("rollbackArrayPassword success");
        } catch (Exception e) {
            result.setResultCode("1");
            result.setResultDescription("rollbackArrayPassword fail." + e);
            LOGGER.error("rollbackArrayPassword error.", e);
        }
        return result;
    }


}
