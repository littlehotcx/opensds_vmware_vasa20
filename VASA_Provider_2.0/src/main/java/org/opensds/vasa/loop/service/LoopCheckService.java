
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

package org.opensds.vasa.loop.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vmware.vim.vasa.v20.StorageFault;


public class LoopCheckService {

    private static Logger logger = LogManager.getLogger(LoopCheckService.class);

    public static void check(ILoopCheck loopCheck, Long intervalTime, Long timeout) throws StorageFault {

        long beginTimeMillis = System.currentTimeMillis();
        long currentTimeMillis = System.currentTimeMillis();

        while ((currentTimeMillis - beginTimeMillis) >= timeout) {

            boolean complete = loopCheck.isComplete();
            if (!complete) {
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    logger.error("loop worker error", e);
                }
            }
            currentTimeMillis = System.currentTimeMillis();
        }


    }

}
