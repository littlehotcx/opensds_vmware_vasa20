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

package org.opensds.vasa.vasa20.device.array.login;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.array.db.model.NStorageArrayBean;
import org.opensds.vasa.vasa20.device.array.db.service.StorageArrayService;

public class DeviceSwitchService {

    private static final Logger LOGGER = LogManager.getLogger(DeviceSwitchService.class);

    private StorageArrayService storageArrayService = ApplicationContextUtil.getBean("storageArrayService");

    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();

    public List<DeviceSwitchMetaData> queryAllControllerIPS(String arrayId) {
        List<DeviceSwitchMetaData> list = new ArrayList<DeviceSwitchMetaData>();
        try {
            NStorageArrayBean array = storageArrayService.getArrayControllIPS(arrayId);
            LOGGER.info("queryAllControllerIPS arrayId=" + arrayId + " " + array);
            String[] ips = array.getIps().split(",");
            for (String ip : ips) {
                list.add(new DeviceSwitchMetaData(ip, array.getPort(), array.getUname(), ArrayPwdAES128Util.decryptPwd(array.getUname(), array.getUpass())));
            }
        } catch (SDKException e) {
            LOGGER.error("queryAllControllerIPS error . ", e);
        }

        return list;
    }

    public void updateAvilableController(String arrayId, DeviceSwitchMetaData avilableController) {
        try {
            if (avilableController == null) {
                LOGGER.info("set array offline,arrayId=" + arrayId);
                storageArrayService.updateArrayControllerIP(arrayId, "", "OFFLINE");
            } else {
                storageArrayService.updateArrayControllerIP(arrayId, avilableController.getIp(), "ONLINE");
            }
        } catch (SDKException e) {
            LOGGER.error("updateAvilableController error . ", e);
        }
    }

    public DeviceSwitchMetaData check2SwitchController(List<DeviceSwitchMetaData> connectMetaDatas) {
        LOGGER.info("check2SwitchController start , controller size :" + connectMetaDatas.size());

        ExecutorService exec = Executors.newFixedThreadPool(connectMetaDatas.size());

        CompletionService<DeviceSwitchMetaData> service = new ExecutorCompletionService<DeviceSwitchMetaData>(exec);

        try {

            for (int i = 0; i < connectMetaDatas.size(); i++) {
                LOGGER.info("check2SwitchController connectMetaDatas=" + connectMetaDatas);
                CheckControllerTask task = new CheckControllerTask(connectMetaDatas.get(i));
                LOGGER.info("ExecutorService thread pool sumbit task : " + connectMetaDatas.get(i));
                service.submit(task);
            }

            for (int i = 0; i < connectMetaDatas.size(); i++) {
                DeviceSwitchMetaData resultTask = service.take().get();
                LOGGER.info("CompletionService get finshed task : " + resultTask);
                if (resultTask.isAvilable()) {
                    return resultTask;
                }
            }

        } catch (InterruptedException e) {
            LOGGER.error("checkController InterruptedException ", e);
        } catch (ExecutionException e) {
            LOGGER.error("checkController ExecutionException ", e);
        } finally {
            exec.shutdownNow();
        }

        return null;
    }

    public synchronized String doCheck2Switch(VASARestReqMessage restReq) {
        // query db get all controller ips
        List<DeviceSwitchMetaData> allController = queryAllControllerIPS(restReq.getArrayId());

        // test login with each ip and return the fast login ip
        DeviceSwitchMetaData avilableController = check2SwitchController(allController);
        if (null == restReq.getSwitchControlCount()) {
            restReq.setSwitchControlCount(allController.size() - 1);
        }
        LOGGER.info("doCheck2Switch avilableController=" + avilableController);
        // update db array's ip otherwise set array status offline
        updateAvilableController(restReq.getArrayId(), avilableController);

        return avilableController == null ? null : avilableController.getServerUrl();
    }

    class CheckControllerTask implements Callable<DeviceSwitchMetaData> {

        private DeviceSwitchMetaData connectMetaData;

        public CheckControllerTask(DeviceSwitchMetaData connectMetaData) {

            this.connectMetaData = connectMetaData;
        }

        @Override
        public DeviceSwitchMetaData call() {

            DeviceSwitchUtil utilInstance = new DeviceSwitchUtil(connectMetaData.getServerUrl());

            if (utilInstance.loginDevice(connectMetaData.getUname(), connectMetaData.getUpass())) {

                connectMetaData.setAvilable(true);

                utilInstance.logoutDevice();
            }

            return connectMetaData;

        }

    }
}
