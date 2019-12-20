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

package org.opensds.vasa.vasa20.device;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthReqBean;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.platform.nemgr.base.DeviceConnectionBase;
import org.opensds.platform.nemgr.base.MultiConnDeviceBase;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.rest.bean.QueryDBResponse;
import org.opensds.vasa.vasa.rest.resource.StorageManagerResource;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.util.DataUtil;

public class VASAConnection extends DeviceConnectionBase {
    private static final Logger LOGGER = LogManager.getLogger(VASAConnection.class);

    protected MultiConnDeviceBase device;

    protected VASADevice vasaDevice = null;

    protected ISDKProtocolAdapter protocolAdapter;

    private DataUtil dataUtil = DataUtil.getInstance();

    private DiscoverService discoverService = DiscoverService.getInstance();

    public ISDKProtocolAdapter getProtocolAdapter() {
        return protocolAdapter;
    }

    protected int keepAliveInteval;

    protected IDeviceManager deviceManager = ApplicationContextUtil
            .getBean("deviceManager");

    public VASAConnection(ISDKProtocolAdapter protocolAdapter,
                          MultiConnDeviceBase serviceProxy, String user, String pwd) {
        super(user, pwd);
        this.protocolAdapter = protocolAdapter;
        this.device = serviceProxy;
    }

    @Override
    public Object getServiceProxy(Class<?>[] itfs) {
//        MessageContext mc = ThreadLocalHolder.get();
//        if (null != getAdditionalData(UCConstant.APP_ID_DEV))
//        {
//            mc.getEntities().put(UCConstant.APP_ID_DEV, getAdditionalData(UCConstant.APP_ID_DEV));
//            mc.getEntities().put(UCConstant.PWD_DEV, getAdditionalData(UCConstant.PWD_DEV));
//        }

        if (itfs.length == 1) {
            if (itfs[0].isInstance(device)) {
                return device;
            }
            return device.getService(itfs[0]);
        } else {
            LOGGER.debug("Intefaces number is not 1");
            return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    itfs, device.getService(itfs));
        }
    }

    /**
     * 保活一次更新DB设备状态
     * 保活失败：尝试登陆 更新DB设备状态，更新控制器IP
     */
    @Override
    public boolean doHeartbeat(String connId) {
    	/*VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(getAdditionalData("deviceId")+"");
     
        MessageContext mc = ThreadLocalHolder.get();
        try
        {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "server/loginInfo", "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");
            
            if(0 != res.getErrorCode())
            {
            	LOGGER.error("doHeartbeat failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode());
            	 mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            	return false;
            }
            
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "0");
            
            return true;
        }
        catch (ProtocolAdapterException e)
        {
            if (ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR == e.getErrorCode())
            {
                setAdditionalData("networkErrorFlag", "Y");
            }
            LOGGER.error("", e);
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            return false;
        }*/
        ResBean doHeartbeat = doHeartbeat();
        if (0 != doHeartbeat.getErrorCode()) {
            return false;
        } else {
            return true;
        }

    }

    public ResBean doHeartbeat() {
        ResBean res = new ResBean();
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(getAdditionalData("deviceId") + "");

        MessageContext mc = ThreadLocalHolder.get();
        try {
            res = (ResBean) protocolAdapter.syncSendMessage(req, "server/loginInfo", "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            if (0 != res.getErrorCode()) {
                LOGGER.error("doHeartbeat failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode());
                mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            }

            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "0");
        } catch (ProtocolAdapterException e) {
            if (ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR == e.getErrorCode()) {
                setAdditionalData("networkErrorFlag", "Y");
            }
            LOGGER.error("doHeartbeat failed! ", e);
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            res.setErrorCode(e.getErrorCode());
            res.setDescription(e.getMessage());
        }

        return res;
    }


    @Override
    public boolean initConn(String connId) {
        LOGGER.info("VASADevice and VASAConnection are initializing.");
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(getAdditionalData("deviceId") + "");
        String arrayId = getAdditionalData("deviceId") + "";
        DeviceUserAuthReqBean reqPayload = new DeviceUserAuthReqBean();
        reqPayload.setUsername(loginUser);
        reqPayload.setPassword(loginPassword);
        //reqPayload.setPassword("Admin@storage");
        reqPayload.setScope("0");
        LOGGER.debug("login device loginUser=" + loginUser + ",loginPassword=******");
        req.setPayload(reqPayload);

        LOGGER.info("=====initconn arrayId=====" + arrayId);
        if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage.toString())) {
            LOGGER.info("=====Device Coming====");
            return false;
        }
        LOGGER.info("=====mytest=====" + arrayId);
        MessageContext mc = ThreadLocalHolder.get();
        try {

            if (StringUtils.isEmpty(arrayId) || checkArrayIsLock(arrayId)) {
                LOGGER.error("The arrayId = " + arrayId + " is lock, do not init connection.");
                return false;
            }
            //add fs
            if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage.toString())) {
                return true;
            }
            String devId = getArrayDeviceId(arrayId);
            Map<String, DArrayIsLock> arrayIsLockInfo = dataUtil.getArrayIsLock();
            DArrayIsLock arrayIsLock = arrayIsLockInfo.get(devId);

            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "xxxxx/sessions", "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            if (1077949061 == res.getErrorCode() || 1077987870 == res.getErrorCode() || -401 == res.getErrorCode()) {
                LOGGER.error("login failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode() + " start update strage lock status turn lock");
                arrayIsLock.setIsLock(true);
                arrayIsLockInfo.put(devId, arrayIsLock);
                dataUtil.setArrayIsLock(arrayIsLockInfo);
                mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
                return false;
            }

            if (0 != res.getErrorCode()) {
                LOGGER.error("login failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode());
                mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
                return false;
            }

            DeviceUserAuthResBean authRes = (DeviceUserAuthResBean) res.getResData();
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "0");

            VASASession.setToken(authRes.getDeviceid(), authRes.getiBaseToken());
            //RestfulAdapterImplForVasa adapterImplForVasa = (RestfulAdapterImplForVasa) protocolAdapter;
            //VASASession.setCookie(authRes.getDeviceid(), adapterImplForVasa.getHttpClient().getCookieStore());
            //更新Capability中的protocolAdapter
            VASAJsonOverHttpCustProvider provider = (VASAJsonOverHttpCustProvider) protocolAdapter.getSdkProtocolAdatperCustProvider();
            AccountInfo accountInfo = provider.getAccountInfo();
            accountInfo.setDevId(authRes.getDeviceid());
            accountInfo.setUserId(loginUser);
            accountInfo.setPassword(loginPassword);
            //更新adapter
//    		RestfulAdapterImplForVasa newAdapter = new RestfulAdapterImplForVasa(vasaDevice.getSap());
//    		newAdapter.setSdkProtocolAdatperCustProvider(new VASAJsonOverHttpCustProviderExt(vasaDevice.getDeviceId()));
//    		vasaDevice.updateCapabilityProtocolAdapter(newAdapter);
            keepAliveInteval = 60;

            return true;
        } catch (ProtocolAdapterException e) {
            if (ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR == e.getErrorCode()) {
                setAdditionalData("networkErrorFlag", "Y");
            }
            LOGGER.error("", e);
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            return false;
        }


    }

    @Override
    public void destroyConn(String connId) {
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        String arrayId = getAdditionalData("deviceId") + "";
        req.setArrayId(arrayId);
        LOGGER.info("Beging destroy array connection. ArrayId=" + arrayId);
        MessageContext mc = ThreadLocalHolder.get();
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "sessions", "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            if (0 != res.getErrorCode()) {
                LOGGER.error("destroyConn failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode());
            }

            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            LOGGER.info("Destroy array connection success. ArrayId=" + arrayId);
        } catch (ProtocolAdapterException e) {
            LOGGER.info("Destroy array connection error. ArrayId=" + arrayId + ",Exception=" + e.getMessage());
            if (ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR == e.getErrorCode()) {
                setAdditionalData("networkErrorFlag", "Y");
            }
            LOGGER.error("", e);
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
        }
    }

    @Override
    public int getKeepAliveTimes() {
        return 0;
    }

    @Override
    public int getKeepAlivePeriod() {
        return keepAliveInteval;
    }

    @Override
    public Date getStartTime() {
        return new Date(System.currentTimeMillis() + keepAliveInteval * 1000);
    }

    protected Date getUTCDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return new Date(cal.getTimeInMillis());
    }

    public String getArrayDeviceId(String arrayId) {
        String devId = null;
        if (StringUtils.isEmpty(arrayId)) {
            LOGGER.error("getArrayDeviceId, the arrayId null");
            return null;
        }
        QueryDBResponse response = new QueryDBResponse();
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        StorageManagerResource storageManagerResource = new StorageManagerResource();
        response = storageManagerResource.queryData();
        list = response.getAllStorageInfo();
        for (StorageInfo storageInfo : list) {
            if (arrayId.equalsIgnoreCase(storageInfo.getId().trim())) {
                devId = storageInfo.getSn();
            }
        }
        return devId;
    }

    public boolean checkArrayIsLock(String arrayId) {
        //true is lock
        //false is not lock
        if (StringUtils.isEmpty(arrayId)) {
            LOGGER.error("check Array is lock, the arrayId null");
            return false;
        }
        String devId = getArrayDeviceId(arrayId);
        if (StringUtils.isEmpty(devId)) {
            LOGGER.error("In checkArrayIsLock function, the deviceId is null");
            return false;
        }
        Map<String, DArrayIsLock> arrayIsLockInfo = dataUtil.getArrayIsLock();
        DArrayIsLock arrayIsLock = arrayIsLockInfo.get(devId);

        if (null == arrayIsLock) {
            discoverService.addStorageArrayIsLockInfo(devId, false);
            arrayIsLockInfo = dataUtil.getArrayIsLock();
            arrayIsLock = arrayIsLockInfo.get(devId);
        }

        if (arrayIsLock.getIsLock()) {
            LOGGER.info("The arrayId = " + arrayId + " is lock");
            return true;
        }
        return false;
    }
}
