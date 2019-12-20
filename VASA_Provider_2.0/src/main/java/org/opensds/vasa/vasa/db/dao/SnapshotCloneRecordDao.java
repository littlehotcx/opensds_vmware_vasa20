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

package org.opensds.vasa.vasa.db.dao;

import java.util.Date;
import java.util.Map;

import org.opensds.vasa.vasa.db.model.snapshotCloneRecord;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-22
 */
public interface SnapshotCloneRecordDao {

    int getUnfinishedCount(Map<String, String> paras);

    int getRemainCount(Map<String, String> paras);

    String getInputName(Map<String, String> paras);

    void initRecord(snapshotCloneRecord snapshotCloneRecord);

    void addRecord(Map<String, String> paras);

    void deleteRecord(Map<String, String> paras);

    void deleteTimeoutRecord(Date date);

    void deleteFinishedRecord(Date date);
}
