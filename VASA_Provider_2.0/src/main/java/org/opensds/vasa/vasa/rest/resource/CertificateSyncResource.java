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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.service.ArrayCrtRefreshService;
import org.opensds.vasa.vasa.service.SecureConnectionService;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;


import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NAddStorageArraySync;
import org.opensds.vasa.vasa.db.model.NArrayCertificate;
import org.opensds.vasa.vasa.db.model.NArrayCertificateSync;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.AddStorageArraySyncService;
import org.opensds.vasa.vasa.db.service.ArrayCertificateService;
import org.opensds.vasa.vasa.db.service.ArrayCertificateSyncService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;

import org.opensds.vasa.vasa.rest.bean.NotifySyncTaskRequestBean;

@Path("vasa/certificateSync")
public class CertificateSyncResource {
    private Logger LOGGER = LogManager.getLogger(CertificateSyncResource.class);
    private ArrayCertificateService arrayCertificateService = ApplicationContextUtil.getBean("arrayCertificateService");
    private ArrayCertificateSyncService arrayCertificateSyncService = ApplicationContextUtil.getBean("arrayCertificateSyncService");
    private ArrayCrtRefreshService arrayCrtRefreshService = ApplicationContextUtil.getBean("arrayCrtRefreshService");
    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private AddStorageArraySyncService addStorageArraySyncService = ApplicationContextUtil.getBean("addStorageArraySyncService");

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseHeader notifyToSyncCertificate(NotifySyncTaskRequestBean request) {
        LOGGER.info("In VASA notifyToSyncCertificate function. request is " + request.toString());
        ResponseHeader response = new ResponseHeader();
        try {
            SecureConnectionService secureConnectionService = SecureConnectionService.getInstance();
            if (StringUtils.isEmpty(request.getWork())) {
                LOGGER.error("the work is null or empty");
                //secureConnectionService.queryCertificateToDoSync(false);
            } else if (request.getWork().equalsIgnoreCase("syncArrayCert")) {
                LOGGER.info("Array sync array cert");
                syncArrayCert();
            } else if (request.getWork().equalsIgnoreCase("syncVmwareCert")) {
                LOGGER.info("the work is syncVmwareCert");
                String installType = ConfigManager.getInstance().getValue("vasa.install.type");
                if (installType != null && !installType.equalsIgnoreCase("ha")) {
                    secureConnectionService.queryCertificateToDoSync(false);
                }
            }
            response.setResultCode("0");
            response.setResultDescription("notifyToSyncCertificate successfully.");
        } catch (Exception e) {
            LOGGER.error("notifyToSyncCertificate fail : " + e);
            response.setResultCode("1");
            response.setResultDescription("notifyToSyncCertificate fail." + e.getMessage());
        }
        LOGGER.info("End notifyToSyncCertificate function : " + response.toString());
        return response;
    }


    public void syncArrayCert() {
        LOGGER.info("In syncArrayCert function.");
        try {
            String localIp = IPUtil.getLocalIP();
            LOGGER.info("syncArrayCert current ip : " + localIp);
            List<NArrayCertificate> needSyncArrayCerts = arrayCertificateService.queryNeedSyncArrayCerts(localIp);

            if (needSyncArrayCerts.size() != 0) {
                arrayCrtRefreshService.refreshArrayCrt();
                for (NArrayCertificate arrayCertificate : needSyncArrayCerts) {
                    NArrayCertificateSync arrayCertificateSync = new NArrayCertificateSync();
                    arrayCertificateSync.setCerId(arrayCertificate.getArrayid());
                    arrayCertificateSync.setSyncIp(localIp);
                    arrayCertificateSync.setSyncTime(DateUtil.getUTCDate());
                    arrayCertificateSyncService.addArrayCertificateSync(arrayCertificateSync);
                }
            }
        } catch (Exception e) {
            LOGGER.info("syncArrayCert fail.", e);
        }
    }

    //no need to use this function.
    public void syncArrayAdd() {
        LOGGER.info("In syncArrayAdd function.");
        try {
            String localIp = IPUtil.getLocalIP();
            LOGGER.info("syncArrayAdd current ip : " + localIp);
            List<StorageInfo> needSyncArrayInfo = storageManagerService.needSyncStorageInfo(localIp);
            if (needSyncArrayInfo.size() != 0) {
				
				/*
				//上报事件
		        Set<String> cachedArrayIds = dataManager.getArrayId();
		        discoverServiceImpl.updateArrayIds();
		        Set<String> arrayIds = dataManager.getArrayId();
		        
		        //产生阵列添加删除事件,返回给所有订阅了Config事件的vcenter server
		        List<String> ucUUIDs = VASAUtil.getConfigEventUcUUIDs();
		        DiscoverService.getInstance().appendConfigStorageArrayEvent
		        (ucUUIDs, VASAUtil.convertSet2List(cachedArrayIds), VASAUtil.convertSet2List(arrayIds));
		        */

                for (StorageInfo storageInfo : needSyncArrayInfo) {
                    NAddStorageArraySync addStorageArraySync = new NAddStorageArraySync();
                    addStorageArraySync.setArrayId(storageInfo.getId());
                    addStorageArraySync.setSyncIp(localIp);
                    addStorageArraySync.setSyncTime(DateUtil.getUTCDate());
                    addStorageArraySyncService.addStorageArrayFuncSync(addStorageArraySync);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
