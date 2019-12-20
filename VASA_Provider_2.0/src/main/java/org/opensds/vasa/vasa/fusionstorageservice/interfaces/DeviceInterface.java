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

package org.opensds.vasa.vasa.fusionstorageservice.interfaces;

import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DLun;

import java.util.List;
import java.util.Map;

/**
 * fusionStorage Infomation
 */
public interface DeviceInterface {

    void initDevice(String userName, String pwd, String scope, String ip, int port) throws Exception;

    List<DLun> getStorageLuns(String arrayId, String[] hostInitiatorIds);

    List<DArray> queryArrays();

    void updateDeviceInfo() throws Exception;

    Map<String, Object> getDeviceInfo();

    Map<String, String> getStorageSystemInfo() throws Exception;

    String getDeviceModel() throws Exception;

    Object getDeviceReference();

    String toString();
}
