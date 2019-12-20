/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.config.dao.itf;

import java.util.List;

import org.opensds.platform.common.bean.config.AppMappingConfig;


/**
 * 设备和eSDK用户的一一映射
 * @author sWX198756
 * @since  eSDK Platform V100R003C10
 */
public interface IAppMappingDAO
{
    boolean addAppMapping(AppMappingConfig appMapping);

    boolean updateAppMapping(AppMappingConfig appMapping);

    boolean deleteAppMapping(String deviceId, String esdkApp);

    AppMappingConfig getAppMappingByESDKApp(String esdkApp);

    List<AppMappingConfig> getAllAppMappings();
}
