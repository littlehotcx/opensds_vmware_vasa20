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

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opensds.vasa.common.DeviceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.domain.model.bean.DArrayInfo;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.RedisUtil;
import org.opensds.vasa.vasa.util.VASAResponseCode;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.array.add.EthPortResponseData;
import org.opensds.vasa.vasa20.device.array.add.StorageDevicerRestUtils;
import org.opensds.vasa.vasa20.device.array.add.SystemResponseData;

import com.google.gson.Gson;

import org.opensds.vasa.base.bean.terminal.ResBean;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.common.utils.VasaRedisKey;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.StorageInfoResponseHeader;
import org.opensds.vasa.vasa.db.model.VvolType;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.rest.bean.AddDeviceBean;
import org.opensds.vasa.vasa.rest.bean.AddStorageArrayResult;
import org.opensds.vasa.vasa.rest.bean.CountResponseBean;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.rest.bean.ModifyDeviceBean;
import org.opensds.vasa.vasa.rest.bean.QueryDBResponse;
import org.opensds.vasa.vasa.rest.bean.VasaStorageResult;

@Path("vasa/StorageManager")
public class StorageManagerResource {

    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private StoragePoolService storagePoolService = ApplicationContextUtil.getBean("storagePoolService");
    private Logger LOGGER = LogManager.getLogger(StorageManagerResource.class);
    private DataUtil dataManager = DataUtil.getInstance();
    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();
    private DiscoverService discoverService = DiscoverService.getInstance();
    private static String MODEL_PREX = "OceanStor ";

    // private FusionStorageInfoImpl fusionStorageInfoImpl = FusionStorageInfoImpl.getInstance();

    @GET
    @Path("count")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CountResponseBean getStorageArrayCount() {
        LOGGER.info("In getStorageArrayCount function.");
        CountResponseBean response = new CountResponseBean();
        try {
            long count = storageManagerService.getStorageArrayCount();
            response.setCount(count);
            response.setResultCode(0L);
            response.setResultDescription("getStorageArrayCount successfully.");
        } catch (Exception e) {
            response.setResultCode(1L);
            response.setResultDescription("getStorageArrayCount fail.");
        }
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AddStorageArrayResult addStorageArray(AddDeviceBean deviceBean) {
        LOGGER.info("In addData function, the request is " + deviceBean.toString());
        AddStorageArrayResult response = new AddStorageArrayResult();
        if (null == deviceBean.getUsername() || null == deviceBean.getPassword() || null == deviceBean.getIp()
                || 0 == deviceBean.getPort()) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }
        try {

            List<StorageInfoResponseHeader> storageInfos = new ArrayList<StorageInfoResponseHeader>();
            StorageInfoResponseHeader storageInfoResponseHeader = new StorageInfoResponseHeader();
            // 登入设备
            LOGGER.info("Add device-----------------------log in");
            List<SystemResponseData> systemResponseDatas = new ArrayList<>();
            List<EthPortResponseData> ethPortResponseDatas = new ArrayList<>();
            List<String> utcTime = new ArrayList<>();

            //login by deviceType

            VasaStorageResult vasaStorageResult = loginAndInitData(deviceBean.getUsername(), deviceBean.getPassword(),
                    deviceBean.getIp(), deviceBean.getPort(), systemResponseDatas, ethPortResponseDatas, utcTime);
            response.setResultCode(vasaStorageResult.getResultCode());
            response.setResultDescription(vasaStorageResult.getResultDescription());
            if (null != response.getResultCode() && !"0".equals(response.getResultCode())) {
                return response;
            }

            SystemResponseData systemResponseData = systemResponseDatas.get(0);
            boolean isIPV6 = true;
            if (isIPV4(deviceBean.getIp())) {
                isIPV6 = false;
            }
            String ips_string = getIps(ethPortResponseDatas, isIPV6);
            LOGGER.info("ips: " + ips_string);

            // id应该是SN
            String sn = systemResponseData.getID();
            //判断整列存在否，并更新阵列信息
            StorageInfo storageBySn = storageManagerService.getStorageBySn(sn);
            if (null != storageBySn) {
                //不为null，即阵列存在
                storageInfoResponseHeader.setId(storageBySn.getId());
                storageInfoResponseHeader.setName(storageBySn.getStoragename());
                storageInfoResponseHeader.setVendor(storageBySn.getVendor());
                storageInfoResponseHeader.setSn(storageBySn.getSn());
                storageInfoResponseHeader.setModel(storageBySn.getModel());
                storageInfoResponseHeader.setProduct_version(storageBySn.getProductversion());
                storageInfoResponseHeader.setIp(storageBySn.getIp());
                storageInfoResponseHeader.setIps(storageBySn.getIps());
                storageInfoResponseHeader.setConn_ip(storageBySn.getIp());
                storageInfoResponseHeader.setPort(storageBySn.getPort());
                storageInfoResponseHeader.setDevice_status(storageBySn.getDevicestatus());
                storageInfoResponseHeader.setUsername(storageBySn.getUsername());
                storageInfoResponseHeader.setRegistered(storageBySn.getRegistered());
                storageInfoResponseHeader.setSupported_vasa_profiles(storageBySn.getVvolsupportProfile());
                storageInfoResponseHeader.setDeviceType(storageBySn.getDeviceType());
                storageInfos.add(storageInfoResponseHeader);
                response.setStorageInfos(storageInfos);
                if (storageBySn.getDeleted() == 0) {
                    // 需要判断是否是已经接入的阵列
                    LOGGER.error("the storage array is already exist, the arrayid = " + storageBySn.getId());
                    response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_ALREADY_EXIST_ERROR);
                    response.setResultDescription(
                            VASAResponseCode.storageManagerService.STORAGE_ARRAY_ALREADY_EXIST_ERROR_DESCRIPTION);
                    return response;
                }
                VasaStorageResult result = new VasaStorageResult();
                result = updateExistArray(deviceBean.getUsername(), deviceBean.getPassword(), deviceBean.getIp(),
                        deviceBean.getPort(), getProductMode(systemResponseData.getPRODUCTMODE()), storageBySn.getDeviceType(), result,
                        systemResponseData, ips_string, storageBySn, utcTime.get(0));
                // 更新内存阵列状态
                discoverService.addArrayInfoMap(storageBySn.getId());
                addStorageArrayIsLockInfo(sn, false);
                discoverService.addFlowControlDevice(sn, storageBySn.getId());
                response.setResultCode(result.getResultCode());
                response.setResultDescription(result.getResultDescription());
                return response;
            }

            boolean ifSupportvvol = checkIfSupportvvol(systemResponseData);
            if (!ifSupportvvol) {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_SUPPORT_VVOL);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_SUPPORT_VVOL_DESCRIPTION);
                return response;
            }

            String arrayId = UUID.randomUUID().toString();
            StorageInfo createStorageInfo = createStorageInfo(arrayId, deviceBean.getUsername(),
                    deviceBean.getPassword(), deviceBean.getIp(), deviceBean.getPort(), systemResponseData, ips_string,
                    utcTime.get(0));

            //V5R7C60新增安可型号，优先使用新字段productModeString设置硬件类型，没有该字段则使用枚举值匹配
            if (systemResponseData.getProductModeString() != null) {
                createStorageInfo.setModel("OceanStor " + systemResponseData.getProductModeString());
            } else {
                createStorageInfo.setModel(DeviceTypeMapper.getProfuctModeName(systemResponseData.getPRODUCTMODE()));
            }

            createStorageInfo.setSupportvvol("supportvvol");
            createStorageInfo.setRegistered("registered");
            createStorageInfo.setCreatetime(new Timestamp(System.currentTimeMillis()));
            createStorageInfo.setDeviceType(0);
            LOGGER.info("storageInfo..." + createStorageInfo.toString());

            storageInfoResponseHeader.setId(createStorageInfo.getId());
            storageInfoResponseHeader.setName(createStorageInfo.getStoragename());
            storageInfoResponseHeader.setVendor(createStorageInfo.getVendor());
            storageInfoResponseHeader.setSn(createStorageInfo.getSn());
            storageInfoResponseHeader.setModel(createStorageInfo.getModel());
            storageInfoResponseHeader.setProduct_version(createStorageInfo.getProductversion());
            storageInfoResponseHeader.setIp(createStorageInfo.getIp());
            storageInfoResponseHeader.setIps(createStorageInfo.getIps());
            storageInfoResponseHeader.setConn_ip(createStorageInfo.getIp());
            storageInfoResponseHeader.setPort(createStorageInfo.getPort());
            storageInfoResponseHeader.setDevice_status(createStorageInfo.getDevicestatus());
            storageInfoResponseHeader.setUsername(createStorageInfo.getUsername());
            storageInfoResponseHeader.setRegistered(createStorageInfo.getRegistered());
            storageInfoResponseHeader.setSupported_vasa_profiles(createStorageInfo.getVvolsupportProfile());
            storageInfoResponseHeader.setDeviceType(createStorageInfo.getDeviceType());
            storageInfos.add(storageInfoResponseHeader);
            storageManagerService.setInfo(createStorageInfo);
            RedisUtil.incr(VasaRedisKey.device_refresh_verion_key);

            addStorageArrayIsLockInfo(sn, false);

            discoverService.addFlowControlDevice(sn, arrayId);
            updateEvent();

            LOGGER.info("name:" + storageInfoResponseHeader.getName() + "support file:"
                    + storageInfoResponseHeader.getSupported_vasa_profiles() + "productversion"
                    + storageInfoResponseHeader.getProduct_version());
            response.setStorageInfos(storageInfos);
            // 更新内存阵列状态
            discoverService.addArrayInfoMap(arrayId);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("storageManager error: " + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    private VasaStorageResult updateExistFSArray(AddDeviceBean deviceBean, StorageInfo fsStorageBySn
            , VasaStorageResult response, String utcTime, SystemResponseData systemResponseData) throws UnsupportedEncodingException {
        if (fsStorageBySn.getDeleted() == 1) {
            StorageInfo storageInfo = creatFSDBModel(fsStorageBySn.getId(), utcTime, systemResponseData, deviceBean);
            String model = systemResponseData.getPRODUCTMODE();
            storageInfo.setModel(model);
            storageInfo.setDeviceType(fsStorageBySn.getDeviceType());
            storageInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));

            LOGGER.info("updateExistFSArray--userName: " + deviceBean.getUsername() + ", ip: " + deviceBean.getIp() + ", deviceType: " + fsStorageBySn.getDeviceType());
            storageManagerService.updateExistDeviceInfo(storageInfo);
            discoverService.addFlowControlDevice(fsStorageBySn.getSn(), fsStorageBySn.getId());
            updateEvent();
            LOGGER.info("refresh devices");
            RedisUtil.incr(VasaRedisKey.device_refresh_verion_key);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            return response;
        } else {
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
            return response;
        }
    }

    private StorageInfo creatFSDBModel(String arrayId, String utcTime, SystemResponseData systemResponseData,
                                       AddDeviceBean deviceBean) throws UnsupportedEncodingException {
        String name = systemResponseData.getNAME();
        String sn = systemResponseData.getID();
        String productversion = systemResponseData.getPRODUCTVERSION();
        String vendor = systemResponseData.getVendor();
        String vvolsupportProfile = systemResponseData.getVASA_SUPPORT_PROFILE();

        StorageInfo existStorageInfo = new StorageInfo();
        existStorageInfo.setId(arrayId);
        existStorageInfo.setIp(deviceBean.getIp());
        existStorageInfo.setIps(deviceBean.getIp());//ips_string.replace(" ", "")
        existStorageInfo.setPort(deviceBean.getPort());
        existStorageInfo.setUsername(deviceBean.getUsername());
        existStorageInfo.setPassword(
                Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((deviceBean.getUsername() + deviceBean.getPassword()).getBytes("UTF-8"))));
        existStorageInfo.setDevicestatus("ONLINE");
        existStorageInfo.setStoragename(name);
        existStorageInfo.setProductversion(productversion);
        existStorageInfo.setVvolsupportProfile(vvolsupportProfile);
        existStorageInfo.setVendor(vendor);
        existStorageInfo.setArrayUTCTime(utcTime);
        existStorageInfo.setSn(sn);
        existStorageInfo.setDeleted(0);
        return existStorageInfo;
    }

    private VasaStorageResult loginAndInitData(String userName, String password, String ip, int port,
                                               List<SystemResponseData> systemResponseDatas, List<EthPortResponseData> ethPortResponseDatas,
                                               List<String> utcTimes) {
        StorageDevicerRestUtils instance = new StorageDevicerRestUtils();
        ResBean userAuthRes = instance.init_conn(userName, password, ip, port);
        VasaStorageResult response = new VasaStorageResult();
        if (0 != userAuthRes.getErrorCode()) {
            LOGGER.error("init connection fail. errMsg=" + userAuthRes.getDescription());
            if (userAuthRes.getErrorCode() == Long
                    .valueOf(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SSL_ERROR).longValue()) {
                response.setResultCode(String.valueOf(userAuthRes.getErrorCode()));
                response.setResultDescription(userAuthRes.getDescription());
            } else if (userAuthRes.getErrorCode() == 1077949061L) {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_USER_PWD_ERR);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_USER_PWD_ERR_DESCRIPTION);
            } else {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_LOGIN);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_LOGIN_DESCRIPTION);
            }
            return response;
        }

        // 登入成功后，获取system信息
        ResBean sysInfo = instance.getSysInfo();
        if (0 != sysInfo.getErrorCode()) {
            LOGGER.error("getSystemInfo fail. errMsg=" + sysInfo.getDescription());
            response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_SYSTEMINFO_ERROE);
            response.setResultDescription(
                    VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_SYSTEMINFO_ERROE_DESCRIPTION);
            return response;
        }

        // 获取IP端口信息
        ResBean ethportInfo = instance.getEthportInfo();
        if (0 != ethportInfo.getErrorCode()) {
            LOGGER.error("get eth_port fail. errMsg=" + ethportInfo.getDescription());
            response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_ETHPORT_ERROR);
            response.setResultDescription(
                    VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_ETHPORT_ERROR_DESCRIPTION);
            return response;
        }

        // 获取阵列当前时间
        if (null != utcTimes) {
            ResBean utcTime = instance.getUTCTime();
            if (0 != utcTime.getErrorCode()) {
                LOGGER.error("get utcTime fail. errMsg=" + utcTime.getDescription());
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_TIME_ERROR);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_GET_TIME_ERROR_DESCRIPTION);
                return response;
            }
            utcTimes.add((String) utcTime.getResData());
        }

        instance.logout();
        systemResponseDatas.add((SystemResponseData) sysInfo.getResData());
        List<EthPortResponseData> datas = (List<EthPortResponseData>) ethportInfo.getResData();
        ethPortResponseDatas.addAll(datas);
        LOGGER.info("login info. systemResponseData=" + systemResponseDatas + ",ethPortResponseDatas="
                + ethPortResponseDatas);
        return response;
    }

    @POST
    @Path("modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaStorageResult modifyData(ModifyDeviceBean modifyDevice) {
        VasaStorageResult response = new VasaStorageResult();
        String username = modifyDevice.getUsername();
        String ip = modifyDevice.getIp();
        String port = modifyDevice.getPort();
        String password = modifyDevice.getPassword();
        String arrayId = modifyDevice.getArrayId();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(ip)
                || StringUtils.isEmpty(port) || StringUtils.isEmpty(arrayId)) {
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        try {
            // 登入设备
            LOGGER.info("Add device-----------------------log in");
            List<SystemResponseData> systemResponseDatas = new ArrayList<>();
            List<EthPortResponseData> ethPortResponseDatas = new ArrayList<>();
            List<String> utcTime = new ArrayList<>();

            response = loginAndInitData(username, password, ip, Integer.valueOf(port), systemResponseDatas,
                    ethPortResponseDatas, null);
            if (null != response.getResultCode() && !"0".equals(response.getResultCode())) {
                return response;
            }

            SystemResponseData systemResponseData = systemResponseDatas.get(0);

            boolean isIPV6 = true;
            if (isIPV4(modifyDevice.getIp())) {
                isIPV6 = false;
            }
            String ips_string = getIps(ethPortResponseDatas, isIPV6);

            StorageInfo currentArrayInfo = storageManagerService.queryInfoByArrayId(arrayId);
            if (!currentArrayInfo.getSn().equals(systemResponseData.getID())) {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SN_MATCH_ERROR);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_SN_MATCH_ERROR_DESCRIPTION);
                return response;
            }

            boolean ifSupportvvol = checkIfSupportvvol(systemResponseData);
            if (!ifSupportvvol) {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_SUPPORT_VVOL);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_NOT_SUPPORT_VVOL_DESCRIPTION);
                return response;
            }

            StorageInfo storageInfo = createStorageInfo(arrayId, username, password, ip, Integer.valueOf(port),
                    systemResponseData, ips_string, null);
            storageInfo.setRegistered("registered");
            storageInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));
            LOGGER.info(" modify storageInfo..." + storageInfo.toString());
            storageManagerService.modifyInfo(storageInfo);
            RedisUtil.incr(VasaRedisKey.device_refresh_verion_key);
            addStorageArrayIsLockInfo(systemResponseData.getID(), false);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            return response;

        } catch (UnsupportedEncodingException e) {
            LOGGER.error("modify Storage Device fail:" + e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        } catch (Exception e) {
            LOGGER.error("modify Storage Device fail:" + e, e);
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    @PUT
    @Path("sync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaStorageResult syncData(String arrayId) {
        LOGGER.info("In syncData function.");
        VasaStorageResult response = new VasaStorageResult();
        try {
            arrayId = (new Gson()).fromJson(arrayId, String.class);
            LOGGER.info("syncData ,arrayId = " + arrayId);
            StorageInfo queryInfo = storageManagerService.queryInfoByArrayId(arrayId);
            LOGGER.info("sync device start to get password *******" + ",username " + queryInfo.getUsername());
            String decodepassword = ArrayPwdAES128Util.decryptPwd(queryInfo.getUsername(), queryInfo.getPassword());
            String sn = queryInfo.getSn();
            Map<String, DArrayIsLock> arrayIsLock = dataManager.getArrayIsLock();
            if (arrayIsLock.get(sn).getIsLock()) {
                LOGGER.error("The arrayId = " + arrayId + " is lock, do not login again.");
                updateStorageArrayStatus(arrayId, "OFFLINE");
            } else {
                LOGGER.info("sync device start to get password *******" + ",username " + queryInfo.getUsername());
                // 登入设备
                List<SystemResponseData> systemResponseDatas = new ArrayList<>();
                List<EthPortResponseData> ethPortResponseDatas = new ArrayList<>();
            }
        } catch (Exception e) {
            LOGGER.error("Sync Storage Device faile:" + e, e);
            updateStorageArrayStatus(arrayId, "OFFLINE");
        }
        response.setResultCode(VASAResponseCode.common.SUCCESS);
        response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        return response;
    }

    /*
     * fusionstorage暂时拿不到IPV4、IPV6重载方法进行Fs的sysdata
     */
    private void syncStorageInfo(StorageInfo queryInfo, SystemResponseData systemResponseData) {
        String name = systemResponseData.getNAME();
        String productversion = systemResponseData.getPRODUCTVERSION();
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setId(queryInfo.getId());
        storageInfo.setIps(queryInfo.getIps());
        storageInfo.setStoragename(name);
        storageInfo.setProductversion(productversion);
        storageInfo.setSn(queryInfo.getSn());
        storageInfo.setDevicestatus("ONLINE");
        storageInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));
        storageManagerService.syncInfo(storageInfo);
    }

    private void syncStorageInfo(String arrayId, String sn, SystemResponseData systemResponseData,
                                 List<EthPortResponseData> ethPortResponseDatas, boolean isIPV6) {
        String name = systemResponseData.getNAME();
        String productversion = systemResponseData.getPRODUCTVERSION();
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setId(arrayId);
        storageInfo.setIps(getIps(ethPortResponseDatas, isIPV6));
        storageInfo.setStoragename(name);
        storageInfo.setProductversion(productversion);
        storageInfo.setSn(sn);
        storageInfo.setDevicestatus("ONLINE");
        storageInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));
        storageManagerService.syncInfo(storageInfo);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryDBResponse queryStorageArray(@QueryParam("pageIndex") String pageIndex,
                                             @QueryParam("pageSize") String pageSize) {
        LOGGER.info("In queryStorageArray function, the request parameter pageIndex=" + pageIndex + " pageSize="
                + pageSize);
        QueryDBResponse response = new QueryDBResponse();
        if (null == pageIndex || null == pageSize) {
            response.setResultCode("1");
            response.setResultDescription("Invalid parameters.");
            return response;
        }
        try {
            long count = storageManagerService.getStorageArrayCount();
            response.setCount((int) count);

            List<StorageInfo> storageArrayList = storageManagerService.queryStorageArray(pageSize, pageIndex);
            String installer = dataManager.getVasaInfoMapByKey("InstallType");
            if (!installer.equalsIgnoreCase("Staas")) {
                Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
                for (StorageInfo storageInfo : storageArrayList) {
                    for (Map.Entry<String, DArrayInfo> entry : arrayInfoMap.entrySet()) {
                        if (storageInfo.getId().equals(entry.getKey())) {
                            DArrayInfo dArrayInfo = entry.getValue();
                            storageInfo.setPriority(dArrayInfo.getPriority());
                            storageInfo.setDevicestatus(dArrayInfo.getStatus());
                        }
                    }
                }
            }
            response.setResultCode("0");
            response.setResultDescription("Query storageArray successfully.");
            response.setAllStorageInfo(storageArrayList);
        } catch (Exception e) {
            LOGGER.error("Query storageArray fail : ", e);
            response.setResultCode("1");
            response.setResultDescription("Invalid parameters.");
        }
        return response;
    }

    @GET
    @Path("priority")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaStorageResult updateStorageArrayPriority(@QueryParam("arrayid") String arrayId,
                                                        @QueryParam("priority") String priority) {
        LOGGER.info("In updateStorageArrayPriority function, the arrayid = " + arrayId + " the priority = " + priority);
        VasaStorageResult response = new VasaStorageResult();
        try {
            if (StringUtils.isEmpty(arrayId) || StringUtils.isEmpty(priority)) {
                LOGGER.error("Invalid parameters, the arrayId = " + arrayId + " the priority=" + priority);
                response.setResultCode("1");
                response.setResultDescription("Invalid parameters");
                return response;
            }
            List<String> arrayIds = new ArrayList<String>();
            arrayIds.add(arrayId);
            discoverService.updateArrayInfoMapDevicePriority(arrayId, Integer.parseInt(priority));
            // discoverService.addStorageArrayUpdateEvent(arrayIds);
            discoverService.addStorageArrayUpdateEvent(arrayIds);
            response.setResultCode("0");
            response.setResultDescription("update storage array priority success.");

        } catch (Exception e) {
            LOGGER.error("updateStorageArrayPriority fail.");
            response.setResultCode("1");
            response.setResultDescription("updateStorageArrayPriority fail.");
        }
        return response;
    }

    @GET
    @Path("status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaStorageResult getStorageArrayStatus(@QueryParam("arrayid") String arrayId,
                                                   @QueryParam("status") String status) {
        LOGGER.info("In updateStorageArrayStatus function, the arrayid = " + arrayId + " the status = " + status);
        VasaStorageResult response = new VasaStorageResult();
        try {

            StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(arrayId);
            if (storageInfo == null || StringUtils.isEmpty(storageInfo.getId())) {
                LOGGER.error("the arrayid is not exist. arrayid = " + arrayId);
                response.setResultCode("1");
                response.setResultDescription("Invalid parameters, the arrayId is not exist.");
                return response;
            }

            if (StringUtils.isEmpty(arrayId) || StringUtils.isEmpty(status)) {
                LOGGER.error("Invalid parameters, the arrayId = " + arrayId + " status = " + status);
                response.setResultCode("1");
                response.setResultDescription("Invalid parameters");
                return response;
            }

            if (!status.equalsIgnoreCase("online") && !status.equalsIgnoreCase("offline")) {
                LOGGER.error("Invalid parameters,the status = " + status);
                response.setResultCode("1");
                response.setResultDescription("Invalid parameters");
                return response;
            }

            List<String> arrayIds = new ArrayList<String>();
            arrayIds.add(arrayId);
            status = status.toUpperCase();
            LOGGER.info("Update storage Array arrayId = " + arrayId + " set status = " + status);
            discoverService.updateArrayInfoMapDeviceStatus(arrayId, status);
            if (status.equalsIgnoreCase("online")) {
                LOGGER.info("Start generate storage status change alarm : Green");
                discoverService.addStorageArrayGreenAlarm(arrayIds);
                for (String arrayid : arrayIds) {
                    discoverService.updateArrayInfoMapDevicePriority(arrayid, 100);
                }
                discoverService.addStorageArrayUpdateEvent(arrayIds);

            } else if (status.equalsIgnoreCase("offline")) {
                LOGGER.info("Start generate storage status change alarm : Red");
                discoverService.addStorageArrayRedAlarm(arrayIds);
                for (String arrayid : arrayIds) {
                    discoverService.updateArrayInfoMapDevicePriority(arrayid, 10);
                }
                discoverService.addStorageArrayUpdateEvent(arrayIds);
            }

            response.setResultCode("0");
            response.setResultDescription("update storage status success");
            // discoverService.addStorageArrayUpdateEvent(arrayIds);
            // 状态变化应该上报告警而不是事件
        } catch (Exception e) {
            LOGGER.error("updateStorageArrayPriority fail.");
            response.setResultCode("1");
            response.setResultDescription("updateStorageArrayPriority fail.");
        }
        return response;
    }

    @GET
    @Path("all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryDBResponse queryData() {
        LOGGER.info("In queryStorageDevice function.");
        QueryDBResponse response = new QueryDBResponse();
        try {
            LOGGER.info("Query data start!");
            List<StorageInfo> devices = new ArrayList<StorageInfo>();

            devices = storageManagerService.queryInfo();
            // 除掉deleted = true的
            List<StorageInfo> realdevices = new ArrayList<StorageInfo>();

            if (devices != null) {
                LOGGER.info("Device count is: " + devices.size());
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getDeleted() == 0) {
                        String decodepassword = ArrayPwdAES128Util.decryptPwd(devices.get(i).getUsername(),
                                devices.get(i).getPassword());
                        devices.get(i).setPassword(decodepassword);
                        realdevices.add(devices.get(i));
                        LOGGER.info("query realdevice:" + realdevices.toString());
                    }
                }
            }

            // 判断是否是Staas场景，如果不是Staas场景，则需要取出内存中的阵列的状态值
            String installer = dataManager.getVasaInfoMapByKey("InstallType");
            if (!installer.equalsIgnoreCase("Staas")) {
                for (StorageInfo storageInfo : realdevices) {
                    Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
                    for (Map.Entry<String, DArrayInfo> entry : arrayInfoMap.entrySet()) {
                        if (storageInfo.getId().equals(entry.getKey())) {
                            DArrayInfo dArrayInfo = entry.getValue();
                            storageInfo.setPriority(dArrayInfo.getPriority());
                            storageInfo.setDevicestatus(dArrayInfo.getStatus());
                        }
                    }
                }
            }

            if (realdevices != null) {
                response.setCount(realdevices.size());
                response.setAllStorageInfo(realdevices);
                LOGGER.info("query devices end" + response.getAllStorageInfo().toString());
            }

            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
            LOGGER.error("Query devices DB error: " + e, e);
        }
        return response;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VasaStorageResult deleteData(@QueryParam("deleteId") String deleteId) {
        LOGGER.info("In deleteData function,the request parameters deleteId=" + deleteId);
        VasaStorageResult response = new VasaStorageResult();
        if (StringUtils.isEmpty(deleteId)) {
            LOGGER.error("deleteId is null, invalid parameter.");
            response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
            response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
            return response;
        }

        // 参数校验，校验arrayId在不在库里面，删除已经
        try {
            StorageInfo storageInfo = storageManagerService.queryInfoByArrayId(deleteId);
            if (null == storageInfo || StringUtils.isEmpty(storageInfo.getId())) {
                LOGGER.error("The arrayid = " + deleteId + " is not exist.");
                response.setResultCode(VASAResponseCode.common.INVALID_PARAMETER);
                response.setResultDescription(VASAResponseCode.common.INVALID_PARAMETER_DESCRIPTION);
                return response;
            }
            if (storageInfo.getDeleted() == 1) {
                response.setResultCode(VASAResponseCode.common.SUCCESS);
                response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
                return response;
            }

            // 校验阵列下的存储池是否映射到了contaienr
            List<NStoragePool> allBindStoragePoolByArrayId = storagePoolService
                    .getAllBindStoragePoolByArrayId(deleteId);
            if (null != allBindStoragePoolByArrayId && allBindStoragePoolByArrayId.size() > 0) {
                response.setResultCode(VASAResponseCode.storageManagerService.STORAGE_ARRAY_BEING_USED);
                response.setResultDescription(
                        VASAResponseCode.storageManagerService.STORAGE_ARRAY_BEING_USED_DESCRIPTION);
                LOGGER.info("delete array err. the array is being used. arrayId=" + deleteId);
                return response;
            }

            // 后面改下
            // StorageInfo storageInfo = new StorageInfo();
            storageInfo.setDeletetime(new Timestamp(System.currentTimeMillis()));
            // storageInfo.setId(deleteId);
            storageInfo.setDeleted(1);
            storageInfo.setDevicestatus("OFFLINE");
            LOGGER.info("delete storageInfo..." + storageInfo.toString());
            storageManagerService.deleteInfo(storageInfo);
            // 刷新DataUtil中的阵列信息
            discoverService.removeArrayInfoMap(deleteId);
            RedisUtil.incr(VasaRedisKey.device_refresh_verion_key);
            // deviceManager.refreshDevices();
            // 上报事件
            updateEvent();
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
        } catch (Exception e) {
            LOGGER.error("Query devices DB error:" + e, e);
            // TODO: handle exception
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC + e);
        }
        return response;
    }

    public void updateEvent() {
        // 上报事件
        Set<String> cachedArrayIds = dataManager.getArrayId();
        discoverServiceImpl.updateArrayIds();
        Set<String> arrayIds = dataManager.getArrayId();

        // 产生阵列添加删除事件,返回给所有订阅了Config事件的vcenter server
        List<String> ucUUIDs = VASAUtil.getConfigEventUcUUIDs();
        DiscoverService.getInstance().appendConfigStorageArrayEvent(ucUUIDs, VASAUtil.convertSet2List(cachedArrayIds),
                VASAUtil.convertSet2List(arrayIds));
    }

    private VasaStorageResult updateExistArray(String userName, String password, String ip, int port, String
            model, int deviceType,
                                               VasaStorageResult response, SystemResponseData systemResponseData, String ips_string,
                                               StorageInfo storageBySn, String utcTime) throws UnsupportedEncodingException {
        LOGGER.info("updateExistArray  userName" + userName + ", password: " + password + ", ip: " + ip + ", deviceType: " + deviceType);
        if ((storageBySn.getDeleted() == 1)) {
            StorageInfo existStorageInfo = createStorageInfo(storageBySn.getId(), userName, password, ip, port,
                    systemResponseData, ips_string, utcTime);
            existStorageInfo.setModel(model);
            existStorageInfo.setDeviceType(deviceType);
            existStorageInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));
            storageManagerService.updateExistDeviceInfo(existStorageInfo);
            discoverService.addFlowControlDevice(storageBySn.getSn(), storageBySn.getId());
            updateEvent();
            LOGGER.info("refresh devices");
            RedisUtil.incr(VasaRedisKey.device_refresh_verion_key);
            response.setResultCode(VASAResponseCode.common.SUCCESS);
            response.setResultDescription(VASAResponseCode.common.SUCCESS_DESC);
            return response;
        } else {
            response.setResultCode(VASAResponseCode.common.ERROR);
            response.setResultDescription(VASAResponseCode.common.ERROR_DESC);
            return response;
        }
    }

    private StorageInfo createStorageInfo(String arrayId, String userName, String password, String ip, int port,
                                          SystemResponseData systemResponseData, String ips_string, String utcTime)
            throws UnsupportedEncodingException {
        StorageInfo existStorageInfo = new StorageInfo();
        String name = systemResponseData.getNAME();
        String sn = systemResponseData.getID();
        String productversion = systemResponseData.getPRODUCTVERSION();
        String vendor = systemResponseData.getVASA_ALTERNATE_NAME();
        String vvolsupportProfile = systemResponseData.getVASA_SUPPORT_PROFILE();
        existStorageInfo.setId(arrayId);
        existStorageInfo.setIp(ip);
        existStorageInfo.setIps(ips_string.replace(" ", ""));
        existStorageInfo.setPort(port);
        existStorageInfo.setUsername(userName);
        existStorageInfo.setPassword(
                Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((userName + password).getBytes("UTF-8"))));
        existStorageInfo.setDevicestatus("ONLINE");
        existStorageInfo.setStoragename(name);
        existStorageInfo.setProductversion(productversion);
        existStorageInfo.setVvolsupportProfile(vvolsupportProfile);
        existStorageInfo.setVendor(vendor);
        existStorageInfo.setArrayUTCTime(utcTime);
        existStorageInfo.setSn(sn);
        existStorageInfo.setDeleted(0);
        return existStorageInfo;
    }

    private String getProductMode(String productmode) {
        String stringproductmode = DeviceTypeMapper.convertDeviceType(productmode);
        return MODEL_PREX + stringproductmode;
    }

    private boolean checkIfSupportvvol(SystemResponseData systemResponseData) {
        // TODO Auto-generated method stub
        String productversion = systemResponseData.getPRODUCTVERSION();
        String productmode = systemResponseData.getPRODUCTMODE();
        String patchversion = systemResponseData.getPatchVersion();
        LOGGER.info(
                "productmode:" + productmode + " productversion: " + productversion + " patchversion: " + patchversion);
        List<VvolType> vvolTypes = new ArrayList<VvolType>();
        vvolTypes = storageManagerService.queryVvolType();
        String version = productversion + patchversion;
        LOGGER.info("Get vvolTypes start here::" + vvolTypes.toString());
        for (int i = 0; i < vvolTypes.size(); i++) {
            String stringproductmode = DeviceTypeMapper.convertDeviceType(productmode);
            LOGGER.info("stringproductmode:" + stringproductmode + ",version=" + version);


            if (productmode.contains("V500R007C60")) {
                LOGGER.info("current device is V5R7C60 kunpeng");
                return true;
            }

            if (vvolTypes.get(i).getDeviceType().indexOf(stringproductmode) != -1) {
                String[] deviceVersion = vvolTypes.get(i).getDeviceVersion().split("\\,");
                for (int k = 0; k < deviceVersion.length; k++) {
                    // 先去匹配是否有*
                    /*
                     * if(deviceVersion[k].contains("*")){
                     * if(deviceVersion[k].substring(0,
                     * deviceVersion[k].length()-1).equals(productversion)){
                     * LOGGER.info("support vvolType"); return true; } } else if
                     * (deviceVersion[k].trim().equals(version.trim())) {
                     * LOGGER.info("support vvolType"); return true; }
                     */
                    if (version.matches(deviceVersion[k])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getIps(List<EthPortResponseData> ethPortResponseDatas, boolean isIpV6) {
        int IP_count = ethPortResponseDatas.size();
        List<String> ips = new ArrayList<String>();
        for (int i = 0; i < IP_count; i++) {
            if (ethPortResponseDatas.get(i).getLOGICTYPE().equals("2")) {
                String ip_control = ethPortResponseDatas.get(i).getIPV4ADDR();
                if ((ip_control != null) && (ip_control.isEmpty() == false)) {
                    ips.add(ip_control);
                }
                //如果为登录ip为ipv6则加入所有ip
                if (isIpV6) {
                    String ip_control1 = ethPortResponseDatas.get(i).getIPV6ADDR();
                    if ((ip_control1 != null) && (ip_control1.isEmpty() == false)) {
                        ips.add(ip_control1);
                    }
                }
            }
        }
        String ips_string = ips.toString().substring(1, ips.toString().length() - 1).replace(" ", "");
        return ips_string;
    }

    private void addStorageArrayIsLockInfo(String devId, boolean isLock) {
        DArrayIsLock arrayislock = new DArrayIsLock();
        arrayislock.setDeviceId(devId);
        arrayislock.setIsLock(isLock);
        Map<String, DArrayIsLock> arrayIsLockInfo = dataManager.getArrayIsLock();
        arrayIsLockInfo.put(devId, arrayislock);
        dataManager.setArrayIsLock(arrayIsLockInfo);
    }

    private void updateStorageArrayStatus(String arrayId, String status) {
        StorageInfo updateInfo = new StorageInfo();
        updateInfo.setId(arrayId);
        updateInfo.setDevicestatus(status.toUpperCase());
        updateInfo.setUpdatetime(new Timestamp(System.currentTimeMillis()));
        storageManagerService.updateStatus(updateInfo);
        discoverService.updateArrayInfoMapDeviceStatus(arrayId, status);
    }

    private boolean isIPV4(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }

        // 判断IP格式和范围
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        return ipAddress;
    }
}
