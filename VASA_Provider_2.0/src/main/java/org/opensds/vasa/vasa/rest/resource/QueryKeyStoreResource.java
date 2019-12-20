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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.service.QueryKeyStoreService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;


@Path("vasa/queryKeyStore")
public class QueryKeyStoreResource {

    private Logger LOGGER = LogManager.getLogger(QueryKeyStoreResource.class);

    private static QueryKeyStoreService queryKeyStoreService = (QueryKeyStoreService) ApplicationContextUtil
            .getBean("queryKeyStoreService");

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader QueryKeyStoreExpireDate() {
        ResponseHeader response = new ResponseHeader();
        LOGGER.info("In vasa QueryKeyStoreExpireDate.");
        try {
            String expireDate = queryKeyStoreService.queryKeyStoreExpireDate();
            response.setResultCode("0");
            response.setResultDescription(expireDate);
            LOGGER.info("Out vasa QueryKeyStoreExpireDate, operated successfully.");
            return response;
        } catch (Exception e) {
            response.setResultCode("-1");
            response.setResultDescription("QueryKeyStoreExpireDate error.");
            LOGGER.error("QueryKeyStoreExpireDate error.");
        }
        LOGGER.info("Out vasa QueryKeyStoreExpireDate, operation error.");
        return response;
    }


}
