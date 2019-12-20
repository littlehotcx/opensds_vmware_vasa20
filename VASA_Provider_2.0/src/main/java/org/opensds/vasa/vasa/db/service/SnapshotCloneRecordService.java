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

package org.opensds.vasa.vasa.db.service;

import com.vmware.vim.vasa.v20.StorageFault;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-23
 */
public interface SnapshotCloneRecordService {

    class Result {
        public String result;
        public String inputName;
        public String id;
    }

    public static String SNAPSHOT = "snapshot";

    public static String CLONE = "clone";

    public static String MIGRATE = "migrate";

    public static String FINISHED = "Finished";

    public static String IN_PROGRESS = "progress";

    public static String MIGRATE_ONE_BY_ONE = "migrateOneByOne";

    boolean checkIfExist(String vmId, String operationType) throws StorageFault;//返回是否已存在当前操作未完成的记录

    void initRecord(String vmId, String operationType, int diskCount, String inputName) throws StorageFault;//初始化

    void deleteRecord(String vmId, String operationType, String inputName) throws StorageFault;

    Result addRecord(String vmId, String operationType) throws StorageFault;//添加一条记录，成功返回名称,失败返回已完成

    String getInput(String vmId, String operationType) throws StorageFault;

    void deleteTimeoutRecord();

    void deleteFinishedRecord();

}
