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

package org.opensds.vasa.vasa.db.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.DateUtil;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.dao.FaultDataManagerDao;
import org.opensds.vasa.vasa.db.model.FaultData;
import org.opensds.vasa.vasa.db.model.FaultDataMetaData;
import org.opensds.vasa.vasa.db.service.FaultDataManagerService;
import org.opensds.vasa.vasa.db.service.VirtualMachineService;

public class FaultDataManagerServiceImpl implements FaultDataManagerService {
    private FaultDataManagerDao faultDataManagerDao;
    private Logger LOGGER = LogManager.getLogger(FaultDataManagerServiceImpl.class);
    private VirtualMachineService virtualMachineService = (VirtualMachineService) ApplicationContextUtil.getBean("virtualMachineService");

    @Override
    public List<FaultData> queryFaultData(Map<String, Object> map) {
        List<FaultData> result = faultDataManagerDao.queryFaultData(map);
        LOGGER.info("The queryFaultData is : " + result.toString());
        if (null != result) {
            for (FaultData data : result) {
                String utcTime = data.getVvolCreateTime();
                String localTime = null;
                try {
                    localTime = DateUtil.utcDateStrToLocal(utcTime);
                } catch (ParseException e) {
                    //如果解析失败，程序继续运行，不抛异常，只是显示在页面上的时间为空
                    LOGGER.error("Parse date string error.", e);
                }
                data.setVvolCreateTime(localTime);
                List<FaultDataMetaData> metaDatas = faultDataManagerDao.queryFaultDataMetaData(data.getVvolId());
                LOGGER.info("metaDatas =" + metaDatas);
                for (FaultDataMetaData faultDataMetaData : metaDatas) {
                    if ("VMW_VVolName".equalsIgnoreCase(faultDataMetaData.getKey())) {
                        data.setVvolName(faultDataMetaData.getValue());
                    } else if ("VMW_VvolProfile".equalsIgnoreCase(faultDataMetaData.getKey())) {
                        data.setVvolProfileId(faultDataMetaData.getValue());
                    }
                }
                //如果vmId为空，则不应该显示vmName，防止前端根据vmId自动合并
                if (data.getVmId() == null || "".equals(data.getVmId())) {
                    data.setVmName("");
                }
                data.setMetaDatas(metaDatas);
            }
        }
        return result;
    }

    @Override
    public int queryFaultDataCount(Map<String, Object> map) {
        return faultDataManagerDao.queryFaultDataCount(map);
    }

    @Override
    public void delFaultData(String vvolId) {
        faultDataManagerDao.delFaultData(vvolId);
    }

    public FaultDataManagerDao getFaultDataManagerDao() {
        return faultDataManagerDao;
    }

    public void setFaultDataManagerDao(FaultDataManagerDao faultDataManagerDao) {
        this.faultDataManagerDao = faultDataManagerDao;
    }

}
