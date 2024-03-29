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

import java.util.List;
import java.util.Map;

import org.opensds.vasa.vasa.db.model.NTaskInfo;

import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;

public interface TaskInfoService extends BaseService<NTaskInfo> {

    public void saveTaskInfo(TaskInfo taskInfo, Map<String, String> extProperties);

    public NTaskInfo getTaskInfoByTaskId(String taskId);

    public void updateTaskInfoByTaskId(TaskInfo taskInfo, Map<String, String> extProperties);

    public List<NTaskInfo> getRunningTaskByArrayId(String arrayId);
}
