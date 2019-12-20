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

import org.opensds.vasa.vasa.db.dao.SnapshotCloneRecordDao;
import org.opensds.vasa.vasa.db.model.snapshotCloneRecord;
import org.opensds.vasa.vasa.db.service.SnapshotCloneRecordService;
import org.opensds.vasa.vasa.util.FaultUtil;

import com.vmware.vim.vasa.v20.StorageFault;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-23
 */
public class SnapshotCloneRecordServiceImpl implements SnapshotCloneRecordService {


    private static Logger LOGGER = LogManager
            .getLogger(SnapshotCloneRecordServiceImpl.class);

    private SnapshotCloneRecordDao snapshotCloneRecordDao;

    public SnapshotCloneRecordDao getSnapshotCloneRecordDao() {
        return snapshotCloneRecordDao;
    }

    public void setSnapshotCloneRecordDao(SnapshotCloneRecordDao snapshotCloneRecordDao) {
        this.snapshotCloneRecordDao = snapshotCloneRecordDao;
    }

    private Map<String, String> buildDbPara(String vmId, String operationType) {
        Map<String, String> para = new HashMap<String, String>();
        para.put("vmId", vmId);
        para.put("operationType", operationType);
        return para;
    }

    @Override
    public void deleteRecord(String vmId, String operationType, String inputName) throws StorageFault {
        Map<String, String> paras = new HashMap<String, String>();
        paras.put("vmId", vmId);
        paras.put("operationType", operationType);
        paras.put("inputName", inputName);
        snapshotCloneRecordDao.deleteRecord(paras);
    }

    @Override
    public boolean checkIfExist(String vmId, String operationType) throws StorageFault {

        boolean isExist = false;
        if (vmId == null || vmId.length() == 0) {
            LOGGER.error("check vmID invalid ");
            throw FaultUtil.storageFault("check vmID invalid");
        }
        if (operationType == null || (!operationType.equals(SNAPSHOT) && !operationType.equals(CLONE) &&
                !operationType.equals(MIGRATE) && !operationType.equals(MIGRATE_ONE_BY_ONE))) {
            LOGGER.error("check operationType invalid ");
            throw FaultUtil.storageFault("check operationType invalid");
        }
        try {

            int count = snapshotCloneRecordDao.getUnfinishedCount(buildDbPara(vmId, operationType));
            if (count > 0) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error("operate db error" + e);
            throw FaultUtil.storageFault("operate db error" + e);
        }
        return isExist;
    }


    @Override
    public void initRecord(String vmId, String operationType, int diskCount, String inputName) throws StorageFault {

        if (vmId == null || vmId.length() == 0) {
            LOGGER.error("check vmID invalid ");
            throw FaultUtil.storageFault("check vmID invalid");
        }
        if (operationType == null || (!operationType.equals(SNAPSHOT) && !operationType.equals(CLONE) &&
                !operationType.equals(MIGRATE) && !operationType.equals(MIGRATE_ONE_BY_ONE))) {
            LOGGER.error("check operationType invalid ");
            throw FaultUtil.storageFault("check operationType invalid");
        }
        if (diskCount < 0) {
            LOGGER.error("check diskCount invalid ");
            throw FaultUtil.storageFault("check diskCount invalid");
        }
        snapshotCloneRecord snapshotCloneRecord = new snapshotCloneRecord();
        snapshotCloneRecord.setVmId(vmId);
        snapshotCloneRecord.setOperationType(operationType);
        snapshotCloneRecord.setDiskCount(diskCount);
        snapshotCloneRecord.setDiskRemain(diskCount);
        snapshotCloneRecord.setStartTime(new Date());
        snapshotCloneRecord.setEndTime(new Date());
        snapshotCloneRecord.setInputName(inputName);
        try {
            snapshotCloneRecordDao.initRecord(snapshotCloneRecord);
        } catch (Exception e) {
            LOGGER.error("operate db error" + e);
            throw FaultUtil.storageFault("operate db error" + e);
        }

    }

    @Override
    public String getInput(String vmId, String operationType) throws StorageFault {
        return snapshotCloneRecordDao.getInputName(buildDbPara(vmId, operationType));
    }

    @Override
    public void deleteTimeoutRecord() {
        //删除超过一天的未完成的卷

        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);

        snapshotCloneRecordDao.deleteTimeoutRecord(date.getTime());
    }

    @Override
    public void deleteFinishedRecord() {
        //删除超过一天的已完成的卷
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);

        snapshotCloneRecordDao.deleteFinishedRecord(date.getTime());
    }

    @Override
    public synchronized Result addRecord(String vmId, String operationType) throws StorageFault {

        String result = "failed";
        Result ret = new Result();
        ret.result = "failed";
        if (vmId == null || vmId.length() == 0) {
            LOGGER.error("check vmID invalid ");
            throw FaultUtil.storageFault("check vmID invalid");
        }
        if (operationType == null || (!operationType.equals(SNAPSHOT) && !operationType.equals(CLONE) &&
                !operationType.equals(MIGRATE) && !operationType.equals(MIGRATE_ONE_BY_ONE))) {
            LOGGER.error("check operationType invalid ");
            throw FaultUtil.storageFault("check operationType invalid");
        }

        try {

            String inputName = snapshotCloneRecordDao.getInputName(buildDbPara(vmId, operationType));
            snapshotCloneRecordDao.addRecord(buildDbPara(vmId, operationType));
            int unfinishedCount = snapshotCloneRecordDao.getRemainCount(buildDbPara(vmId, operationType));
            ret.inputName = inputName;
            if (unfinishedCount >= 1) {
                LOGGER.info("add record : query unfinished count = " + unfinishedCount);
                ret.result = IN_PROGRESS;
                return ret;
            } else {
                ret.result = FINISHED;
                return ret;
            }
        } catch (Exception e) {
            LOGGER.error("operate db error" + e);
            throw FaultUtil.storageFault("operate db error" + e);
        }
    }
}
