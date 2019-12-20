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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.RedisUtil;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.VasaRedisKey;
import org.opensds.vasa.vasa.db.model.VasaOperation;
import org.opensds.vasa.vasa.db.model.VasaProperty;
import org.opensds.vasa.vasa.db.service.VasaOperationService;
import org.opensds.vasa.vasa.db.service.VasaPropertyService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.VasaPropertyRes;

@Path("vasa/vasaProperty")
public class VasaPropertyResource {

    private static Logger logger = LogManager.getLogger(VasaPropertyResource.class);
    private static VasaPropertyService vasaPropertyService = ApplicationContextUtil.getBean("vasaPropertyService");
    private static VasaOperationService vasaOperationService = ApplicationContextUtil.getBean("vasaOperationService");

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader updatePropertyValue(VasaProperty vasaProperty) {
        ResponseHeader result = new ResponseHeader();
        if (null == vasaProperty.getId()) {
            logger.error("VasaPropertyResource/updatePropertyValue error. The property id is null!!");
            result.setResultCode(Long.valueOf(VASAResponseCode.common.ERROR));
            result.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        } else {
            try {
                VasaProperty propertyByName = vasaPropertyService.getPropertyByName(VasaProperty.RETAIN_VP_CERTIFICATE_KEY);
                if (propertyByName.getId().equals(vasaProperty.getId())) {
                    String multi_vc_value = RedisUtil.getStringKeyValue(VasaRedisKey.multi_vc_config_value_key);
                    if (null != multi_vc_value && null != vasaProperty.getValue()) {
                        VasaOperation t = new VasaOperation();
                        t.setKey(VasaOperation.RELOAD_SERVER_KEYSTORE_KEY);
                        if (multi_vc_value.equalsIgnoreCase(vasaProperty.getValue())) {
                            t.setValue("false");
                            vasaOperationService.updateData(t);
                        } else {
                            t.setValue("true");
                            vasaOperationService.updateData(t);
                        }
                    }
                }
                vasaPropertyService.updateData(vasaProperty);
                result.setResultCode(Long.valueOf(VASAResponseCode.common.SUCCESS));
                result.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("VasaPropertyResource/updatePropertyValue error.", e);
                result.setResultCode(Long.valueOf(VASAResponseCode.common.ERROR));
                result.setResultDescription(VASAResponseCode.common.ERROR_DESC);
            }
        }
        return result;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaPropertyRes getAllPropertyValue() {
        VasaPropertyRes result = new VasaPropertyRes();
        try {
            List<VasaProperty> all = vasaPropertyService.getAll();
            result.setProperties(all);
            result.setResultCode(Long.valueOf(VASAResponseCode.common.SUCCESS));
            result.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("VasaPropertyResource/getAllPropertyValue error.", e);
            result.setResultCode(Long.valueOf(VASAResponseCode.common.ERROR));
            result.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        logger.debug("VasaPropertyResource/getAllPropertyValue result=" + result);
        return result;
    }

}
