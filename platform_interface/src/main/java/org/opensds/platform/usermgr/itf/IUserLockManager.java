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

import java.util.Date;

public interface IUserLockManager
{
    boolean lockUser(String userId);
    
    boolean unlockUser(String userId);
    
    boolean tryUnlockUser(String userId);
    
    boolean isUserLocked(String userId);
    
    int getRetryTimes(String userId);
    
    void increaseRetryTimes(String userId);
    
    void resetRetryTimes(String userId);
    
    void setRetryTime(String userId, Date date);
    
    boolean reachMaxRetryTimes(String userId);
}
