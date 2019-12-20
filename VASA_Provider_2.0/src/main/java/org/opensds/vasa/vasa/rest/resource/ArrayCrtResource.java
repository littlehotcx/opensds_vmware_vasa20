
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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.ArrayCrtRefreshService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.util.ByteConvertUtil;
import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.util.VASAResponseCode;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NArrayCertificate;
import org.opensds.vasa.vasa.db.service.ArrayCertificateService;

import org.opensds.vasa.vasa.rest.bean.ResponseHeader;

import com.vmware.vim.vasa.v20.StorageFault;

@Path("vasa/arraycrt")
public class ArrayCrtResource {

    private ArrayCertificateService arrayCertificateService = ApplicationContextUtil.getBean("arrayCertificateService");
    private ArrayCrtRefreshService arrayCrtRefreshService = ApplicationContextUtil.getBean("arrayCrtRefreshService");
    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();
    private static Logger logger = LogManager.getLogger(ArrayCrtResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader saveArrayCrt(HashMap<String, String> reqMap) {
        ResponseHeader responseBean = new ResponseHeader();
        String crtFilePath = reqMap.get("filePath");
        String arrayid = reqMap.get("arrayId");
        logger.info("crtFilePath=" + crtFilePath + ",arrayid=" + arrayid);
        try {

            if (!crtFilePath.matches("^.+(crt|cer|der)$")) {
                responseBean.setResultCode(VASAResponseCode.storageCrtManagerService.STORAGE_CRT_TYPE_ERROR);
                responseBean.setResultDescription(VASAResponseCode.storageCrtManagerService.STORAGE_CRT_TYPE_ERROR_DESCRIPTION);
                return responseBean;
            }

            NArrayCertificate getArrayCertificate = arrayCertificateService.getByArrayId(arrayid);
            if (null != getArrayCertificate) {
                responseBean.setResultCode(VASAResponseCode.storageCrtManagerService.STORAGE_UNIQ_ID_ALREADY_EXIST);
                responseBean.setResultDescription(VASAResponseCode.storageCrtManagerService.STORAGE_UNIQ_ID_ALREADY_EXIST_DESCRIPTION);
                return responseBean;
            }
            NArrayCertificate arrayCertificate = new NArrayCertificate();
            byte[] file2byte = ByteConvertUtil.file2byte(crtFilePath);
            List<NArrayCertificate> getall = arrayCertificateService.getall();
            for (NArrayCertificate nArrayCertificate : getall) {
                byte[] cacontent = nArrayCertificate.getCacontent();
                if (Arrays.equals(file2byte, cacontent)) {
                    responseBean.setResultCode(VASAResponseCode.storageCrtManagerService.STORAGE_CRT_ALREADY_EXIST);
                    responseBean.setResultDescription(VASAResponseCode.storageCrtManagerService.STORAGE_CRT_ALREADY_EXIST_DESCRIPTION);
                    return responseBean;
                }
            }
            arrayCertificate.setCacontent(file2byte);
            arrayCertificate.setArrayid(arrayid);
            arrayCertificate.setCreateTime(new Date());
            arrayCertificateService.save(arrayCertificate);
            arrayCrtRefreshService.refreshArrayCrt();
            //save sync info
            discoverServiceImpl.notifyLeaderToBroadcastRefresh(IPUtil.getLocalIP(), "syncArrayCert", arrayid);

            responseBean.setResultCode(VASAResponseCode.common.SUCCESS);
            responseBean.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (StorageFault e) {
            // TODO Auto-generated catch block
            logger.error("add crt fail.", e);
            responseBean.setResultCode(VASAResponseCode.common.ERROR);
            responseBean.setResultDescription(VASAResponseCode.common.ERROR_DESC);
        }
        return responseBean;
    }
}
