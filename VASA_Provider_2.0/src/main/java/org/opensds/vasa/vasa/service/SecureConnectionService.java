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

package org.opensds.vasa.vasa.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.NUser;
import org.opensds.vasa.vasa.db.model.NVmwareCertificate;
import org.opensds.vasa.vasa.db.model.NVmwareCertificateSync;
import org.opensds.vasa.vasa.db.service.UserManagerService;
import org.opensds.vasa.vasa.db.service.VasaServiceCenterService;
import org.opensds.vasa.vasa.db.service.VmwareCertificateService;
import org.opensds.vasa.vasa.db.service.VmwareCertificateSyncService;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.runnable.UpdateHostIps;
import org.opensds.vasa.vasa.util.ByteConvertUtil;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.FileManager;
import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.LockManager;
import org.opensds.vasa.vasa.util.RestConstant;
import org.opensds.vasa.vasa.util.RestRequestMessage;
import org.opensds.vasa.vasa.util.RestUtilsOfOM;
import org.opensds.vasa.vasa.util.SSLUtil;
import org.opensds.vasa.vasa.util.SessionContext;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidCertificate;
import com.vmware.vim.vasa.v20.InvalidLogin;
import com.vmware.vim.vasa.v20.InvalidSession;
import com.vmware.vim.vasa.v20.PermissionDenied;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MessageCatalog;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.StorageCatalogEnum;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;
import com.vmware.vim.vasa.v20.data.xsd.VasaProviderInfo;
import com.vmware.vim.vasa.v20.data.xsd.VendorModel;
import com.vmware.vim.vasa.v20.xsd.QueryCACertificateRevocationListsResponse;
import com.vmware.vim.vasa.v20.xsd.RefreshCACertificatesAndCRLs;
import com.vmware.vim.vasa.v20.xsd.RequestCSRResponse;

public class SecureConnectionService extends BaseService {
    // 单例
    private static SecureConnectionService instance;

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(SecureConnectionService.class);

    // Provider版本
    private static final String PROVIDER_VERSION = VASAUtil.PROVIDER_VERSION;

    // 逗号分隔符
    private static final String VASA_SPLITE = ",";

    private StorageService storageService = StorageService.getInstance();

    // 证书保存到DB服务
    private VmwareCertificateService certificateService = ApplicationContextUtil.getBean("vmwareCertificateService");

    // 证书同步结果服务
    private VmwareCertificateSyncService certificateSyncService = ApplicationContextUtil
            .getBean("vmwareCertificateSyncService");

    // vasa服务中心
    private VasaServiceCenterService vasaServiceCenterService = ApplicationContextUtil
            .getBean("vasaServiceCenterService");

    // 用户管理
    private UserManagerService userManagerService = ApplicationContextUtil.getBean("userManagerService");

    //private VasaInfoService vasaInfoService = ApplicationContextUtil.getBean("vasaInfoService");

    private SSLUtil sslUtil;

    private DataUtil dataUtil;

    // VASA Provider信息
    private VasaProviderInfo vpInfo;

    /*
     * 构造方法
     */
    private SecureConnectionService() {
        LOGGER.info("Provider version: " + PROVIDER_VERSION);
        this.dataUtil = DataUtil.getInstance();
    }

    /**
     * Accessor method for singleton instance
     *
     * @return SecureConnectionService 返回结果
     */
    public static synchronized SecureConnectionService getInstance() {
        if (null == instance) {
            instance = new SecureConnectionService();
        }
        return instance;
    }

    public boolean checkCurrentNodeIsMaster() {
//		try {
//			String localIp = IPUtil.getLocalIP();
//
//			List<NVasaServiceCenter> master = vasaServiceCenterService.queryCurrentMaster();
//
//			if (master.size() == 0) {
//				return false;
//			}
//
//			if (master.get(0).getServiceIp().equals(localIp)) {
//				return true;
//			}
//
//			return false;
//		} catch (StorageFault e) {
//			LOGGER.error("checkCurrentNodeIsMaster error.", e);
//
//			return false;
//		}
        return DistributedMasterHandler.isMaster();
    }

    public void queryCurrentMasterOrCompeteIt() {
//		try {
//			String localIp = IPUtil.getLocalIP();
//
//			List<NVasaServiceCenter> master = vasaServiceCenterService.queryCurrentMaster();
//
//			if (master.size() == 0) {
//				vasaServiceCenterService.competeMaster(localIp);
//			} else {
//				NVasaServiceCenter serviceCenter = master.get(0);
//				LOGGER.info("current ip : " + localIp + " , master ip : " + serviceCenter.getServiceIp());
//				if (serviceCenter.getServiceIp().equals(localIp)) {
//					if (System.currentTimeMillis() - serviceCenter.getLastModifiedTime().getTime() > MagicNumber.INT10
//							* MagicNumber.INT60 * MagicNumber.INT1000) {
//						vasaServiceCenterService.competeMaster(localIp);
//						LOGGER.info("current node is master node , but the updateTime has expired , then compete master : "+localIp);
//					} else {
//						vasaServiceCenterService.updateMaster(localIp);
//						LOGGER.info("current node is master node , just keep alive : "+localIp);
//					}
//				} else {
//					if (System.currentTimeMillis() - serviceCenter.getLastModifiedTime().getTime() > MagicNumber.INT10
//							* MagicNumber.INT60 * MagicNumber.INT1000) {
//						vasaServiceCenterService.competeMaster(localIp);
//						LOGGER.info("current node is not master node and the updateTime has expired , then compete master : "+localIp);
//					}
//				}
//			}
        DistributedMasterHandler.isMaster();
//		} catch (StorageFault e) {
//			LOGGER.error("queryCurrentMasterOrCompeteIt error.", e);
//		}
    }

    /**
     * 方法 ： init
     *
     * @param sslUtilIn 方法参数：sslUtilIn
     */
    public void init(SSLUtil sslUtilIn) {
        this.sslUtil = sslUtilIn;
    }

    public void queryCertificateToDoSync(boolean needInvalidateSession) {
        try {
            String localIp = IPUtil.getLocalIP();
            LOGGER.info("queryCertificateToDoSync current ip : " + localIp);

            // query not yet sync cert
            List<NVmwareCertificate> needSyncCerts = certificateService.queryNeedSyncCerts(localIp);

            // check and add cert to refresh ssl
            String fileName = ConfigManager.getInstance().getValue("vasa.ssl.truststoreFile");
            String trustStoreFileName = FileManager.getBasePath() + "conf" + File.separator + fileName;

            boolean hasRefresh = false;
            for (NVmwareCertificate certificate : needSyncCerts) {

                ByteConvertUtil.byte2file(certificate.getCaContent(), trustStoreFileName);

                // insert sync result
                NVmwareCertificateSync sync = new NVmwareCertificateSync();
                sync.setVcid(certificate.getId());
                sync.setSyncIp(localIp);
                sync.setSyncTime(DateUtil.getUTCDate());

                certificateSyncService.addVmwareCertificateSync(sync);

                hasRefresh = true;
            }

            LOGGER.debug("queryCertificateToDoSync/need invalidate session :" + needInvalidateSession);
            if (hasRefresh) {
                SSLUtil.refreshTrustStore();
            }

            if (needInvalidateSession) {
                invalidateSession();
            }

        } catch (Exception localException) {
            LOGGER.error("StorageFault/queryCertificateToDoSync failed e: ", localException);
        }
    }

    public void refreshAndNotifyLeaderToBroadcast(String currentNodeIp) throws StorageFault, InvalidCertificate {
        String fileName = ConfigManager.getInstance().getValue("vasa.ssl.truststoreFile");
        String trustStoreFileName = FileManager.getBasePath() + "conf" + File.separator + fileName;

        byte[] bytes = ByteConvertUtil.file2byte(trustStoreFileName);

        NVmwareCertificate certificate = new NVmwareCertificate();
        certificate.setCaContent(bytes);
        certificate.setCreationTime(DateUtil.getUTCDate());

        certificateService.addVmwareCertificate(certificate);

        NVmwareCertificateSync certificateSync = new NVmwareCertificateSync();
        certificateSync.setVcid(certificate.getId());
        certificateSync.setSyncIp(currentNodeIp);
        certificateSync.setSyncTime(DateUtil.getUTCDate());

        certificateSyncService.addVmwareCertificateSync(certificateSync);

        // notify leader
        String leaderServiceUrl = ConfigManager.getInstance().getValue("om.rest.url");

        List<NUser> omUsers = userManagerService.getUserInfoByUsername("admin");
        if (omUsers == null || omUsers.size() == 0) {
            LOGGER.error("get om user size is 0.");
            throw FaultUtil.storageFault("get om user size is 0.");
        }

        RestRequestMessage requestMessage = new RestRequestMessage();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceIp", currentNodeIp);
        parameters.put("work", "syncVmwareCert");
        requestMessage.setHttpMethod(RestConstant.HTTP_METHOD_POST);
        requestMessage.setPayload(parameters);

        RestUtilsOfOM restClient = RestUtilsOfOM.getInstance(leaderServiceUrl, omUsers.get(0).getUsername(),
                omUsers.get(0).getPassword());
        String result = restClient.sendMessage(requestMessage, "/notifySyncCert");
        if (result == null) {
            LOGGER.error("request notifySyncCert not available.");
            throw FaultUtil.storageFault("request notifySyncCert not available.");
        }
        if ("401".equals(result)) {
            LOGGER.error("request notifySyncCert return 401.");
            throw FaultUtil.storageFault("request notifySyncCert return 401.");
        }

        ResponseHeader response = new Gson().fromJson(result, ResponseHeader.class);
        if (response.getResultCode() != 0) {
            LOGGER.error("request notifySyncCert fail . err : " + response.getResultDescription());
            throw FaultUtil.storageFault("request notifySyncCert fail . err : " + response.getResultDescription());
        }
    }

    /**
     * vasaService interface
     *
     * @param username       方法参数：username
     * @param password       方法参数：password
     * @param certificateStr 方法参数：certificateStr
     * @return VasaProviderInfo 返回结果
     * @throws InvalidCertificate 异常：InvalidCertificate
     * @throws InvalidLogin       异常：InvalidLogin
     * @throws InvalidSession     异常：InvalidSession
     * @throws StorageFault       异常：StorageFault
     */

    public VasaProviderInfo registerVASACertificate(String userName, String passW0rd, String cert)
            throws InvalidCertificate, InvalidLogin, InvalidSession, StorageFault {
        try {

            String clientAddress = this.sslUtil.checkHttpRequest(false, false);

            if (LockManager.isLocked(userName)) {
                LOGGER.error("StorageFault/registerVASACertificate failed , user locked realse time : "
                        + LockManager.getLockRealseTime(userName));
                throw FaultUtil.storageFault("user locked realse time : " + LockManager.getLockRealseTime(userName));
            }

            String alias = null;
            if ((userName == null) && (passW0rd == null))
                alias = "server";
            else
                verifyPassword(userName, passW0rd);
            X509Certificate localX509Certificate = (X509Certificate) this.sslUtil.buildCertificate(cert);
            localX509Certificate.checkValidity();

            LOGGER.debug("registerVASACertificate() valid username and password");
            if (!this.sslUtil.certificateIsTrusted(localX509Certificate)) {
                this.sslUtil.addCertificateToTrustStore(alias, clientAddress, localX509Certificate);
                LOGGER.info("registerVASACertificate() new cert added as trusted");
                SSLUtil.refreshTrustStore();
                invalidateSession();
            } else {
                LOGGER.info("registerVASACertificate() cert was already trusted.");
            }

            LOGGER.debug("registerVASACertificate add certificate to db and do sync.");

            String installer = dataUtil.getVasaInfoMapByKey("InstallType");
            //String installer = vasaInfoService.getValueByKey("Installer");
            if ("Staas".equalsIgnoreCase(installer)) {
                LOGGER.info("The Staas environment need to notify other vasa provider to refresh cert.");
                refreshAndNotifyLeaderToBroadcast(IPUtil.getLocalIP());
            } else {
                LOGGER.info("The vasa environment no need to notify other vasa provider to refresh cert.");
            }

            return this.vpInfo;
        } catch (InvalidSession localInvalidSession) {
            LOGGER.error("InvalidSession/invalid session.");
            throw localInvalidSession;
        } catch (InvalidCertificate localInvalidCertificate) {
            LOGGER.error("InvalidCertificate/InvalidCertificate error.", localInvalidCertificate);
            throw localInvalidCertificate;
        } catch (CertificateExpiredException localCertificateExpiredException) {
            LOGGER.error("InvalidCertificate/CertificateExpiredException error.", localCertificateExpiredException);
            throw FaultUtil.invalidCertificate(localCertificateExpiredException);
        } catch (CertificateNotYetValidException localCertificateNotYetValidException) {
            LOGGER.error("InvalidCertificate/CertificateNotYetValidException error.",
                    localCertificateNotYetValidException);
            throw FaultUtil.invalidCertificate(localCertificateNotYetValidException);
        } catch (InvalidLogin localInvalidLogin) {
            LOGGER.error("InvalidLogin/invalid login.");
            throw localInvalidLogin;
        } catch (Exception localException) {
            LOGGER.error("StorageFault/registerVASACertificate failed e: ", localException);
            throw FaultUtil.storageFault("runtime ", localException);
        }
    }

    public String loadConfigFile(String filename) throws StorageFault {
        InputStreamReader isr = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filename));
            // @SuppressWarnings("resource")
            isr = new InputStreamReader(is, "UTF-8");
            int ch = 0;

            StringBuilder sb = new StringBuilder();

            while ((ch = isr.read()) != -1) {
                String str = (char) ch + "";
                sb.append(str);
            }

            return sb.toString();

        } catch (FileNotFoundException e) {
            throw FaultUtil.storageFault("Exception ", e);
        } catch (IOException e) {
            throw FaultUtil.storageFault("Exception ", e);
        } finally {
            try {
                /**
                 * 修改CodeDEX问题：FORTIFY.Unreleased_Resource start Modified by
                 * wWX315527 2016/11/19
                 */
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                /**
                 * 修改CodeDEX问题：FORTIFY.Unreleased_Resource end Modified by
                 * wWX315527 2016/11/19
                 */
            } catch (IOException e) {
                LOGGER.error("failed to close isr");
            }
        }
    }

    public RequestCSRResponse requestCSR() throws InvalidSession, PermissionDenied, StorageFault {

        sslUtil.generateCSR();

        String strCsr = sslUtil.loadCSR();

        RequestCSRResponse response = new RequestCSRResponse();
        // CSR Verification test --start 这儿的修改是为了认证用例测试
        // Modified By wWX315527
        if (strCsr != null) {
            if (strCsr.contains(" NEW ")) {
                strCsr = strCsr.replaceAll(" NEW ", " ");
            }
            if (strCsr.contains("\r")) {
                strCsr = strCsr.replaceAll("\r", "");
            }
        }
        // CSR Verification test --end
        response.setReturn(strCsr);
        return response;
    }

    public void refreshCACertificatesAndCRLs(RefreshCACertificatesAndCRLs parameters) {

    }

    public List<String> queryCACertificates() throws InvalidSession, StorageFault {
        return sslUtil.getAllCertificatesFromKeystore();
    }

    public QueryCACertificateRevocationListsResponse queryCACertificateRevocationLists() {
        QueryCACertificateRevocationListsResponse response = new QueryCACertificateRevocationListsResponse();
        response.getReturn().add("");
        response.getReturn().add("");
        return response;
    }

    private synchronized void updateEsxHostIP(String hostId, List<String> newIps) {
        dataUtil.setHostIpsOfEsxHost(hostId, newIps);
        LOGGER.info("save esxhostId:" + hostId);
        UpdateHostIps updater = new UpdateHostIps(hostId, newIps);
        updater.updateCommFs();
        new Thread(updater, "updateShare-" + hostId + "_" + System.currentTimeMillis()).start();
    }

    /**
     * vasaService interface
     *
     * @param uc 方法参数：uc
     * @return VasaProviderInfo 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws InvalidSession  异常：InvalidSession
     * @throws StorageFault    异常：StorageFault
     */
    public VasaProviderInfo setContext(UsageContext uc) throws InvalidArgument, InvalidSession, StorageFault {
        String clientAddress = "unknown";
        try {
            // LOGGER.info("setContext called. UsageContext:\n" +
            // JaxbUtil.convertToXml(uc));
            SessionContext sc = null;
            clientAddress = sslUtil.checkHttpRequest(true, false);

            validateUsageContext(uc);
            for (String ip : uc.getHostIoIpAddress()) {
                LOGGER.info("host ip: " + ip);

            }
            // Tear down any existing session
            invalidateSessionWithoutSetCookie();

            // Create new session.
            sc = SessionContext.createSession(uc, clientAddress);
            vpInfo.setSessionId(sc.getSessionId());

            sslUtil.setHttpResponse(sc);


            // 将该usgaecontext 加入到列表
            dataUtil.addUsageContextUUID(VASAUtil.getUcUUID(uc), uc);
            // if(uc.getSubscribeEvent() != null)
            // {
            // dataUtil.getSubscribeEventForVcenter().put(VASAUtil.getUcUUID(uc),
            // uc.getSubscribeEvent());
            // }
//			dataUtil.setHostIps(uc.getHostIoIpAddress());
            String hostId = uc.getHostGuid();

            if (hostId != null) {
                updateEsxHostIP(hostId, uc.getHostIoIpAddress());
            }
            LOGGER.debug("Just check the dataUtil:" + dataUtil.toString());
            // LOGGER.info("vpInfo is:\n" +
            // JaxbUtil.convertToXml(vpInfo));

            return vpInfo;
        } catch (StorageFault sf) {
            LOGGER.error("StorageFault/setContext StorageFault.");
            throw sf;
        } catch (InvalidSession is) {
            LOGGER.error("InvalidSession/setContext InvalidSession.");
            throw is;
        } catch (Exception e) {
            LOGGER.error("StorageFault/setContext failed with unknown exception. Converting to StorageFault: ", e);
            throw FaultUtil.storageFault("runtime ", e);
        }
    }

    /**
     * internal routine to perform Certificate unregister operation
     */
    private void unregisterCertificate(X509Certificate x509Cert) throws InvalidCertificate, StorageFault {
        try {
            if (sslUtil.certificateIsTrusted(x509Cert)) {
                LOGGER.debug("unregisterCertificate(): cert removed from trusted");
                sslUtil.removeCertificateFromTrustStore((Certificate) x509Cert);
                SSLUtil.refreshTrustStore();
                if (sslUtil.certificateIsTrusted(x509Cert)) {
                    LOGGER.error("Certificate could not be removed from the trustStore.");
                    throw FaultUtil.storageFault("Certificate could not be removed from the trustStore.");
                }
                invalidateSession();
            } else {
                LOGGER.error("Certificate not registered.");
                throw FaultUtil.invalidCertificate("Certificate not registered.");
            }
        } catch (InvalidCertificate ic) {
            LOGGER.error("unregisterCertificate InvalidCertificate.");
            throw ic;
        } catch (Exception e) {
            LOGGER.error("unregisterCertificate failed with unknown exception. Converting to StorageFault: ", e);
            throw FaultUtil.storageFault("runtime ", e);
        }
    }

    /**
     * vasaService interface
     *
     * @param existingCertificate 方法参数：existingCertificate
     * @throws InvalidCertificate 异常：InvalidCertificate
     * @throws InvalidSession     异常：InvalidSession
     * @throws StorageFault       异常：StorageFault
     */
    public void unregisterVASACertificate(String existingCertificate)
            throws InvalidCertificate, InvalidSession, StorageFault {
        try {
            LOGGER.info("unregisterVASACertificate called existingcertificate is " + VASAUtil.LINE_SEPRATOR
                    + existingCertificate);
            /*
             * Need to have a valid SSL session, but VASA session not required
             */
            sslUtil.checkHttpRequest(false, false);

            X509Certificate x509Cert = (X509Certificate) sslUtil.buildCertificate(existingCertificate);
            unregisterCertificate(x509Cert);
            String installer = dataUtil.getVasaInfoMapByKey("InstallType");
            if ("Staas".equalsIgnoreCase(installer)) {
                LOGGER.info("The Staas environment need to notify other vasa provider to refresh cert.");
                refreshAndNotifyLeaderToBroadcast(IPUtil.getLocalIP());
            } else {
                LOGGER.info("The vasa environment no need to notify other vasa provider to refresh cert.");
            }
        } catch (InvalidSession is) {
            // thrown by sslUtil.checkHttpRequest()
            LOGGER.error("InvalidSession/unregisterVASACertificate invalidSession.");
            throw is;
        } catch (InvalidCertificate ic) {
            // thrown by sslUtil.buildCertificate()
            // thrown by unregisterCertificate()
            LOGGER.error("InvalidCertificate/unregisterVASACertificate invalidCertificate.");
            throw ic;
        } catch (StorageFault sf) {
            // thrown by unregisterCertificate()
            LOGGER.error("StorageFault/InvalidCertificate InvalidCertificate.");
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/unregisterVASACertificate failed with unknown exception. "
                    + "Converting to StorageFault: ", e);
            throw FaultUtil.storageFault("runtime ", e);
        }
    }

    /**
     * verify contents of usage context
     */
    private void validateUsageContext(UsageContext uc) throws StorageFault, InvalidArgument {
        try {
            if (null == uc) {
                LOGGER.error("InvalidArgument/UsageContext is null");
                throw FaultUtil.invalidArgument("UsageContext is null");
            }

            int count = validateHostInitiators(uc);
            count += validateMountInfo(uc.getMountPoint());
            if (count == 0) {
                LOGGER.error("Either host initiators or mount points must be specified");
            }
        } catch (InvalidArgument ia) {
            // thrown by validateHostInitiators
            // thrown by validateMountInfo
            // thrown by this function
            LOGGER.error("InvalidArgument/validateUsageContext InvalidArgument.");
            throw ia;
        } catch (StorageFault sf) {
            // thrown by validateHostInitiators
            // thrown by validateMountInfo
            LOGGER.error("StorageFault/validateUsageContext StorageFault.");
            throw sf;
        } catch (Exception e) {
            LOGGER.error("StorageFault/validateUsageContext failed e: ", e);
            throw FaultUtil.storageFault("validateUsageContext unexpected exception. Convert to StorageFault. " + e);
        }
    }

    private void validateUsageContextByInitiators(UsageContext uc) throws StorageFault, InvalidArgument {
        try {
            if (null == uc) {
                LOGGER.error("InvalidArgument/UsageContext is null");
                throw FaultUtil.invalidArgument("UsageContext is null");
            }

            int count = validateHostInitiators(uc);
            if (count == 0) {
                LOGGER.error("Either host initiators or mount points must be specified");
            }
        } catch (InvalidArgument ia) {
            // thrown by validateHostInitiators
            // thrown by validateMountInfo
            // thrown by this function
            LOGGER.error("validateUsageContext InvalidArgument.");
            throw ia;
        } catch (StorageFault sf) {
            // thrown by validateHostInitiators
            // thrown by validateMountInfo
            LOGGER.error("validateUsageContext StorageFault.");
            throw sf;
        } catch (Exception e) {
            LOGGER.error("validateUsageContext failed e: ", e);
            throw FaultUtil.storageFault("validateUsageContext unexpected exception. Convert to StorageFault. " + e);
        }
    }

    /**
     * Verify contents of the MountInfo array.
     */
    private int validateMountInfo(List<MountInfo> infos) throws StorageFault {
        MountInfo[] mountInfos = ListUtil.list2ArrayMI(infos);
        // try
        // {
        if (Util.isEmpty(mountInfos)) {
            // an empty mi is valid
            return 0;
        }

        /*
         * Need to check that each Mount Point exists. Return an error if one
         * does not.
         */
        int validCount = 0;
        MountInfo[] lmi = new MountInfo[1];
        lmi[0] = new MountInfo();
        for (int i = 0; i < mountInfos.length; i++) {
            if (mountInfos[i].getServerName() == null || mountInfos[i].getFilePath() == null) {
                try {
                    LOGGER.error("InvalidArgument/Invalid Mount Info format");
                    throw FaultUtil.invalidArgument("Invalid Mount Info format");
                } catch (InvalidArgument e) {
                    LOGGER.error("StorageFault/validateMountInfo failed e: ", e);
                    throw FaultUtil.storageFault("validateMountInfo unexpected error. Convert to StorageFault." + e);
                }
            }

            lmi[0].setServerName(mountInfos[i].getServerName());
            lmi[0].setFilePath(mountInfos[i].getFilePath());
            try {
                List<DFileSystem> dfs;
                dfs = storageService.queryStorageFileSystems(infos);
                if ((dfs != null) && (dfs.size() > 0)) {
                    validCount++;
                }
            } catch (InvalidArgument e) {
                LOGGER.debug("validateMountInfo queryStorageFileSystems - NOT FOUND with "
                        + mountInfos[i].getServerName() + " " + mountInfos[i].getFilePath());
            } catch (Exception e) {
                LOGGER.debug("validateMountInfo queryStorageFileSystems - NOT FOUND with "
                        + mountInfos[i].getServerName() + " " + mountInfos[i].getFilePath());
            }
        }
        return validCount;
        // }
        // catch (Exception e)
        // {
        // LogManager.error("validateMountInfo failed e: ", e);
        // throw
        // FaultUtil.storageFault("validateMountInfo unexpected error. Convert
        // to StorageFault."
        // + e);
        // }
    }

    /**
     * Verify contents of the HostInitiatorInfo array.
     */
    private int validateHostInitiators(UsageContext uc) throws StorageFault {
        try {
            HostInitiatorInfo[] hii = ListUtil.list2ArrayHostInit(uc.getHostInitiator());
            if (Util.isEmpty(hii)) {
                // an empty hii is valid
                return 0;
            }
            int hiidCtrTemp = 0;
            /*
             * Need to check that each HostInitiator exists. If not ignore the
             * HBA and continue.
             */
            String[] hiiIds = null;
            for (int i = 0; i < hii.length; i++) {
                hiiIds = storageService.getHostInitiatorIdsFromUsageContext(uc, i);
                if (hiiIds != null && (!Util.isEmpty(hiiIds))) {
                    hiidCtrTemp++;
                }

            }
            return hiidCtrTemp;
        } catch (Exception e) {
            LOGGER.error("StorageFault/validateHostInitiators failed e: ", e);
            throw FaultUtil.storageFault("validateHostInitators unexpected error. Convert to StorageFault.", e);
        }
    }

    /**
     * verifyPassword() Check for a valid username and password : This may
     * eventually be replaced with a query to an LDAP server.
     *
     * @throws StorageFault
     */
    private void verifyPassword(String username, String password) throws InvalidLogin, StorageFault {
        LOGGER.info("In verifyPassword function, the request username=" + username);
        UserManagerService userManagerService = ApplicationContextUtil.getBean("userManagerService");
        List<NUser> userList = userManagerService.getUserInfoByUsername(username);
        if (userList.size() == 0) {
            LOGGER.error("StorageFault/verifyPassword error : get user size is 0 . ");
            throw FaultUtil.invalidLogin("invalid username or password");
        }
        NUser user = userList.get(0);
        if (!password.equals(user.getPassword())) {

            LockManager.lockUser(username);
            if (LockManager.isLocked(username)) {
                LOGGER.error("StorageFault/verifyPassword error , user locked realse time : " + LockManager.getLockRealseTime(username));
                throw FaultUtil.storageFault("user locked realse time : " + LockManager.getLockRealseTime(username));
            }

            LOGGER.error("invalid username or password");
            throw FaultUtil.invalidLogin("invalid username or password");
        }
        //clear login lock count
        LockManager.resetLock(username);
    }

    /**
     * remove the current Session context and VASA_SESSION_ID cookie
     */
    private void invalidateSession() throws InvalidSession {
        try {
            String str = this.sslUtil.getCookie(SSLUtil.VASA_SESSIONID_STR);
            LOGGER.info("invalidateSession(" + VASAUtil.replaceSessionId(str) + ") started");
            if (str != null)
                SessionContext.removeSession(str);
            LOGGER.info("invalidateSession(" + VASAUtil.replaceSessionId(str) + ") completed");
            this.sslUtil.setCookie(SSLUtil.VASA_SESSIONID_STR, SessionContext.INVALID_SESSION_ID);
            this.vpInfo.setSessionId(SessionContext.INVALID_SESSION_ID);
        } catch (Exception localException) {
            LOGGER.error("InvalidSession/Could not find session context " + localException);
            throw FaultUtil.invalidSession("Could not find session context " + localException);
        }
    }

    private void invalidateSessionWithoutSetCookie() throws InvalidSession {
        try {
            String str = this.sslUtil.getCookie(SSLUtil.VASA_SESSIONID_STR);
            LOGGER.info("invalidateSession(" + VASAUtil.replaceSessionId(str) + ") started");
            if (str != null)
                SessionContext.removeSession(str);
            LOGGER.info("invalidateSession(" + VASAUtil.replaceSessionId(str) + ") completed");
            this.vpInfo.setSessionId(SessionContext.INVALID_SESSION_ID);
        } catch (Exception localException) {
            LOGGER.error("InvalidSession/Could not find session context " + localException);
            throw FaultUtil.invalidSession("Could not find session context " + localException);
        }
    }

    /**
     * Craft the VASAProvider info
     *
     * @return VasaProviderInfo 返回结果
     */
    public VasaProviderInfo initializeVasaProviderInfo() {
        vpInfo = new VasaProviderInfo();
        VendorModel vendorModel = new VendorModel();
        vendorModel.setModelId("VP");
        vendorModel.setVendorId("VP");

        vpInfo.setName("OPEN SDS VASA");
        vpInfo.setVasaApiVersion("2.0");
        vpInfo.setVasaProviderVersion("2.1.05");
        vpInfo.getSupportedVendorModel().add(vendorModel);// List of vendor‐model data.
        vpInfo.setSessionId("0");
        vpInfo.setDefaultSessionTimeoutInSeconds(SessionContext.DEFAULT_SESSION_TIMEOUT);

        vpInfo.setDefaultNamespace("org.opensds.vasaprovider");
        String vpId = "";
        String installer = dataUtil.getVasaInfoMapByKey("InstallType");
        if ("Staas".equalsIgnoreCase(installer)) {
            vpId = "29a2d254-e2ab-4fca-9faa-62aa8d44e783";
            LOGGER.info("The installer is Staas, the vpId is the same. vpId = " + vpId);
        } else {
            vpId = ConfigManager.getInstance().getValue("vasa.identifier.id");
            LOGGER.info("The installer is vasa, the vpId is the diff. vpId = " + vpId);
        }
        if (vpId != null && !"".equals(vpId)) {
            vpInfo.setUid(vpId);
        } else {
            LOGGER.error("invalid vpId");
            vpInfo.setUid(UUID.randomUUID().toString());
        }

        vpInfo.getSupportedProfile().add("ProfileBasedManagementProfile");
        vpInfo.setNeedsExplicitActivation(false);
        vpInfo.setMaxConcurrentRequestsPerSession(16L);
        return vpInfo;
    }

    /**
     * called by vasaService APIs to get the list of Host Initiator Ids
     * associated with the given UsageContext.
     *
     * @return String[] 返回结果
     * @throws InvalidSession 异常：InvalidSession
     * @throws StorageFault   异常：StorageFault
     */
    public String[] getHostInitiatorIds() throws InvalidSession, StorageFault {
        try {
            UsageContext uc = getUsageContext();
            HostInitiatorInfo[] hii = ListUtil.list2ArrayHostInit(uc.getHostInitiator());
            if (Util.isEmpty(hii)) {
                // an empty hii is valid
                return new String[0];
            }
            return storageService.getHostInitiatorIdsFromUsageContext(uc, -1);
        } catch (StorageFault sf) {
            // thrown by getHostInitiatorIdsFromUsageContext
            LOGGER.error("getHostInitiatorIds StorageFault.");
            throw sf;
        } catch (InvalidSession is) {
            // thrown by getUsageContext()
            LOGGER.error("getHostInitiatorIds InvalidSession.");
            throw is;
        } catch (Exception e) {
            LOGGER.error(this.getClass().getName() + "getHostInitiatorIds failed: ", e);
            throw FaultUtil.storageFault("could not get host initiators from user context: ", e);
        }
    }

    /**
     * called by vasaService APIs to verify the connection and get the
     * UsageContext.
     *
     * @return UsageContext 返回结果
     * @throws InvalidSession 异常：InvalidSession
     * @throws StorageFault   异常：StorageFault
     */
    public UsageContext getUsageContext() throws InvalidSession, StorageFault {
        try {
            // verify valid SSL and VASA Sessions.
            // String clientAddress = sslUtil.checkHttpRequest(true, true);

            String sessionId = sslUtil.getCookie(SSLUtil.VASA_SESSIONID_STR);
            if (null == sessionId) {
                // modified: 如果sessionid是空
                sslUtil.checkHttpRequest(false, true);
                // this should "never happen" if checkHttpRequest does not
                // throw an exception

                // throw
                // FaultUtil.storageFault("getUsageContext internal error.");
            }
            SessionContext sc = SessionContext.lookupSessionContextBySessionId(sessionId);
            if (null == sc) {
                // this should "never happen" if checkHttpRequest does not
                // throw an exception
                throw FaultUtil.storageFault("getUsageContext internal error.");
            }

            sslUtil.setHttpResponse(sc);
            UsageContext uc = sc.getUsageContext();
            if (null == uc) {
                throw FaultUtil.storageFault("UsageContext is not set");
            }

            // 2015-11-18 setContext时校验下即可，后续直接用，减少不必要校验
            validateUsageContextByInitiators(uc);
            return uc;
        } catch (StorageFault sf) {
            // thrown by this function
            LOGGER.error("getUsageContext failed: ", sf);
            throw sf;
        } catch (InvalidSession is) {
            // thrown by checkHttpRequest
            LOGGER.error("getUsageContext InvalidSession.");
            throw is;
        } catch (Exception e) {
            LOGGER.error("getUsageContext failed: ", e);
            throw FaultUtil.invalidSession("getUsageContext unexpected error. Convert to StorageFault.", e);
        }
    }

    /**
     * called by vasaService APIs to get the list of Host File Systems
     * associated with the given UsageContext.
     *
     * @return MountInfo[] 返回结果
     * @throws InvalidSession 异常：InvalidSession
     * @throws StorageFault   异常：StorageFault
     */
    public MountInfo[] getMountInfo() throws InvalidSession, StorageFault {
        try {
            /*
             * This call assumes that the mount points in the UsageContext are
             * valid. This is different from the behavior of
             * getHostInitiatorIds() which checks the existence of the HIs in
             * the UsageContext. This "should" not be a problem since both MI
             * and HI are checked for existence in validateUsageContext() which
             * is called when the setContext() API is called to setup the VASA
             * Session. However, the behavior of getMountInfo and
             * getHostInitiatorsIds should be the same. Either both should check
             * or neither should check.
             */
            return ListUtil.list2ArrayMI(getUsageContext().getMountPoint());
        } catch (StorageFault sf) {
            // thrown by getUsageContext()
            // thrown by getMountPoint()
            LOGGER.error("getMountInfo StorageFault.");
            throw sf;
        } catch (InvalidSession is) {
            // thrown by getUsageContext()
            LOGGER.error("getMountInfo InvalidSession.");
            throw is;
        } catch (Exception e) {
            LOGGER.error(this.getClass().getName() + "getMountInfo failed: ", e);
            throw FaultUtil.storageFault("getMountInfo unexpected exception. Convert to StorageFault.", e);
        }
    }

    /**
     * 方法 ： queryCatalog
     *
     * @return MessageCatalog[] 返回结果
     * @throws StorageFault   异常：StorageFault
     * @throws InvalidSession 异常：InvalidSession
     */
    public List<MessageCatalog> queryCatalog() throws StorageFault, InvalidSession {
        // verify valid SSL and VASA Sessions.
        sslUtil.checkHttpRequest(false, false);

        List<MessageCatalog> mcList = new ArrayList<MessageCatalog>(0);

        String[] lauguages = VASAUtil.ISM_VASA_LANGUAGE.split(VASA_SPLITE);
        String[] eventDescFileNames = VASAUtil.ISM_VASA_EVENT_DESC_FILE_NAME.split(VASA_SPLITE);

        MessageCatalog localMessageCatalog = null;
        String str = null;
        Calendar localCalendar = null;

        int languagesCount = lauguages.length;
        int eventFileCount = eventDescFileNames.length;
        String language = null;
        String eventFileName = null;
        for (int i = 0; i < languagesCount; i++) {
            language = lauguages[i];
            for (int j = 0; j < eventFileCount; j++) {
                eventFileName = eventDescFileNames[j];
                localMessageCatalog = new MessageCatalog();
                localMessageCatalog.setLocale(language);
                localMessageCatalog.setCatalogVersion(PROVIDER_VERSION);
                localMessageCatalog.setModuleName(VASAUtil.ISMPROVIDER_NAME);
                if (eventFileName.equals("event.vmsg")) {
                    str = StorageCatalogEnum.EVENT.value();
                } else if (eventFileName.equals("fault.vmsg")) {
                    str = StorageCatalogEnum.FAULT.value();
                } else if (eventFileName.equals("alarm.vmsg")) {
                    str = StorageCatalogEnum.ALARM.value();
                }

                localMessageCatalog.setCatalogName(str);
                localMessageCatalog.setCatalogUri('/' + "esdk" + "/catalog/" + language + '/' + eventFileName);
                // localMessageCatalog.setCatalogUri("http://129.61.70.12/"
                // + VASAUtil.ISM_VASA_PROVIDER_SERVER_NAME + "/catalog/"
                // + language + "/" + eventFileName);
                localCalendar = Calendar.getInstance();
                // localCalendar.setTimeInMillis(new File(
                // "/ism-vasa-provider/catalog" + language + "/"
                // + eventFileName).lastModified());
                localCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
                localMessageCatalog.setLastModified(localCalendar);
                mcList.add(localMessageCatalog);
                LOGGER.info("Got a catalog,locale=" + localMessageCatalog.getLocale() + ",catalogversion="
                        + localMessageCatalog.getCatalogVersion() + ",catalogname="
                        + localMessageCatalog.getCatalogName() + ",uri=" + localMessageCatalog.getCatalogUri()
                        + ",moduleName=" + localMessageCatalog.getModuleName());
            }
        }

        // return (MessageCatalog[]) mcList.toArray(new
        // MessageCatalog[mcList.size()]);
        return mcList;
    }

}
