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

package org.opensds.vasa.vasa20.device;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 *
 * 陈明军 chenmingjun c90005775 要求VASA Provider需要限制每秒下发的请求数
 *
 *
 */
public class RestQosControl {
    private static final Logger LOGGER = LogManager.getLogger(RestQosControl.class);

    private Long[] accessWindow;

    private int limit;

    private int curPosition;

    private long period;

    private final Object lock = new Object();

    public RestQosControl(int limit, int period, TimeUnit timeUnit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + limit);
        }
        curPosition = 0;
        this.period = timeUnit.toMillis(period);
        this.limit = limit;
        accessWindow = new Long[limit];
        Arrays.fill(accessWindow, 0L);
    }

    public boolean isPass() {
        long curTime = System.currentTimeMillis();
        synchronized (lock) {
            if (curTime >= period + accessWindow[curPosition]) {
                accessWindow[curPosition++] = curTime;
                curPosition = curPosition % limit;
                return true;
            } else {
                LOGGER.error("can not send request");
                return false;
            }
        }
    }
}