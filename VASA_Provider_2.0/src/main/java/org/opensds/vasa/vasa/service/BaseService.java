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

package org.opensds.vasa.vasa.service;

import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.usermgr.itf.IUserManager;

public class BaseService {
    protected IUserManager userManager = (IUserManager) ApplicationContextUtil.getBean("userManager");

    public boolean checkUser(String userId, String password) {
        return userManager.checkUser(userId, password);
    }
}
