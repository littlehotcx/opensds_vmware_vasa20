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

package org.opensds.platform.usermgr.itf;

import java.util.List;

import org.opensds.platform.common.bean.config.UserConfig;

public interface IUserManager
{	
    /**
     * 获取所有用户列表
     * 
     * @return 所有用户列表
     */
    List<UserConfig> getUserList();
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserConfig getUserById(String userId);
    
    /**
     * 刷新用户，重新从文件中载入
     */
    void refreshUsers();
    
    /**
     * 检查用户名是否合法
     * 
     * @param userId 用户名
     * @param password 用户密码
     * @return true如果用户名密码正确否则返回false
     */
    boolean checkUser(String userId, String password);
}
