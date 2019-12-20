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

import java.util.List;

import org.opensds.vasa.vasa.db.model.VvolPath;

public interface VvolPathDao {
    //根据文件系统名字查询所有记录
    List<VvolPath> queryAllRecordByFileSystem(String fileSystemName);

    //插入一条新的记录
    void insertRecord(VvolPath record);

    //更新sharePath
    void updateSharePath(VvolPath newSharePath);

    VvolPath getVvolPathByVvolId(String vvolId);

    void updateVvolId(VvolPath vvolPath);

    void deleteVvolPathByVvolId(String vvolId);

    List<VvolPath> getVvolPathByFuzzySystemName(String fileSystemName);

    boolean isBindState(String vvolId);

    void setBindState(VvolPath vvolPath);
}
