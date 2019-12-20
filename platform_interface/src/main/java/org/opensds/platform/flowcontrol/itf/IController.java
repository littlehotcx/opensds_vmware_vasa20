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

package org.opensds.platform.flowcontrol.itf;

public interface IController
{
    // 注册登记接口，Monitor和Performer通过此接口向Controller注册，并建立通道--暂不使用，采用spring注入方式注册
    void register();

    // 手动通知开启或关闭流控---特殊场合需要实行对所有消息的流控 
    public void notifyFlowControlByNeed(boolean fcSwitch);
}
