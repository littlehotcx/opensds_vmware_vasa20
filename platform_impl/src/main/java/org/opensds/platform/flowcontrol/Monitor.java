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

package org.opensds.platform.flowcontrol;

import java.util.concurrent.atomic.AtomicLong;

import org.opensds.platform.flowcontrol.itf.IMonitor;

public class Monitor implements IMonitor
{
    // 监控周期内SOAP接口访问次数
    private static AtomicLong visitCnt = new AtomicLong();;

    @Override
    public void reportStatus(int msgVisitCnt) //报告当前消息状态
    {
        // 调用次数+msgVisitCnt
        long visitAccount = visitCnt.addAndGet(msgVisitCnt);
        visitCnt.set(visitAccount);
        return;
    }

    @Override
    public long getStatus() //获取当前消息状态
    {
        long visitAccount = visitCnt.get();
        visitCnt.set(0);
        return visitAccount;
    }
}
